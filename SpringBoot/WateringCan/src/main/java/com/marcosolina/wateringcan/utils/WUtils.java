package com.marcosolina.wateringcan.utils;

import java.util.HashMap;
import java.util.Map;

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
	
	
	public static Map<String, Map<String, String>> getJsUrlConstants() {
		Map<String, Map<String, String>> map = new HashMap<>();
		
		Map<String, String> actions = new HashMap<>();
		actions.put("SET_PUMP_STATUS", contextPath + WConstants.URL_ACTIONS_ROOT + WConstants.URL_ACTIONS_SET_STATUS);
		actions.put("LIST_POTS", contextPath + WConstants.URL_ACTIONS_ROOT + WConstants.URL_ACTIONS_LIST_POTS);
		actions.put("SAVE_CONFIG", contextPath + WConstants.URL_ACTIONS_ROOT + WConstants.URL_ACTIONS_SAVE_CONFIG);
		map.put("ACTIONS", actions);
		
		return map;
	}

	public static int arduinoCommandsPort() {
		return arduinoCommandsPort;
	}
	
	public static long getMilliSecondToPourMl(int ml) {
		double seconds = (double)ml / WUtils.mlPerSecond;
		long milliseconds = (long) (seconds * 1000);
		return milliseconds;
	}
	
}
