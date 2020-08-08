package com.marcosolina.wateringcan.services.interfaces;

import java.util.Set;

import com.marcosolina.wateringcan.devices.Pump;
import com.marcosolina.wateringcan.errors.WateringException;

/**
 * This interface provides you a set of action that you can perform
 * 
 * @author Marco
 *
 */
public interface ActionService {

	/**
	 * It returns a list of all the available pumps
	 * 
	 * @return
	 * @throws WateringException
	 */
	public Set<Pump> getListOfPumps() throws WateringException;

	/**
	 * It set the pump status
	 * 
	 * @param pump
	 * @return
	 * @throws WateringException
	 */
	public boolean setPumpStatus(Pump pump) throws WateringException;
}
