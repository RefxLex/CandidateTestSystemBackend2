package com.reflex.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.reflex.model.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
	
	  Optional<User> findByuserName(String username);
	  
	  Optional<User> findByemail(String email);

	  Boolean existsByuserName(String username);

	  Boolean existsByemail(String email);
	  
	  Boolean existsBypassword(String password);
	  
	  Boolean existsByphone(String phone);
	  
	  @Query(
			  value = "SELECT * FROM user_profile WHERE user_status = :status AND deleted = false",
			  countQuery = "SELECT count(*) FROM user_profile",
			  nativeQuery = true)
	  Page<User> selectByUserStatusWithPagination(@Param("status") String status, Pageable pageable);
	  
	  @Query(
			  value = "SELECT * FROM user_profile WHERE full_name LIKE :name% AND deleted = false",
			  countQuery = "SELECT count(*) FROM user_profile",
			  nativeQuery = true)
	  Page<User> selectByUserNameWithPagination(@Param("name") String name, Pageable pageable);
	  

}
