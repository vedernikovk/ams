package com.ams.kv.contacts.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<ContactEntity, Long> {
	Optional<ContactEntity> findByEmail(String email);
	Optional<ContactEntity> findByPhone(String phone);
}
