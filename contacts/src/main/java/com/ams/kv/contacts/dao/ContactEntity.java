package com.ams.kv.contacts.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="CONTACT")
public class ContactEntity extends BaseEntity {

	private Long id;
	private String firstName;
	private String lastName;
	private String email;
	private String phone;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="contactId")
	@SequenceGenerator(name = "contactId", allocationSize = 1, sequenceName = "CONTACT_SEQ")	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="FIRST_NAME", nullable=false)
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	@Column(name="LAST_NAME", nullable=false)
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	@Column(name="PHONE", nullable=true, unique=true)
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone= phone;
	}
	
	@Column(name="EMAIL", nullable=true, unique=true)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
