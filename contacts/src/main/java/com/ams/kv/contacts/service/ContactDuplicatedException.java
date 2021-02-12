package com.ams.kv.contacts.service;

public class ContactDuplicatedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ContactDuplicatedException() {
	}

	public ContactDuplicatedException(String message) {
		super(message);
	}

	public ContactDuplicatedException(String message, Throwable cause) {
		super(message, cause);
	}
}
