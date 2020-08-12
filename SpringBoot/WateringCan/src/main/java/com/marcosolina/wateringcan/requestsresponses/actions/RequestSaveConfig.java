package com.marcosolina.wateringcan.requestsresponses.actions;

import java.io.Serializable;
import java.util.Set;

import com.marcosolina.wateringcan.devices.FlowerPot;

/**
 * Flower pot configuration save. These are the config info that I want to
 * persist
 * 
 * @author Marco
 *
 */
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
