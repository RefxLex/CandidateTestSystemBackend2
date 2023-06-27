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
			  value = "SELECT * FROM user_profiles WHERE email = :email AND id != :userId",
			  nativeQuery = true)
	  List<User> selectByEmailExcludeOneById(@Param("email") String email, @Param("userId") Long userId);
	  
	  @Query(
			  value = "SELECT * FROM user_profiles WHERE phone = :phone AND id != :userId",
			  nativeQuery = true)
	  List<User> selectByPhoneExcludeOneById(@Param("phone") String phone, @Param("userId") Long userId);
	  
	  @Query(
			  value = "SELECT * FROM user_profiles WHERE user_status = :status AND user_status != 'none' ",
			  nativeQuery = true)
	  List<User> selectByUserStatus(@Param("status") String status);
	  
	  @Query(
			  value = "SELECT * FROM user_profiles WHERE full_name LIKE :name% AND user_status != 'none' ",
			  nativeQuery = true)
	  List<User> selectByUserName(@Param("name") String name);
	  
	  @Query(
			  value = "SELECT * FROM user_profiles WHERE user_status != 'none' ",
			  nativeQuery = true)
	  List<User> selectAll();
	  
	  @Query(
			  value = "SELECT * FROM user_profiles WHERE user_status = :status AND full_name LIKE :name% AND user_status != 'none' ",
			  nativeQuery = true)
	  List<User> selectByUserStatusAndUserName(@Param("name") String name, @Param("status") String status);
	  
	  @Query(
			  value = "SELECT * FROM user_profiles WHERE user_status = 'none' ",
			  nativeQuery = true)
	  List<User> selectByUserStatusNone();
	  
}
