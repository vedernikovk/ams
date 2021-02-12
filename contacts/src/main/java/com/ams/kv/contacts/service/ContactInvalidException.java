package com.ams.kv.contacts.service;

public class ContactInvalidException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ContactInvalidException() {
	}

	public ContactInvalidException(String message) {
		super(message);
	}

	public ContactInvalidException(String message, Throwable cause) {
		super(message, cause);
	}
}
