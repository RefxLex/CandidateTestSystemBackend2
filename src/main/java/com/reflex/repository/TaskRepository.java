package com.reflex.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.reflex.model.Task;
import com.reflex.model.User;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
	
	Boolean existsByname(String name);
	
	Optional<Task> findBytaskCodeLanguageId(int langId);
	
	@Query(
			  value = "SELECT * FROM tasks WHERE name LIKE :name% AND deleted = false",
			  nativeQuery = true)
	List<Task> selectByName(@Param("name") String name);
	
	@Query(
			  value = "SELECT * FROM tasks WHERE topic_id = :topicId AND deleted = false",
			  nativeQuery = true)
	List<Task> selectByTopic(@Param("topicId") Long topicId);
	
	@Query(
			  value = "SELECT * FROM tasks WHERE difficulty_id = :levelId AND deleted = false",
			  nativeQuery = true)
	List<Task> selectByLevel(@Param("levelId") Long levelId);
	
	@Query(
			  value = "SELECT * FROM tasks WHERE deleted = false",
			  nativeQuery = true)
	List<Task> selectAll();
	
	@Query(
			  value = "SELECT * FROM tasks WHERE topic_id = :topicId AND name LIKE :name% AND deleted = false",
			  nativeQuery = true)
	List<Task> selectByTopicAndName(@Param("name") String name, @Param("topicId") Long topicId);
	
	@Query(
			  value = "SELECT * FROM tasks WHERE topic_id = :topicId AND difficulty_id = :levelId AND deleted = false",
			  nativeQuery = true)
	List<Task> selectByTopicAndLevel(@Param("levelId") Long levelId, @Param("topicId") Long topicId);
	
	@Query(
			  value = "SELECT * FROM tasks WHERE name LIKE :name% AND difficulty_id = :levelId AND deleted = false",
			  nativeQuery = true)
	List<Task> selectByNameAndLevel(@Param("name") String name, @Param("levelId") Long levelId);
	
	@Query(
			  value = "SELECT * FROM tasks WHERE name LIKE :name% AND topic_id = :topicId AND difficulty_id = :levelId AND deleted = false",
			  nativeQuery = true)
	List<Task> selectByNameAndLevelAndTopic(@Param("name") String name, @Param("topicId") Long topicId, @Param("levelId") Long levelId);
	
}
