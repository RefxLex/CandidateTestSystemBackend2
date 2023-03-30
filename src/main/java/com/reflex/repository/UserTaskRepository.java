package com.reflex.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.reflex.model.UserTask;

@Repository
public interface UserTaskRepository extends JpaRepository<UserTask, Long> {
	
	  @Query(
			  value = "SELECT * FROM user_tasks WHERE user_id = :userId",
			  nativeQuery = true)
	  List<UserTask> selectByUserId(@Param("userId") Long userId);
	  
	  @Query(
			  value = "SELECT * FROM user_tasks WHERE assign_date >= :day AND assign_date < :next_day ",
			  nativeQuery = true)
	  List<UserTask> selectByLastAssingDate(@Param("day") Instant day, @Param("next_day") Instant nextDay);
	  //List<UserTask> selectByLastAssingDate(@Param("day") String day, @Param("next_day") String nextDay);
	  	
}
