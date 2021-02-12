package com.ams.kv.contacts.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.ams.kv.contacts.model.Contact;
import com.ams.kv.contacts.model.ServiceError;
import com.ams.kv.contacts.service.ContactDuplicatedException;
import com.ams.kv.contacts.service.ContactInvalidException;
import com.ams.kv.contacts.service.ContactNotFoundException;
import com.ams.kv.contacts.service.ContactService;
import com.ams.kv.utils.Constants;

@RestController
@RequestMapping(Constants.CONTACT_URL)
public class ContactController {
	
	private static Logger log = LoggerFactory.getLogger(ContactController.class.getName());
	
	@Autowired
	private ContactService service;

    @GetMapping(value = "{id}", produces = { "application/json" })
	public ResponseEntity<Contact> retrieveContactById(@PathVariable int id) {    	
    	log.debug("Entering retrieveContactById(); id = {}", id);
    	
    	var contact = service.retrieveContactById(id);
    	
		log.debug("Contact retrieved: {}", contact);
		
    	return new ResponseEntity<Contact>(contact, HttpStatus.OK);
	}
       	
    @GetMapping(params = { "email" }, produces = { "application/json" })
	public ResponseEntity<Contact> retrieveContactByEmail(@RequestParam("email") String email) {    	
    	log.debug("Entering retrieveContactByEmail(); email = {}", email);
    	
    	var contact = service.retrieveContactByEmail(email);
    	
		log.debug("Contact retrieved: {}", contact);
		
    	return new ResponseEntity<Contact>(contact, HttpStatus.OK);
	}

    @GetMapping(params = { "phone" }, produces = { "application/json" })
	public ResponseEntity<Contact> retrieveContactByPhone(@RequestParam("phone") String phone) {    	
    	log.debug("Entering retrieveContactByPhone(); phone = {}", phone);
    	
    	var contact = service.retrieveContactByPhone(phone);
    	
		log.debug("Contact retrieved: {}", contact);
		
    	return new ResponseEntity<Contact>(contact, HttpStatus.OK);
	}
    
    @PutMapping(value = "{id}", consumes = { "application/json" })
	public ResponseEntity<Void> updateContact(@RequestBody Contact contact, @PathVariable int id) {    	
    	log.debug("Entering updateContact(); id = {}, contact = {}", id, contact);
    	
    	service.updateContact(id, contact);
    	
		log.debug("Contact updated");
		
    	return new ResponseEntity<>(HttpStatus.OK);
	}
    
    @PostMapping(consumes = { "application/json" })
	public ResponseEntity<Void> addContact(@RequestBody Contact contact, UriComponentsBuilder ucb) {    	
    	log.debug("Entering addContact(); contact = {}", contact);
    	
    	var id = service.addContact(contact);
    	
        var locationUri = ucb.path(Constants.CONTACT_URL + "/")
            .path(Long.toString(id))
            .build()
            .toUri();
        
		log.debug("Contact added at %s", locationUri);
        
		var headers = new HttpHeaders();
        headers.setLocation(locationUri);
	
    	return new ResponseEntity<>(headers, HttpStatus.CREATED);
	}    
    
    @DeleteMapping(value = "{id}")
	public ResponseEntity<Void> deleteContact(@PathVariable int id) {    	
    	log.debug("Entering deleteContact(); id = {}", id);
    	
    	service.deleteContact(id);
    	
		log.debug("Contact deleted");
		
    	return new ResponseEntity<>(HttpStatus.OK);
	}
    
    @ExceptionHandler(ContactNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void contactNotFound(ContactNotFoundException e) {
    	log.error("Contact not found", e);
    }
    
    @ExceptionHandler(ContactInvalidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ServiceError contactInvalid(ContactInvalidException e) {
    	log.error("Contact is invalid", e);

    	var error = new ServiceError();
    	error.setCode(1);
    	error.setMessage(e.getMessage());

    	return error;    	
    }
    
    @ExceptionHandler(ContactDuplicatedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ServiceError contactDuplicated(ContactDuplicatedException e) {
    	log.error("Contact is duplicated", e);

    	var error = new ServiceError();
    	error.setCode(2);
    	error.setMessage(e.getMessage());

    	return error;    	
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void exceptionHandler(Exception e) {
    	log.error("Error processing request", e);
    }    
}
