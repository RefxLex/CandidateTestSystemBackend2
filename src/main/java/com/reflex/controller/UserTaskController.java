package com.reflex.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.reflex.model.Task;
import com.reflex.model.User;
import com.reflex.model.UserTask;
import com.reflex.repository.TaskRepository;
import com.reflex.repository.UserRepository;
import com.reflex.repository.UserTaskRepository;
import com.reflex.request.UserTaskRequest;

import jakarta.validation.Valid;

@CrossOrigin
@RestController
//@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/user-task")
public class UserTaskController {
	
	@Autowired
	UserTaskRepository userTaskRepository;
	
	@Autowired
	TaskRepository taskRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@GetMapping("/{userId}")
	public ResponseEntity<List<UserTask>> getUserTasksByUserId(@PathVariable ("userId") Long userId){
		List<UserTask> userTasksList = new ArrayList<>();
		userTasksList = userTaskRepository.selectByUserId(userId);
		return new ResponseEntity<>(userTasksList, HttpStatus.OK);
	}
	
	@PostMapping("/{userId}")
	@PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<UserTask> createUserTask(
			@PathVariable("userId") Long userId,
			@RequestParam Long taskId,
			@RequestParam int languageId){
		Optional<Task> task = taskRepository.findById(taskId);
		if(task.isPresent()==false) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No task found with taskId=" + taskId);
		}
		Optional<User> user = userRepository.findById(userId);
		if(user.isPresent()==false) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user found with userId=" + userId);
		}
		int overallTestsCount = task.get().getTaskTestInput().size();
		Instant assignDate = Instant.now();
		UserTask newUserTask = new UserTask(user.get(), task.get(), assignDate, languageId, overallTestsCount);
		return new ResponseEntity<>(userTaskRepository.save(newUserTask), HttpStatus.CREATED);
	}
	
	@PutMapping("/start/{id}")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<UserTask> startTask(@PathVariable ("id") Long userTaskId){
		Optional<UserTask> oldUserTask = userTaskRepository.findById(userTaskId);
		if(oldUserTask.isPresent()==false) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No userTask found with id=" + userTaskId);
		}
		UserTask newUserTask = oldUserTask.get();
		newUserTask.setStartDate(Instant.now());
		return new ResponseEntity<>(userTaskRepository.save(newUserTask), HttpStatus.OK);
	}
	
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<UserTask> updateUserTask(@PathVariable("id") Long userTaskId, @RequestBody UserTaskRequest userTaskRequest){
		Optional<UserTask> oldUserTask = userTaskRepository.findById(userTaskId);
		if(oldUserTask.isPresent()==false) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No userTask found with id=" + userTaskId);
		}
		UserTask newUserTask = oldUserTask.get();
		newUserTask.setComment(userTaskRequest.getComment());
		return new ResponseEntity<>(userTaskRepository.save(newUserTask), HttpStatus.OK);
	}
	
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<?> deleteUserTask(@PathVariable("id") Long userTaskId){
		Optional<UserTask> userTask = userTaskRepository.findById(userTaskId);
		if(userTask.isPresent()==false) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No userTask found with id=" + userTaskId);
		}
		userTaskRepository.deleteById(userTaskId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
}
