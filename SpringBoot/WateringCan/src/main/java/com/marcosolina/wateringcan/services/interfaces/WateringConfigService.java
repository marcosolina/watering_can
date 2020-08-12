package com.marcosolina.wateringcan.services.interfaces;

import java.util.Set;

import com.marcosolina.wateringcan.devices.FlowerPot;
import com.marcosolina.wateringcan.errors.WateringException;

public interface WateringConfigService {

	public boolean storePotsConfig(Set<FlowerPot> pots) throws WateringException;
	
	public Set<FlowerPot> loadPotsConfig() throws WateringException;
	
	public String getPotDescription(String mac, String id);
	
	public void setWetAndDryValues(FlowerPot pot);
}
