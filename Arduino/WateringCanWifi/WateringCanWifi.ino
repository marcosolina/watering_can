#include <SPI.h>
#include <WiFiNINA.h>
#include <WiFiUdp.h>

#define PORTA_SERVER 81//porta su cui ascolta arduino
#define DEBUG 1

#include "arduino_secrets.h"
/*
 * please enter your sensitive data in the Secret arduino_secrets.h
 */
char ssid[] = SECRET_SSID;// your network SSID (name)
char pass[] = SECRET_PASS;// your network password (use for WPA, or use as key for WEP)


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


void setup() {
//Initialize serial and wait for port to open:
#if DEBUG == 1
  Serial.begin(9600);
  while (!Serial) {
    ; // wait for serial port to connect. Needed for native USB port only
  }
#endif

}

void loop() {
  // put your main code here, to run repeatedly:

}


/* #################################
   START WIFI Stuff
  ##################################*/
boolean connectToWifi() {
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
