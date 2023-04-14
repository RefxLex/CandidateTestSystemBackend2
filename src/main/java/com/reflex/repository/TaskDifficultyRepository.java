package com.reflex.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.reflex.model.TaskDifficulty;

@Repository
public interface TaskDifficultyRepository extends JpaRepository<TaskDifficulty, Long> {
	
	Optional<TaskDifficulty> findByname(String name);
}
