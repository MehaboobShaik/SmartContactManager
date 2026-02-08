package com.contactManager.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.contactManager.entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	public Optional<User> findByEmail(String email);
}
