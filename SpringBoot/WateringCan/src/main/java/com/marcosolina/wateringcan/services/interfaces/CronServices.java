package com.marcosolina.wateringcan.services.interfaces;

/**
 * This service takes care of the recurring operations
 * 
 * @author Marco
 *
 */
public interface CronServices {

	/**
	 * It tries to discover the Arduino boards
	 */
	public void broadCastServerDiscoeryMessate();

}
