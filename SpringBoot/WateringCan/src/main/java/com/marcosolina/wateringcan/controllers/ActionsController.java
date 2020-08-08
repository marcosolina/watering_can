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

import com.marcosolina.wateringcan.devices.Pump;
import com.marcosolina.wateringcan.errors.WateringException;
import com.marcosolina.wateringcan.requestsresponses.actions.ResponseChangePumpStatus;
import com.marcosolina.wateringcan.requestsresponses.actions.ResponseGetPumpList;
import com.marcosolina.wateringcan.services.interfaces.ActionService;
import com.marcosolina.wateringcan.utils.WConstants;

@RestController
@RequestMapping(value = WConstants.URL_ACTIONS_ROOT)
public class ActionsController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionsController.class);

	@Autowired
	private ActionService actionService;
	
	@PostMapping(value = WConstants.URL_ACTIONS_SET_STATUS)
	public ResponseEntity<ResponseChangePumpStatus> changePumpStatus(@RequestBody Pump pump) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					String.format("New pump status change request for pump: %s on IP: %s", pump.getId(), pump.getIp()));
		}

		ResponseChangePumpStatus resp = new ResponseChangePumpStatus();
		try {
			resp.setStatus(actionService.setPumpStatus(pump));
		} catch (WateringException e) {
			resp.addError(e);
		}

		return new ResponseEntity<>(resp, HttpStatus.OK);
	}

	@GetMapping(value = WConstants.URL_ACTIONS_LIST_PUMPS)
	public ResponseEntity<ResponseGetPumpList> getPumpsList() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("New list pump request");
		}

		ResponseGetPumpList resp = new ResponseGetPumpList();
		try {
			actionService.getListOfPumps().stream().forEach(resp::addPump);
			resp.setStatus(true);
		} catch (WateringException e) {
			resp.addError(e);
		}

		return new ResponseEntity<>(resp, HttpStatus.OK);
	}
}
