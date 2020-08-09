package com.marcosolina.wateringcan.devices;

import java.io.Serializable;

import com.marcosolina.wateringcan.enums.PumpStatuses;

/**
 * This class represents a single water pump
 * 
 * @author Marco
 *
 */
public class Pump implements Serializable {

	private static final long serialVersionUID = 1L;
	private String mac;
	private String id;
	private int ml;
	private String description;
	private PumpStatuses status;

	public Pump() {
	}

	public Pump(String mac, String id, PumpStatuses status) {
		this.mac = mac;
		this.id = id;
		this.status = status;
	}
	

	public int getMl() {
		return ml;
	}

	public void setMl(int ml) {
		this.ml = ml;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public PumpStatuses getStatus() {
		return status;
	}

	public void setStatus(PumpStatuses status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((mac == null) ? 0 : mac.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pump other = (Pump) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (mac == null) {
			if (other.mac != null)
				return false;
		} else if (!mac.equals(other.mac))
			return false;
		return true;
	}

}
