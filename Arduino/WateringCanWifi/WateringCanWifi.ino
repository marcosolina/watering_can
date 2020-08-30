#include <SPI.h>
#include <WiFiNINA.h>
#include <WiFiUdp.h>

#define DEBUG 0


/*############################################
   NETWORK PROPERTIES - START
  ############################################*/
#include "arduino_secrets.h"
/*
   please enter your sensitive data in the Secret arduino_secrets.h
*/
char ssid[] = SECRET_SSID;// your network SSID (name)
char pass[] = SECRET_PASS;// your network password (use for WPA, or use as key for WEP)
byte mac[6];

unsigned long timeOutDhcp = 60000;// 1 minute
unsigned long timeOutDhcpOld = 0;

#define TCP_LISTENING_PORT 85
WiFiServer tcpServer(TCP_LISTENING_PORT);
WiFiClient tcpClient;
boolean iHaveAnIpAddress = false;

#define UDP_LISTENING_PORT 90
WiFiUDP udpServer;

boolean iHaveRaspIp = false;
byte ipRasp[4];
int RASP_PORT = 0;

/*############################################
   NETWORK PROPERTIES - END
  ############################################*/

/*############################################
   MOISTURE SENSORs PROPERTIES - START
  ############################################*/

/*
   I have to manually calibrate the sensors. Unfortunately every sensor requires its own
   test. I will provide here some default values
*/
const int dry = 1023;// value when I leave it on the table
const int wet = 704;// value when I leave it in a glass of water

/*############################################
  MOISTURE SENSORs PROPERTIES - END
  ############################################*/


/*############################################
   PUMPs PROPERTIES - START
  ############################################*/

const int connectedPumps = 4;//number of connected pumps
const int pumpPinColumn = 0;
const int pumpStatusColumn = 1;
const int moisturePinColumn = 2;
const int moistureValueColumn = 3;
const int moistureMinValueRegisteredColumn = 4;
const int moistureMaxValueRegisteredColumn = 5;
const int moistureWetColumn = 6;
const int moistureDryColumn = 7;
const int moistureCurReadColumn = 8;

/*
   This array contains the following info about the pumps:
   [0] -> Pump Pin
   [1] -> Current status on/off
   [2] -> Moisture Pin
   [3] -> Moisture Value %
   [4] -> Min Moisture Value readed
   [5] -> Max Moisture Value readed
   [6] -> Wet Moisture Value set
   [7] -> Dry Moisture Value set
   [7] -> Current Moisture Value read
*/
int pumps[connectedPumps][9] = {
  {2, 0, A5, 0, 2000, 0, 0 , 0, 0},
  {3, 0, A4, 0, 2000, 0, 0 , 0, 0},
  {4, 0, A3, 0, 2000, 0, 0 , 0, 0},
  {5, 0, A2, 0, 2000, 0, 0 , 0, 0}
}
;

/*############################################
   PUMPs PROPERTIES - START
  ############################################*/


void setup() {
  //Initialize serial and wait for port to open:
#if DEBUG == 1
  Serial.begin(9600);
  while (!Serial) {
    ; // wait for serial port to connect. Needed for native USB port only
  }
  Serial.println("Ok serial");
#endif

  for (int i = 0; i < connectedPumps; i++) {
    pinMode(pumps[i][pumpPinColumn], OUTPUT);
    pumps[i][pumpStatusColumn] = HIGH;
  }

  applyOutputs();
}

void loop() {
  connectToWifi();
  discoverTheRasp();
  receiveACommand();
}


/* #################################
   START WIFI Stuff
  ##################################*/
