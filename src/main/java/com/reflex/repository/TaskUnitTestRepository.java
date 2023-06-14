package com.reflex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.reflex.model.TaskUnitTest;

@Repository
public interface TaskUnitTestRepository extends JpaRepository<TaskUnitTest, Long> {

}
