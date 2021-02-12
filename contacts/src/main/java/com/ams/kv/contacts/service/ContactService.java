package com.ams.kv.contacts.service;

import com.ams.kv.contacts.model.Contact;

public interface ContactService {
	
	Contact retrieveContactById(long id) throws ContactNotFoundException;
	
	Contact retrieveContactByEmail(String email) throws ContactNotFoundException;
	
	Contact retrieveContactByPhone(String phone) throws ContactNotFoundException;
	
	long addContact(Contact contact) throws ContactDuplicatedException, ContactInvalidException;
	
	void updateContact(long id, Contact contact) throws ContactNotFoundException, ContactDuplicatedException, ContactInvalidException;
	
	void deleteContact(long id) throws ContactNotFoundException;
}