boolean connectToWifi() {

  /*
     Nothing to do if I am already connected...
  */
  if (iHaveAnIpAddress) {
    return true;
  }

  /*
     Don't try to often...
  */
  if (timeOutDhcpOld != 0 && (millis() - timeOutDhcpOld) < timeOutDhcp) {
    return false;
  }

  timeOutDhcpOld = millis();

  // check for the WiFi module:
  if (WiFi.status() == WL_NO_MODULE) {
#if DEBUG == 1
    Serial.println("Communication with WiFi module failed!");
    // don't continue
#endif
    while (true);
  }


#if DEBUG == 1
  String fv = WiFi.firmwareVersion();
  if (fv < WIFI_FIRMWARE_LATEST_VERSION) {
    Serial.println("Please upgrade the firmware");
  }
#endif

  // attempt to connect to Wifi network:
  int attempts = 0;
  int status = WL_IDLE_STATUS;     // the Wifi radio's status
  while (status != WL_CONNECTED) {
    if (attempts > 3) {
      return false;
    }
#if DEBUG == 1
    Serial.print("Attempting to connect to WPA SSID: ");
    Serial.println(ssid);
#endif
    // Connect to WPA/WPA2 network:
    status = WiFi.begin(ssid, pass);

    attempts++;
  }

#if DEBUG == 1
  // you're connected now, so print out the data:
  Serial.print("You're connected to the network");

  printCurrentNet();
  printWifiData();
#endif

  /*
     Store the mac addres
  */
  WiFi.macAddress(mac);
  /*
     If connected start to listen for UDP packets (Rasp discovery)
     and for incoming request (Rasp commands)
  */
  udpServer.begin(UDP_LISTENING_PORT);
  tcpServer.begin();
  iHaveAnIpAddress = true;
  return true;
}

void disconnectWifi() {
  if (WiFi.status() == WL_CONNECTED) {
#if DEBUG == 1
    Serial.println("Disconnecting WiFi");
#endif
    WiFi.disconnect();

#if DEBUG == 1
    Serial.println("WiFi disconnected");
#endif
  }
}

void discoverTheRasp() {
  /*
     If I don't have an IP it means that I am not connected,
     hence I am not able to find the Rasp
  */
  if (!iHaveAnIpAddress)
    return;

  /*
     No need to continue if I already have the Rasp IP
  */
  if (iHaveRaspIp)
    return;

  int dataReceived = udpServer.parsePacket();
  if (dataReceived) {
    char packetBuffer[UDP_TX_PACKET_MAX_SIZE];
    udpServer.read(packetBuffer, UDP_TX_PACKET_MAX_SIZE);

    /*
      The Rasp will send a message like: WCAN8080X.
      I check for the string "WCAN" to understand that it
      is a message coming from the Rasp, and I will read the rest
      of the string until the "X" to understand on which port the SpringBoot app
      is running
    */
    if (packetBuffer[0] == 'W' && packetBuffer[1] == 'C' && packetBuffer[2] == 'A' && packetBuffer[3] == 'N') {

      IPAddress ip = udpServer.remoteIP();
      ipRasp[0] = ip[0];
      ipRasp[1] = ip[1];
      ipRasp[2] = ip[2];
      ipRasp[3] = ip[3];

      /*
         Let's check for the port number
      */
      String serverPort = "";
      for (int i = 4; i < 15; i++) {
        if (packetBuffer[i] == 'X') {
          break;
        }
        serverPort += packetBuffer[i];
      }

      /*
         If not available try to default
      */
      if (serverPort.length() == 0) {
        serverPort = "8080";
      }

      /*
         Store the infor
      */
      RASP_PORT = serverPort.toInt();

#if DEBUG == 1
      Serial.println("I've got the SpringBoot app IP");
      Serial.print("The app is running on port:");
      Serial.println(RASP_PORT);
#endif

      /*
         I have the rasp IP and the port. Let me register with the app
      */
      if (tcpClient.connect(ipRasp, RASP_PORT)) {
        iHaveRaspIp = true;
        udpServer.stop();
        tcpClient.print("GET /WateringCan/Arduino/registration?MAC=");

        for (int i = 5; i >= 0; i--) {
          if (mac[i] < 16) {
            tcpClient.print("0");
          }
          tcpClient.print(String(mac[i], HEX));
          if (i > 0) {
            tcpClient.print(":");
          }
        }

        tcpClient.print(" HTTP/1.1\r\n");
        tcpClient.print("Host: ");
        tcpClient.print(ipRasp[0]);
        tcpClient.print(".");
        tcpClient.print(ipRasp[1]);
        tcpClient.print(".");
        tcpClient.print(ipRasp[2]);
        tcpClient.print(".");
        tcpClient.print(ipRasp[3]);
        tcpClient.print("\r\n");
        tcpClient.print("Connection: close\r\n");
        tcpClient.print("\r\n");
        delay(10);//give some time to the bytes to be delivered
        tcpClient.stop();
#if DEBUG == 1
        Serial.println("I have sent my ip to the SpringBoot app");
#endif
      }
    }
  }
}

