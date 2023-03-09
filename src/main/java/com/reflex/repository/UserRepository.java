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
	  
	  //@Query("SELECT (id, email, user_name, first_name, second_name, last_name, info, phone, user_status) FROM user_profile WHERE user_status = status LIMIT rows")
	  //List<User> selectUserByStatus(@Param("status") String status, @Param("rows") int rowsNumber);
	  
	  Page<User> findByuserStatusContainingOrderByfirstNameDesc(String userStatus, Pageable pageable);
		
	  Page<User> findByuserStatusContainingOrderBylastActivityDesc(String userStatus, Pageable pageable);
}
