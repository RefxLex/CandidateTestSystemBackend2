package com.reflex.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.reflex.model.UserTaskResult;


@Repository
public interface UserTaskResultRepository extends JpaRepository<UserTaskResult, Long> {
	
	  @Query(
			  value = "SELECT * FROM user_task_result WHERE user_task_id = :userTaskId",
			  nativeQuery = true)
	  List<UserTaskResult> selectByUserTaskId(@Param("userTaskId") Long userTaskId);
	
}
