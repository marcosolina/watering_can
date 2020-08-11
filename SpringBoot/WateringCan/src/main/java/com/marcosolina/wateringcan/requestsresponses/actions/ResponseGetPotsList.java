package com.marcosolina.wateringcan.requestsresponses.actions;

import java.util.ArrayList;
import java.util.List;

import com.marcosolina.wateringcan.devices.FlowerPot;
import com.marcosolina.wateringcan.requestsresponses.HttpWateringResponse;

/**
 * HTTP Response object that contains a list of all the available flower pots
 * 
 * @author Marco
 *
 */
public class ResponseGetPotsList extends HttpWateringResponse {
	private List<FlowerPot> pots;

	public boolean addPot(FlowerPot pot) {
		if (this.pots == null) {
			this.pots = new ArrayList<>();
		}

		return this.pots.add(pot);
	}

	public List<FlowerPot> getPots() {
		return pots;
	}

	public void setPumps(List<FlowerPot> pots) {
		this.pots = pots;
	}

}
