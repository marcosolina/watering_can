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

import com.marcosolina.wateringcan.devices.Pump;
import com.marcosolina.wateringcan.enums.PumpStatuses;
import com.marcosolina.wateringcan.errors.WateringException;
import com.marcosolina.wateringcan.services.interfaces.ActionService;
import com.marcosolina.wateringcan.services.interfaces.BoardsManager;
import com.marcosolina.wateringcan.utils.WUtils;

public class ActionServiceNetwork implements ActionService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionServiceNetwork.class);

	@Autowired
	private BoardsManager boardsManager;
	
	@Override
	public Set<Pump> getListOfPumps() throws WateringException {
		LOGGER.debug("Retrieving list of available pumps");
		Set<Pump> pumpsSet = new HashSet<>();
		
		List<String> ips = boardsManager.getIpList();
		for (String ip : ips) {
			String reply = sendCommand(ip, WUtils.arduinoCommandsPort(), "0");
			pumpsSet.addAll(convertArduinoPumpsStatusesReply(reply, ip));
		}
		
		
		return pumpsSet;
	}
	
	private Set<Pump> convertArduinoPumpsStatusesReply(String arduinoReply, String ip){
		Set<Pump> pumpsSet = new HashSet<>();
		String [] pumps = arduinoReply.split("_");
		for (String pump : pumps) {
			String [] pumpInfo = pump.split("-");
			pumpsSet.add(new Pump(ip, pumpInfo[0], PumpStatuses.fromInt(Integer.parseInt(pumpInfo[1]))));
		}
		
		return pumpsSet;
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
	public boolean setPumpStatus(Pump pump) throws WateringException {
		String reply = sendCommand(pump.getIp(), WUtils.arduinoCommandsPort(), String.format("E%s-%s", pump.getId(), pump.getStatus().getStatus()));
		return "OK".contentEquals(reply);
	}
}
