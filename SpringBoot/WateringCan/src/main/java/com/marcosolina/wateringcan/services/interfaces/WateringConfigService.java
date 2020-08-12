package com.marcosolina.wateringcan.services.interfaces;

import java.util.Set;

import com.marcosolina.wateringcan.devices.FlowerPot;
import com.marcosolina.wateringcan.errors.WateringException;

public interface WateringConfigService {

	/**
	 * It persists the Flowers Pot configuration
	 * 
	 * @param pots
	 * @return
	 * @throws WateringException
	 */
	public boolean storePotsConfig(Set<FlowerPot> pots) throws WateringException;

	/**
	 * It loads a previous persisted Flowers pots config
	 * 
	 * @return
	 * @throws WateringException
	 */
	public Set<FlowerPot> loadPotsConfig() throws WateringException;

	/**
	 * It retrieves the pot description
	 * 
	 * @param mac
	 * @param id
	 * @return
	 */
	public String getPotDescription(String mac, String id);

	/**
	 * In sets into the input param the values for the "Wet" and "Dry" retrieved
	 * from the persisted config
	 * 
	 * @param pot
	 */
	public void setWetAndDryValues(FlowerPot pot);
}
