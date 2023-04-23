package com.reflex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.reflex.model.TaskTestInput;

@Repository
public interface TaskTestInputRepository extends JpaRepository<TaskTestInput, Long> {

}
