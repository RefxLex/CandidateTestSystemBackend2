package com.reflex.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.reflex.model.Role;
import com.reflex.model.User;
import com.reflex.model.enums.ERole;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
	
	Optional<Role> findByname(ERole name);
	
	@Query(
			value = "SELECT * FROM roles WHERE name = :role",
			nativeQuery = true)
	Optional<Role> selectByName(@Param("role") String role);
	
}
