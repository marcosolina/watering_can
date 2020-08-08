package com.marcosolina.wateringcan.services.interfaces;

import java.util.List;

/**
 * It provides the functionalities to manage the Arduino boards
 * 
 * @author Marco
 *
 */
public interface BoardsManager {

	/**
	 * It registers the board into the system
	 * 
	 * @param ip
	 * @param mac
	 * @return
	 */
	public boolean registerBoard(String ip, String mac);

	/**
	 * It returns the IP associated to the provided MAC address
	 * 
	 * @param mac
	 * @return
	 */
	public String getIpForMac(String mac);

	/**
	 * It returns the associated MAC address to the provided IP address
	 * 
	 * @param ip
	 * @return
	 */
	public String getMacForIp(String ip);

	/**
	 * It returns a list of the available boards
	 * 
	 * @return
	 */
	public List<String> getIpList();

}
