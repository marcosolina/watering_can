package com.marcosolina.wateringcan.utils;

import java.util.HashMap;
import java.util.Map;

import com.marcosolina.wateringcan.devices.FlowerPot;

public class WUtils {

	private static String contextPath;
	private static int arduinoCommandsPort;
	private static int mlPerSecond;

	private WUtils() {

	}

	public static void setContextPath(String contextPath) {
		WUtils.contextPath = contextPath;
	}

	public static void arduinoCommandsPort(int arduinoPort) {
		WUtils.arduinoCommandsPort = arduinoPort;
	}

	public static void setMlPerSecond(int mlPerSecond) {
		WUtils.mlPerSecond = mlPerSecond;
	}

	/**
	 * Map used in the front-end. It helps me do don't remember the URL for the
	 * different HTTP requests
	 * 
	 * @return
	 */
	public static Map<String, Map<String, String>> getJsUrlConstants() {
		Map<String, Map<String, String>> map = new HashMap<>();

		Map<String, String> actions = new HashMap<>();
		actions.put("SET_PUMP_STATUS", contextPath + WConstants.URL_ACTIONS_ROOT + WConstants.URL_ACTIONS_SET_STATUS);
		actions.put("LIST_POTS", contextPath + WConstants.URL_ACTIONS_ROOT + WConstants.URL_ACTIONS_LIST_POTS);
		actions.put("SAVE_CONFIG", contextPath + WConstants.URL_ACTIONS_ROOT + WConstants.URL_ACTIONS_SAVE_CONFIG);
		actions.put("SET_WET_DRY", contextPath + WConstants.URL_ACTIONS_ROOT + WConstants.URL_ACTIONS_SET_WET_DRY);
		map.put("ACTIONS", actions);

		return map;
	}

	public static int arduinoCommandsPort() {
		return arduinoCommandsPort;
	}

	/**
	 * The user should calibrate the pumps. If he does not I will default to the
	 * value that I have calculated with my pump
	 * 
	 * @param ml
	 * @return
	 */
	public static long getMilliSecondToPourMl(FlowerPot pot) {
		int mlPerSecond = pot.getMlPerSecond() > 0 ? pot.getMlPerSecond() : WUtils.mlPerSecond;
		double seconds = (double) pot.getMl() / mlPerSecond;
		long milliseconds = (long) (seconds * 1000);
		return milliseconds;
	}

}