void receiveACommand() {
  /*
     I cannot receive a command if I don't have
     an IP
  */
  if (!iHaveAnIpAddress) {
    return;
  }

  WiFiClient client = tcpServer.available();
  if (client) {

    char command = '0';//default command

    /*
       The first byte that I will receive will tell mi what to do
    */
    if (client.connected() && client.available()) {
      command = client.read();
    }

    String receivedMessage = "";
    switch (command) {
      case '0':
#if DEBUG == 1
        sendOutputStatuses(NULL);
#endif
        readMoisture();
#if DEBUG == 1
        sendOutputStatuses(NULL);
#endif
        sendOutputStatuses(client);
        break;
      case 'E':
        /*
           EpumpNumber-pumpStatus
           Example: E2-1 -> Pump number 2 turn on
        */
        while (client.connected() && client.available()) {
          char c = client.read();
          if (c == '-') {
            int pumpNumber = receivedMessage.toInt();
            if (pumpNumber < connectedPumps) {
              pumps[pumpNumber][pumpStatusColumn] = client.read() == '1' ? HIGH : LOW;
            }
            receivedMessage = "";
          }
          receivedMessage += c;
        }
        applyOutputs();
        client.print("OK");
        break;
      case 'U':
        {
          /*
             EpumpNumber-pumpStatus
             Example: E2-1-100 -> Pump number 2 - dry val - wet val
          */
          int tmpColumn = 0;
          int tmpRow = 1000;
          int tmpDryVal = 0;
          int tmpWetVal = 0;
          while (client.connected() && client.available()) {
            char c = client.read();
            if (c == '-') {
              int intValue = receivedMessage.toInt();
              switch (tmpColumn) {
                case 0:
                  tmpRow = intValue;
                  break;
                case 1:
                  tmpDryVal = intValue;
                  break;
              }
              tmpColumn++;
              receivedMessage = "";
              continue;
            }
            receivedMessage += c;
          }

          /*
             The last number does not have the "-" on the right
          */
          if (tmpColumn == 2 && receivedMessage.length() > 0) {
            tmpWetVal = receivedMessage.toInt();
          }
          if (tmpRow < connectedPumps) {
            pumps[tmpRow][moistureWetColumn] = tmpWetVal;
            pumps[tmpRow][moistureDryColumn] = tmpDryVal;
          }
          client.print("OK");
          break;
        }
      default:
        break;
    }

    client.flush();
    client.stop();
  }
}

void applyOutputs() {
  for (int i = 0; i < connectedPumps; i++) {
    digitalWrite(pumps[i][pumpPinColumn], pumps[i][pumpStatusColumn]);
  }
}

