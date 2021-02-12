package com.ams.kv.contacts.service;

public class ContactNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public ContactNotFoundException() {
	}

	public ContactNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ContactNotFoundException(String message) {
		super(message);
	}
}
