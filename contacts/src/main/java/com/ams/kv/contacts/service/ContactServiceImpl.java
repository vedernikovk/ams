package com.ams.kv.contacts.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ams.kv.contacts.dao.ContactEntity;
import com.ams.kv.contacts.dao.ContactRepository;
import com.ams.kv.contacts.model.Contact; 

@Service
public class ContactServiceImpl implements ContactService {

	private static Logger log = LoggerFactory.getLogger(ContactServiceImpl.class.getName());

	@Autowired
	private ContactRepository contactRepository;
	
	@Override
	@Transactional (readOnly = true)
	public Contact retrieveContactById(long id) throws ContactNotFoundException {
		
		var contactEntity = contactRepository.findById(id);
		
		if (contactEntity.isPresent()) {
			return entityToModel(contactEntity.get());
		} else {
			throw new ContactNotFoundException(Long.toString(id));
		}
	}		

	@Override
	@Transactional (readOnly = true)
	public Contact retrieveContactByEmail(String email) throws ContactNotFoundException {
		
		var contactEntity = contactRepository.findByEmail(email);
		
		if (contactEntity.isPresent()) {
			return entityToModel(contactEntity.get());
		} else {
			throw new ContactNotFoundException(email);
		}
	}

	@Override
	@Transactional (readOnly = true)	
	public Contact retrieveContactByPhone(String phone) throws ContactNotFoundException {
		
		var contactEntity = contactRepository.findByPhone(phone);
		
		if (contactEntity.isPresent()) {
			return entityToModel(contactEntity.get());
		} else {
			throw new ContactNotFoundException(phone);
		}
	}

	@Override
	public long addContact(Contact contact) throws ContactDuplicatedException, ContactInvalidException {
		validate(contact);
		
		var entity = new ContactEntity();
		modelToEntity(contact, entity);

		try {
			var id = saveContact(entity);
			return id;
			
		} catch (DataIntegrityViolationException e) {
			log.error("Error adding contact", e);
			
			throw new ContactDuplicatedException("Contact is duplicated");
		}		
	}

	@Override
	public void updateContact(long id, Contact contact) throws ContactNotFoundException, ContactDuplicatedException, ContactInvalidException {
		validate(contact);
		
		var entry = contactRepository.findById(id);		
		if (entry.isPresent()) {
			try {
				var entity = entry.get();
				modelToEntity(contact, entity);
				
				saveContact(entity);
				
			} catch (DataIntegrityViolationException e) {
				log.error("Error updating contact", e);
				
				throw new ContactDuplicatedException("Contact is duplicated");
			}

		} else {
			throw new ContactNotFoundException(Long.toString(id));
		}
	}

	@Transactional
	public void deleteContact(long id) throws ContactNotFoundException {
		var entry = contactRepository.findById(id);
		
		if (entry.isPresent()) {
			try {
				contactRepository.delete(entry.get());
			} catch (Exception e) {
				log.error("Error deleting contact", e);			
			}

		} else {
			throw new ContactNotFoundException(Long.toString(id));
		}
	}

	@Transactional
	private long saveContact(ContactEntity contact) {
		contactRepository.save(contact);
		return contact.getId();
	}

	private void validate(Contact contact) {
		if (!StringUtils.hasText(contact.getEmail()) && !StringUtils.hasText(contact.getPhone())) {
			throw new ContactInvalidException("Either email or phone must be presented");
		}
	}
	
	private void modelToEntity(Contact model, ContactEntity entity) {
		entity.setFirstName(model.getFirstName());
		entity.setLastName(model.getLastName());
		entity.setEmail(model.getEmail());
		entity.setPhone(model.getPhone());
	}
	
	private Contact entityToModel(ContactEntity entity) {
		var contact = new Contact();
		contact.setFirstName(entity.getFirstName());
		contact.setLastName(entity.getLastName());
		contact.setEmail(entity.getEmail());
		contact.setPhone(entity.getPhone());
		return contact;
	}	
}
