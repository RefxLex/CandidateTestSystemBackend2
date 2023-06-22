package com.reflex.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.reflex.model.TaskDifficulty;
import com.reflex.repository.TaskDifficultyRepository;
import com.reflex.request.TopicRequest;

import jakarta.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api/level")
public class TaskDifficultyController {
	
	@Autowired
	TaskDifficultyRepository taskDifficultyRepository;
	
	@GetMapping("/{id}")
	@PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<TaskDifficulty> getLevelById(@PathVariable("id") Long levelId){
		Optional<TaskDifficulty> taskDifficulty = taskDifficultyRepository.findById(levelId);
		if(taskDifficulty.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No level found with id=" + levelId);
		}
		return new ResponseEntity<>(taskDifficulty.get(), HttpStatus.OK);
	}
	
	@GetMapping("/all")
	@PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<List<TaskDifficulty>> getAllLevels(){
		List<TaskDifficulty> taskDifficultyList = taskDifficultyRepository.findAll();
		return new ResponseEntity<>(taskDifficultyList, HttpStatus.OK);
	}
	
	@PostMapping("/create")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<TaskDifficulty> createLevel(@Valid @RequestBody TopicRequest levelRequest){
		Optional<TaskDifficulty> duplicateTaskDifficulty = taskDifficultyRepository.findByname(levelRequest.getName());
		if(duplicateTaskDifficulty.isPresent()) {
			if(duplicateTaskDifficulty.get().isDeleted()) {
				duplicateTaskDifficulty.get().setDeleted(false);
				return new ResponseEntity<>(taskDifficultyRepository.save(duplicateTaskDifficulty.get()), HttpStatus.OK);
			}else {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate task difficulty");
			}
		}
		TaskDifficulty taskDifficulty = new TaskDifficulty(levelRequest.getName());
		return new ResponseEntity<>(taskDifficultyRepository.save(taskDifficulty), HttpStatus.CREATED);
	}
	
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<TaskDifficulty> updateLevel(@PathVariable("id") Long levelId, @Valid @RequestBody TopicRequest levelRequest){
		Optional<TaskDifficulty> oldTaskDifficulty = taskDifficultyRepository.findById(levelId);
		if(oldTaskDifficulty.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No level found with id=" + levelId);
		}
		TaskDifficulty newTaskDiffiuclty = oldTaskDifficulty.get();
		newTaskDiffiuclty.setName(levelRequest.getName());
		return new ResponseEntity<>(taskDifficultyRepository.save(newTaskDiffiuclty), HttpStatus.OK);
	}
	
	@PutMapping("/delete/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<TaskDifficulty> softDelLevel(@PathVariable("id") Long levelId){
		Optional<TaskDifficulty> oldTaskDifficulty = taskDifficultyRepository.findById(levelId);
		if(oldTaskDifficulty.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,  "No level found with id=" + levelId);
		}
		TaskDifficulty newTaskDifficulty = oldTaskDifficulty.get();
		newTaskDifficulty.setDeleted(true);
		taskDifficultyRepository.save(newTaskDifficulty);
		return new ResponseEntity<>(null, HttpStatus.OK);	
	}
	
	
}
