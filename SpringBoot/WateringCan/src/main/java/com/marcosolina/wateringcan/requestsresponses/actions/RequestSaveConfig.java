package com.marcosolina.wateringcan.requestsresponses.actions;

import java.io.Serializable;
import java.util.Set;

import com.marcosolina.wateringcan.devices.Pump;

public class RequestSaveConfig implements Serializable {

	private static final long serialVersionUID = 1L;
	private Set<Pump> pumps;

	public Set<Pump> getPumps() {
		return pumps;
	}

	public void setPumps(Set<Pump> pumps) {
		this.pumps = pumps;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
