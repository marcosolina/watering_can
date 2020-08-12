package com.marcosolina.wateringcan.services.implementations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.marcosolina.wateringcan.devices.FlowerPot;
import com.marcosolina.wateringcan.errors.WateringException;
import com.marcosolina.wateringcan.services.interfaces.ActionService;
import com.marcosolina.wateringcan.services.interfaces.BoardsManager;
import com.marcosolina.wateringcan.services.interfaces.WateringConfigService;

/**
 * This implementation manages everything in the memory
 * 
 * @author Marco
 *
 */
public class BoardsManagerInMemory implements BoardsManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(BoardsManagerInMemory.class);
	
	@Autowired
	private WateringConfigService wateringConfig;
	@Autowired
	private ActionService actionService;
	
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

	@Override
	public List<String> getMacList() {
		List<String> pumps = new ArrayList<>();
		
		boards.entrySet().stream().forEach(e->pumps.add(e.getKey()));
		
		return pumps;
	}

	@Override
	public void setLoadedConfig(String mac) {
		LOGGER.debug(String.format("Updaing the board: %s with the stored json config", mac));
		try {
			Set<FlowerPot> pots = wateringConfig.loadPotsConfig();
			pots.stream().filter(p -> p.getMac().equals(mac)).forEach(p -> {
				try {
					actionService.updateWetDryPotValues(p);
				} catch (WateringException e) {
					LOGGER.error(e.getMessage());
				}
			});
		} catch (WateringException e) {
			LOGGER.error(e.getMessage());
		}
		
	}

}
