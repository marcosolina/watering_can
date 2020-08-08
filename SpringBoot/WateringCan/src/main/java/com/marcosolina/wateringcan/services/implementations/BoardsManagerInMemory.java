package com.marcosolina.wateringcan.services.implementations;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marcosolina.wateringcan.services.interfaces.BoardsManager;

/**
 * This implementation manages everything in the memory
 * 
 * @author Marco
 *
 */
public class BoardsManagerInMemory implements BoardsManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(BoardsManagerInMemory.class);
	
	/*
	 * Key -> MAC address
	 * Value -> IP address
	 */
	private static final Map<String, String> boards = new HashMap<>();

	@Override
	public boolean registerBoard(String ip, String mac) {
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Registering board with IP: %s and MAC: %s", ip, mac));
		}
		boards.compute(mac, (k, v) -> ip);
		return true;
	}

	@Override
	public String getIpForMac(String mac) {
		return boards.get(mac);
	}

	@Override
	public String getMacForIp(String ip) {
		for(Entry<String, String> entry : boards.entrySet()) {
			if(entry.getValue().equals(ip)) {
				return entry.getKey();
			}
		}
		return null;
	}

}
