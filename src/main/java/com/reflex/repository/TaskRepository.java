package com.reflex.repository;

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
	
	//Page<User> findByuserStatusContainingOrderByfirstNameDesc(String userStatus, Pageable pageable);
	//@Query("SELECT * FROM tasks WHERE topic_id = topicId AND ")
	//Page<Task> selectLikeTopicAndDifficulty (@Param ("topic_id")Long topicId, @Param ("difficulty") String difficulty, Pageable pageable);
	
	Page<Task> findBytopicIdAndBytaskDifficultyOrderBynameDesc (String topicId, String taskDifficulty, Pageable pageable);
	
	Page<Task> findBytopicIdAndBytaskDifficultyAndBynameLikeOrderBynameDesc (String topicId, String taskDifficulty, String name, Pageable pageable);
}
