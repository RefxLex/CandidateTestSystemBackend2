package com.reflex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.reflex.model.Topic;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
	
	Boolean existsByname(String name);
}
