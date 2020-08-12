package com.marcosolina.wateringcan.services.interfaces;

import java.util.Set;

import com.marcosolina.wateringcan.devices.FlowerPot;
import com.marcosolina.wateringcan.errors.WateringException;

/**
 * This interface provides you a set of action that you can perform
 * 
 * @author Marco
 *
 */
public interface ActionService {

	/**
	 * It returns a list of all the available flower pots
	 * 
	 * @return
	 * @throws WateringException
	 */
	public Set<FlowerPot> getListOfPots() throws WateringException;

	/**
	 * It set the flower pot status
	 * 
	 * @param pump
	 * @return
	 * @throws WateringException
	 */
	public boolean setPotStatus(FlowerPot pot) throws WateringException;

	/**
	 * Informing Arduino of the values that the user has selected for the "Dry" and
	 * "Wet" statuses
	 * 
	 * @param pot
	 * @return
	 * @throws WateringException
	 */
	public boolean updateWetDryPotValues(FlowerPot pot) throws WateringException;
}
