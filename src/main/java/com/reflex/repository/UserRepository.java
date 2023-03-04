package com.reflex.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.reflex.model.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
	
	  Optional<User> findByuserName(String username);

	  Boolean existsByuserName(String username);

	  Boolean existsByemail(String email);
	  
	  Boolean existsBypassword(String password);
	  
	  Boolean existsByphone(String phone);
}
