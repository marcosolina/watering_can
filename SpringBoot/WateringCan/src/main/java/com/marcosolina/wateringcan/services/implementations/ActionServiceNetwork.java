package com.marcosolina.wateringcan.services.implementations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.marcosolina.wateringcan.devices.FlowerPot;
import com.marcosolina.wateringcan.enums.PumpStatuses;
import com.marcosolina.wateringcan.errors.WateringException;
import com.marcosolina.wateringcan.services.interfaces.ActionService;
import com.marcosolina.wateringcan.services.interfaces.BoardsManager;
import com.marcosolina.wateringcan.services.interfaces.WateringConfigService;
import com.marcosolina.wateringcan.utils.WUtils;

public class ActionServiceNetwork implements ActionService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionServiceNetwork.class);

	@Autowired
	private BoardsManager boardsManager;
	
	@Autowired
	private WateringConfigService configService;
	
	@Override
	public Set<FlowerPot> getListOfPots() throws WateringException {
		LOGGER.debug("Retrieving list of available pumps");
		Set<FlowerPot> potsSet = new HashSet<>();
		
		List<String> macs = boardsManager.getMacList();
		for (String mac : macs) {
			String ip = boardsManager.getIpForMac(mac);
			String reply = sendCommand(ip, WUtils.arduinoCommandsPort(), "0");
			potsSet.addAll(convertArduinoPotsStatusesReply(reply, mac));
		}
		
		
		return potsSet;
	}
	
	private Set<FlowerPot> convertArduinoPotsStatusesReply(String arduinoReply, String mac){
		Set<FlowerPot> potSet = new HashSet<>();
		String [] pots = arduinoReply.split("_");
		for (String pot : pots) {
			String [] potInfo = pot.split("-");
			String potId = potInfo[0];
			String pumpStatus = potInfo[1];
			String moistVal = potInfo[2];
			String moistMin = potInfo[3];
			String moistMax = potInfo[4];
			String moistWet = potInfo[5];
			String moistDry = potInfo[6];
			
			FlowerPot p = new FlowerPot(mac, potId, PumpStatuses.fromInt(Integer.parseInt(pumpStatus)));
			p.setDryValue(Integer.parseInt(moistDry));
			p.setHumidity(Integer.parseInt(moistVal));
			p.setMaxHumidityRead(Integer.parseInt(moistMax));
			p.setMinHumidityRead(Integer.parseInt(moistMin));
			p.setWetValue(Integer.parseInt(moistWet));
			
			p.setDescription(configService.getPotDescription(mac, potId));
			
			potSet.add(p);
		}
		
		return potSet;
	}

	private String sendCommand(String ip, int port, String command) throws WateringException {
		try (Socket socket = new Socket(ip, port)) {
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(String.format("Sending to ip: %s command: %s", ip, command));
			}

			out.println(command);
			String reply = in.readLine();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(String.format("IP: %s has replied with: %s", ip, reply));
			}

			return reply;

		} catch (IOException e) {
			if (LOGGER.isDebugEnabled()) {
				e.printStackTrace();
			}
		}

		throw new WateringException("Not able to send the command");
	}

	@Override
	public boolean setPotStatus(FlowerPot pot) throws WateringException {
		
		/*
		 * If I am turning on the Pump I have to start a new thread
		 * to turn it of later
		 */
		if(pot.getStatus() == PumpStatuses.ON) {
			if(pot.getMl() > 0) {
				String reply = sendCommand(boardsManager.getIpForMac(pot.getMac()), WUtils.arduinoCommandsPort(), String.format("E%s-%s", pot.getId(), pot.getStatus().getStatus()));
				Runnable stopPumpRunnable = () -> {
					try {
						Thread.sleep(WUtils.getMilliSecondToPourMl(pot.getMl()));
						sendCommand(boardsManager.getIpForMac(pot.getMac()), WUtils.arduinoCommandsPort(), String.format("E%s-%s", pot.getId(), PumpStatuses.OFF.getStatus()));
					} catch (WateringException | InterruptedException e) {
						e.printStackTrace();
					}
				};
				
				new Thread(stopPumpRunnable).start();
				
				return "OK".contentEquals(reply);
			}
			return true;
		}
		
		String reply = sendCommand(boardsManager.getIpForMac(pot.getMac()), WUtils.arduinoCommandsPort(), String.format("E%s-%s", pot.getId(), pot.getStatus().getStatus()));
		return "OK".contentEquals(reply);
	}
	
}
