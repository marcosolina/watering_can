package com.marcosolina.wateringcan.controllers;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.marcosolina.wateringcan.services.interfaces.BoardsManager;
import com.marcosolina.wateringcan.utils.WConstants;

/**
 * This controller is the one that exposes the APIs to the Arduino boards
 * 
 * @author Marco
 *
 */
@RestController
@RequestMapping(value = WConstants.URL_BOARDS_ROOT)
public class BoardsController {

	@Autowired
	private BoardsManager boardsManager;

	private static final Logger LOGGER = LoggerFactory.getLogger(BoardsController.class);

	@GetMapping(value = WConstants.URL_BOARDS_REGISTRATION)
	public ResponseEntity<Void> storeArduinoIp(HttpServletRequest request, @RequestParam("MAC") String mac) {
		mac = mac.toUpperCase();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					String.format("Board registration request for IP: %s and MAC: %s", request.getRemoteAddr(), mac));
		}

		boardsManager.registerBoard(request.getRemoteAddr(), mac);
		boardsManager.setLoadedConfig(mac);

		return new ResponseEntity<>(HttpStatus.OK);
	}
	
}
