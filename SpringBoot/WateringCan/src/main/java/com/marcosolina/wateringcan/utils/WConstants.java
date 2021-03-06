package com.marcosolina.wateringcan.utils;

/**
 * Watering can constants
 * 
 * @author Marco
 *
 */
public class WConstants {
	private WConstants() {
	}

	// @formatter:off
	/*###################################
	 * URLs for the Actions controller
	 ###################################*/
	// @formatter:on
	public static final String URL_ACTIONS_ROOT = "/actions";
	public static final String URL_ACTIONS_SET_STATUS = "/setStatus";
	public static final String URL_ACTIONS_LIST_POTS = "/pumplist";
	public static final String URL_ACTIONS_SAVE_CONFIG = "/saveconfig";
	public static final String URL_ACTIONS_SET_WET_DRY = "/wetDry";
	
	
	// @formatter:off
	/*###################################
	 * URLs for the Boards controller
	 ###################################*/
	// @formatter:on
	public static final String URL_BOARDS_ROOT = "/Arduino";
	public static final String URL_BOARDS_REGISTRATION = "/registration";
	
}