void readMoisture() {
  for (int i = 0; i < connectedPumps; i++) {
    int sensorValue = analogRead(pumps[i][moisturePinColumn]);
    /*
       Storing the min value ever
    */
    if (sensorValue < pumps[i][moistureMinValueRegisteredColumn]) {
      pumps[i][moistureMinValueRegisteredColumn] = sensorValue;
    }

    /*
       Storing the max value ever
    */
    if (sensorValue > pumps[i][moistureMaxValueRegisteredColumn]) {
      pumps[i][moistureMaxValueRegisteredColumn] = sensorValue;
    }

    int w = wet;
    int d = dry;

    /*
       If the user has configured the wet and dry value I will use them, otherwise go for the defaults
    */
    if (pumps[i][moistureWetColumn] + pumps[i][moistureDryColumn] != 0) {
      w = pumps[i][moistureWetColumn];
      d = pumps[i][moistureDryColumn];
    }

    int percentageHumidity = map(sensorValue, w, d, 100, 0);
    pumps[i][moistureValueColumn] = percentageHumidity < 0 ? 0 : percentageHumidity;
    pumps[i][moistureCurReadColumn] = sensorValue;
  }

}


/**
   It sends to the client the output status
   pinId-status-moisturevalue-minValue-maxValue-wet-dry_...
*/
void sendOutputStatuses(WiFiClient client) {
  if (client) {
    for (int i = 0; i < connectedPumps; i++) {
      client.print(i);
      client.print('-');
      client.print(pumps[i][pumpStatusColumn]);
      client.print('-');
      client.print(pumps[i][moistureValueColumn]);
      client.print('-');
      client.print(pumps[i][moistureMinValueRegisteredColumn]);
      client.print('-');
      client.print(pumps[i][moistureMaxValueRegisteredColumn]);
      client.print('-');
      client.print(pumps[i][moistureWetColumn]);
      client.print('-');
      client.print(pumps[i][moistureDryColumn]);
      client.print('-');
      client.print(pumps[i][moistureCurReadColumn]);
      if (i < (connectedPumps - 1)) {
        client.print('_');
      }
    }
  }
#if DEBUG == 1
  for (int i = 0; i < connectedPumps; i++) {
    Serial.print(i);
    Serial.print('-');
    Serial.print(pumps[i][pumpStatusColumn]);
    Serial.print('-');
    Serial.print(pumps[i][moistureValueColumn]);
    Serial.print('-');
    Serial.print(pumps[i][moistureMinValueRegisteredColumn]);
    Serial.print('-');
    Serial.print(pumps[i][moistureMaxValueRegisteredColumn]);
    Serial.print('-');
    Serial.print(pumps[i][moistureWetColumn]);
    Serial.print('-');
    Serial.print(pumps[i][moistureDryColumn]);
    Serial.print('-');
    Serial.println(pumps[i][moistureCurReadColumn]);
  }
#endif
}

/*########################################################
   FOR DEBUG PURPOSE - START
  ########################################################*/
#if DEBUG == 1
void printWifiData() {
  // print your board's IP address:
  IPAddress ip = WiFi.localIP();
  Serial.print("IP Address: ");
  Serial.println(ip);
  Serial.println(ip);

  // print your MAC address:
  byte mac[6];
  WiFi.macAddress(mac);
  Serial.print("MAC address: ");
  printMacAddress(mac);
}

void printCurrentNet() {
  // print the SSID of the network you're attached to:
  Serial.print("SSID: ");
  Serial.println(WiFi.SSID());

  // print the MAC address of the router you're attached to:
  byte bssid[6];
  WiFi.BSSID(bssid);
  Serial.print("BSSID: ");
  printMacAddress(bssid);

  // print the received signal strength:
  long rssi = WiFi.RSSI();
  Serial.print("signal strength (RSSI):");
  Serial.println(rssi);

  // print the encryption type:
  byte encryption = WiFi.encryptionType();
  Serial.print("Encryption Type:");
  Serial.println(encryption, HEX);
  Serial.println();
}

void printMacAddress(byte mac[]) {
  for (int i = 5; i >= 0; i--) {
    if (mac[i] < 16) {
      Serial.print("0");
    }
    Serial.print(mac[i], HEX);
    if (i > 0) {
      Serial.print(":");
    }
  }
  Serial.println();
}
#endif

/*########################################################
   FOR DEBUG PURPOSE - END
  ########################################################*/
