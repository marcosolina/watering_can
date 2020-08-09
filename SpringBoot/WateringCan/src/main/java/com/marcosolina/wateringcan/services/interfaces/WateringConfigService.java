package com.marcosolina.wateringcan.services.interfaces;

import java.util.Set;

import com.marcosolina.wateringcan.devices.Pump;
import com.marcosolina.wateringcan.errors.WateringException;

public interface WateringConfigService {

	public boolean storePumpsConfig(Set<Pump> pumps) throws WateringException;

	public Set<Pump> loadPumpsConfig() throws WateringException;
	
	public String getPupmDescription(String mac, String id);
}
