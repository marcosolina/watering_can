package com.marcosolina.wateringcan.requestsresponses;

import java.util.ArrayList;
import java.util.List;

import com.marcosolina.wateringcan.errors.WateringException;

/**
 * Abstract class to provide a common set of data in the responses
 * 
 * @author Marco
 *
 */
public abstract class HttpWateringResponse {

	private boolean status;
	private List<WateringException> errors;

	public boolean addError(WateringException error) {
		if (this.errors == null) {
			this.errors = new ArrayList<>();
		}
		return this.errors.add(error);
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public List<WateringException> getErrors() {
		return errors;
	}

	public void setErrors(List<WateringException> errors) {
		this.errors = errors;
	}

}
