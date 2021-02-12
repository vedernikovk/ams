package com.ams.kv.contacts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.ams.kv.contacts.model.Contact;
import com.ams.kv.contacts.model.ServiceError;
import com.ams.kv.contacts.utils.Constants;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ContactsApplicationTests {
			
	@LocalServerPort
	private String port;
    
    @Autowired
    private TestRestTemplate client;

    @Test
    void testFindByIdFailure() {		
 		var response = client.exchange("http://localhost:" + port + Constants.CONTACT_URL + "/0", HttpMethod.GET, getRequest(), Contact.class); 		
 		assertTrue(response.getStatusCode().equals(HttpStatus.NOT_FOUND));		
    }
    
    @Test
    void testFindByEmailFailure() {		
 		var response = client.exchange("http://localhost:" + port + Constants.CONTACT_URL + "?email=qwe", HttpMethod.GET, getRequest(), Contact.class); 		
 		assertTrue(response.getStatusCode().equals(HttpStatus.NOT_FOUND));		
    }

    
    @Test
    void testFindByPhoneFailure() {		
 		var response = client.exchange("http://localhost:" + port + Constants.CONTACT_URL + "?phone=123", HttpMethod.GET, getRequest(), Contact.class); 		
 		assertTrue(response.getStatusCode().equals(HttpStatus.NOT_FOUND));		
    }
    
    @Test
    void testCreateInvalidContact() {		
 		var url = "http://localhost:" + port + Constants.CONTACT_URL;

    	var contact = new Contact();
    	contact.setFirstName("John");
    	contact.setLastName("Brown");
    	
    	var response = client.exchange(url, HttpMethod.POST, getRequest(contact), ServiceError.class);
 		assertTrue(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
 		
    	ServiceError error = response.getBody();
    	assertEquals(1, error.getCode());
    	assertEquals("Either email or phone must be presented", error.getMessage());
    }

    @Test
    void testDuplicateContact() {		
 		var baseUrl = "http://localhost:" + port + Constants.CONTACT_URL;

    	var contact = new Contact();
    	contact.setFirstName("Michael");
    	contact.setLastName("Jordan");
    	contact.setEmail("mj@email.com");
    	contact.setPhone("45-23");

    	// 1. Create the contact
    	ResponseEntity<Void> response = client.exchange(baseUrl, HttpMethod.POST, getRequest(contact), Void.class);
 		assertTrue(response.getStatusCode().equals(HttpStatus.CREATED));

 		var location = response.getHeaders().get(HttpHeaders.LOCATION);
 		assertNotNull(location);
 		assertEquals(1, location.size());
 		
 		var firstContactURL = location.get(0);
 		
 		// 2. Create duplicate 
    	response = client.exchange(baseUrl, HttpMethod.POST, getRequest(contact), Void.class);
 		assertTrue(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
 		
 		// 3. Create new contact  
    	contact = new Contact();
    	contact.setFirstName("Lebron");
    	contact.setLastName("James");
    	contact.setEmail("lbj@email.com");
    	contact.setPhone("23-6");	
    	
    	response = client.exchange(baseUrl, HttpMethod.POST, getRequest(contact), Void.class);
 		assertTrue(response.getStatusCode().equals(HttpStatus.CREATED));

 		location = response.getHeaders().get(HttpHeaders.LOCATION);
 		assertNotNull(location);
 		assertEquals(1, location.size());
 		
 		var secondContactURL = location.get(0);
 		
 		// 4. Duplicate email
 		contact.setEmail("mj@email.com");
 		
    	response = client.exchange(secondContactURL, HttpMethod.PUT, getRequest(contact), Void.class);
 		assertTrue(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
 		
 		// 6. Duplicate phone
    	contact.setEmail("lbj@email.com");
    	contact.setPhone("45-23");
    	
    	response = client.exchange(secondContactURL, HttpMethod.PUT, getRequest(contact), Void.class);
 		assertTrue(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
 		
 		// 7. Delete contacts
 		response = client.exchange(firstContactURL, HttpMethod.DELETE, getRequest(contact), Void.class);
 		assertTrue(response.getStatusCode().equals(HttpStatus.OK));
 		
 		response = client.exchange(secondContactURL, HttpMethod.DELETE, getRequest(contact), Void.class);
 		assertTrue(response.getStatusCode().equals(HttpStatus.OK));
 	}
    
    @Test
    void testCRUDPass() {
 		var baseUrl = "http://localhost:" + port + Constants.CONTACT_URL;

    	var contact = new Contact();
    	contact.setFirstName("John");
    	contact.setLastName("Brown");
    	contact.setEmail("JohnBrown@email.com");
    	contact.setPhone("123456789");

    	// 1. Create the contact
    	ResponseEntity<Void> createResponse = client.exchange(baseUrl, HttpMethod.POST, getRequest(contact), Void.class);
 		assertTrue(createResponse.getStatusCode().equals(HttpStatus.CREATED));
 		
 		// 2. Confirm its there
 		var location = createResponse.getHeaders().get(HttpHeaders.LOCATION);
 		assertNotNull(location);
 		assertEquals(1, location.size());
 		
 		var contactURL = location.get(0);
 		
 		var getResponse = client.exchange(baseUrl + "?email=JohnBrown@email.com", HttpMethod.GET, getRequest(), Contact.class); 		
 		assertTrue(getResponse.getStatusCode().equals(HttpStatus.OK));
 		
 		contact = getResponse.getBody();
 		assertTrue(contact.getFirstName().equals("John"));
 		assertTrue(contact.getLastName().equals("Brown"));
 		assertTrue(contact.getEmail().equals("JohnBrown@email.com"));
 		assertTrue(contact.getPhone().equals("123456789"));
 		
    	// 3. Update the contact
 		contact.setFirstName("Joe");
 		contact.setLastName("Johnson");
 		contact.setEmail("jj@email.com");
 		contact.setPhone("987654321");
 		
    	var updateResponse = client.exchange(contactURL, HttpMethod.PUT, getRequest(contact), Void.class);
 		assertTrue(updateResponse.getStatusCode().equals(HttpStatus.OK));
 		
 		// 4. Confirm it was updated 		
 		getResponse = client.exchange(baseUrl + "?phone=987654321", HttpMethod.GET, getRequest(), Contact.class);
 		
 		assertTrue(getResponse.getStatusCode().equals(HttpStatus.OK));
 		
 		contact = getResponse.getBody();
 		assertTrue(contact.getFirstName().equals("Joe"));
 		assertTrue(contact.getLastName().equals("Johnson"));
 		assertTrue(contact.getEmail().equals("jj@email.com"));
 		assertTrue(contact.getPhone().equals("987654321")); 		
 		
 		// 5. Delete the contact
 		updateResponse = client.exchange(contactURL, HttpMethod.DELETE, getRequest(contact), Void.class);
 		assertTrue(getResponse.getStatusCode().equals(HttpStatus.OK));
 		
 		// 6. Confirm its not there
 		getResponse = client.getForEntity(contactURL, Contact.class); 		
 		assertTrue(getResponse.getStatusCode().equals(HttpStatus.NOT_FOUND)); 		
    }
    
    private HttpEntity<Contact> getRequest(Contact contact) {
   	 	var headers = new HttpHeaders();
   	 	headers.setContentType(MediaType.APPLICATION_JSON);
    	return new HttpEntity<Contact>(contact, headers);
    }
    
    private HttpEntity<Void> getRequest() {
   	 	var headers = new HttpHeaders();
   	 	headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    	return new HttpEntity<Void>(headers);
    }    
}
