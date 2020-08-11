package com.marcosolina.wateringcan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.marcosolina.wateringcan.services.implementations.ActionServiceNetwork;
import com.marcosolina.wateringcan.services.implementations.BoardsManagerInMemory;
import com.marcosolina.wateringcan.services.implementations.WateringConfigJson;
import com.marcosolina.wateringcan.services.interfaces.ActionService;
import com.marcosolina.wateringcan.services.interfaces.BoardsManager;
import com.marcosolina.wateringcan.services.interfaces.WateringConfigService;

/**
 * Standard Spring config class
 * 
 * @author Marco
 *
 */
@Configuration
public class Beans {

	@Bean
	public ActionService getActionService() {
		return new ActionServiceNetwork();
	}

	@Bean
	public BoardsManager getBoardsManager() {
		return new BoardsManagerInMemory();
	}

	@Bean
	public WateringConfigService getWatringConfigService() {
		return new WateringConfigJson();
	}
}
