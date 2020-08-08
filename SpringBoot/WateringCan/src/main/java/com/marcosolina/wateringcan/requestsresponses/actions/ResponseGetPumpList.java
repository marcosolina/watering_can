package com.marcosolina.wateringcan.requestsresponses.actions;

import java.util.ArrayList;
import java.util.List;

import com.marcosolina.wateringcan.devices.Pump;
import com.marcosolina.wateringcan.requestsresponses.HttpWateringResponse;

/**
 * HTTP Response object that contains a list of all the available pumps
 * 
 * @author Marco
 *
 */
public class ResponseGetPumpList extends HttpWateringResponse {
	private List<Pump> pumps;

	public boolean addPump(Pump pump) {
		if (this.pumps == null) {
			this.pumps = new ArrayList<>();
		}

		return this.pumps.add(pump);
	}

	public List<Pump> getPumps() {
		return pumps;
	}

	public void setPumps(List<Pump> pumps) {
		this.pumps = pumps;
	}

}
