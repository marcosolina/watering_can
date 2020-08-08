package com.marcosolina.wateringcan.errors;

/**
 * Custom exception
 * 
 * @author Marco
 *
 */
public class WateringException extends Exception {
	private static final long serialVersionUID = 1L;

	public enum ExceptionType {
		warning, info, success, error
	}

	private String title;
	private String message;
	private ExceptionType type;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public WateringException(String title, String message, ExceptionType type) {
		super(message);
		this.message = message;
		this.title = title;
		this.type = type;
	}

	public WateringException(String message) {
		this("Error", message, ExceptionType.error);
	}

	public WateringException(Exception e) {
		this(e.getMessage());
	}

	@Override
	public String toString() {
		return this.getMessage();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ExceptionType getType() {
		return type;
	}

	public void setType(ExceptionType type) {
		this.type = type;
	}

	public WateringException clearStackTrace() {
		this.setStackTrace(new StackTraceElement[0]);
		return this;
	}
}
