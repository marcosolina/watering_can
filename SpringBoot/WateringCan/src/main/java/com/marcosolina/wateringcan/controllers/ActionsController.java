package com.marcosolina.wateringcan.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.marcosolina.wateringcan.devices.FlowerPot;
import com.marcosolina.wateringcan.errors.WateringException;
import com.marcosolina.wateringcan.requestsresponses.ResponseSimple;
import com.marcosolina.wateringcan.requestsresponses.actions.RequestSaveConfig;
import com.marcosolina.wateringcan.requestsresponses.actions.ResponseChangePotStatus;
import com.marcosolina.wateringcan.requestsresponses.actions.ResponseGetPotsList;
import com.marcosolina.wateringcan.services.interfaces.ActionService;
import com.marcosolina.wateringcan.services.interfaces.WateringConfigService;
import com.marcosolina.wateringcan.utils.WConstants;

@RestController
@RequestMapping(value = WConstants.URL_ACTIONS_ROOT)
public class ActionsController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionsController.class);

	@Autowired
	private ActionService actionService;

	@Autowired
	private WateringConfigService configService;

	@PostMapping(value = WConstants.URL_ACTIONS_SET_STATUS)
	public ResponseEntity<ResponseChangePotStatus> changePotStatus(@RequestBody FlowerPot pot) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					String.format("New pot status change request for pot: %s on MAC: %s", pot.getId(), pot.getMac()));
		}

		ResponseChangePotStatus resp = new ResponseChangePotStatus();
		try {
			resp.setStatus(actionService.setPotStatus(pot));
		} catch (WateringException e) {
			resp.addError(e);
		}

		return new ResponseEntity<>(resp, HttpStatus.OK);
	}

	@GetMapping(value = WConstants.URL_ACTIONS_LIST_POTS)
	public ResponseEntity<ResponseGetPotsList> getPotsList() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("New list pots request");
		}

		ResponseGetPotsList resp = new ResponseGetPotsList();
		try {
			actionService.getListOfPots().stream().forEach(resp::addPot);
			resp.setStatus(true);
		} catch (WateringException e) {
			resp.addError(e);
		}

		return new ResponseEntity<>(resp, HttpStatus.OK);
	}

	@PostMapping(value = WConstants.URL_ACTIONS_SAVE_CONFIG)
	public ResponseEntity<ResponseSimple> saveConfig(@RequestBody RequestSaveConfig request) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("New save config request");
		}

		ResponseSimple resp = new ResponseSimple();
		try {
			resp.setStatus(configService.storePotsConfig(request.getPots()));
		} catch (WateringException e) {
			resp.addError(e);
		}

		return new ResponseEntity<>(resp, HttpStatus.OK);
	}
}
