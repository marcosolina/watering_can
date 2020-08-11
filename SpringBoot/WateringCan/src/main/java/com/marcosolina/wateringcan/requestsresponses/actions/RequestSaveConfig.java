package com.marcosolina.wateringcan.requestsresponses.actions;

import java.io.Serializable;
import java.util.Set;

import com.marcosolina.wateringcan.devices.FlowerPot;

public class RequestSaveConfig implements Serializable {

	private static final long serialVersionUID = 1L;
	private Set<FlowerPot> pots;

	public Set<FlowerPot> getPots() {
		return pots;
	}

	public void setPots(Set<FlowerPot> pots) {
		this.pots = pots;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
