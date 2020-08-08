package com.marcosolina.wateringcan.enums;

/**
 * Use this enum to manage the pump status
 * 
 * @author Marco
 *
 */
public enum PumpStatuses {
	ON(0), OFF(1);

	private int status;

	PumpStatuses(int status) {
		this.status = status;
	}

	public int getStatus() {
		return this.status;
	}

	public static PumpStatuses fromInt(int status) {
		for (PumpStatuses p : PumpStatuses.values()) {
			if (p.status == status) {
				return p;
			}
		}
		return null;
	}
}
