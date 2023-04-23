package com.reflex.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.reflex.model.Task;
import com.reflex.model.TaskDifficulty;
import com.reflex.model.TaskTestInput;
import com.reflex.model.Topic;
import com.reflex.model.User;
import com.reflex.model.enums.UserStatus;
import com.reflex.repository.TaskDifficultyRepository;
import com.reflex.repository.TaskRepository;
import com.reflex.repository.TaskTestInputRepository;
import com.reflex.repository.TopicRepository;
import com.reflex.request.TaskRequest;
import com.reflex.request.TestInputRequest;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api/task")
public class TaskController {
	
	@Autowired
	TaskRepository taskRepository;
	
	@Autowired 
	TopicRepository topicRepository;
	
	@Autowired
	TaskDifficultyRepository taskDifficultyRepository;
	
	@Autowired
	TaskTestInputRepository taskTestInputRepository;
	
	@GetMapping("/{id}")
	public ResponseEntity<Task> getTaskById(@PathVariable ("id") Long id){
        Optional<Task> task = Optional.ofNullable(taskRepository.findById(id).orElseThrow(() ->
    	new ResponseStatusException(HttpStatus.NOT_FOUND, "No task found with id=" + id)));
        return new ResponseEntity<>(task.get(), HttpStatus.OK);
	}
	
	
	@GetMapping("/filter")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<List<Task>> getTaskByFilter(
			@RequestParam String name,
			@RequestParam String topic_id,
			@RequestParam String level_id) throws NumberFormatException {
		
		List<Task> tasks = new ArrayList<Task>();

		if( (name!="") && (topic_id=="") && (level_id=="")) {
			tasks = taskRepository.selectByName(name);
		}
		else if((name!="") && (topic_id!="") && (level_id=="")) {
			tasks = taskRepository.selectByTopicAndName(name, Long.parseLong(topic_id));
		}
		else if((name=="") && (topic_id!="") && (level_id!="")) {
			tasks = taskRepository.selectByTopicAndLevel(Long.parseLong(level_id), Long.parseLong(topic_id));
		}
		else if((name!="") && (topic_id=="") && (level_id!="")) {
			tasks = taskRepository.selectByNameAndLevel(name, Long.parseLong(level_id));
		}
		else if((name!="") && (topic_id!="") && (level_id!="")) {
			tasks = taskRepository.selectByNameAndLevelAndTopic(name, Long.parseLong(topic_id), Long.parseLong(level_id));
		}
		else if((name=="") && (topic_id!="") && (level_id=="")) {
			tasks = taskRepository.selectByTopic(Long.parseLong(topic_id));
		}
		else if((name=="") && (topic_id=="") && (level_id!="")) {
			tasks = taskRepository.selectByLevel(Long.parseLong(level_id));
		}
		else {
			tasks = taskRepository.selectAll();
		}
		
	    return new ResponseEntity<>(tasks, HttpStatus.OK);
	}
	
	// For server side pagination
	/*
	@GetMapping("/filter")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<Map<String, Object>> getTaskByFilter(
			@RequestParam (required=false) String name,
			@RequestParam (required=false) Long topic,
			@RequestParam (required=false) String level,
			@RequestParam (defaultValue = "0") int page,
			@RequestParam (defaultValue = "10") int size,
    		@RequestParam (defaultValue = "ASC") String direction){
		
		List<Task> tasks = new ArrayList<Task>();

		Pageable paging = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), "name"));		
		Page<Task> pageTasks;

		if( (name!=null) && (topic==null) && (level==null)) {
			pageTasks = taskRepository.selectByNameWithPagination(name, paging);
		}
		else if((name!=null) && (topic!=null) && (level==null)) {
			pageTasks = taskRepository.selectByTopicAndNameWithPagination(name, topic, paging);
		}
		else if((name==null) && (topic!=null) && (level!=null)) {
			pageTasks = taskRepository.selectByTopicAndLevelWithPagination(level, topic, paging);
		}
		else if((name!=null) && (topic==null) && (level!=null)) {
			pageTasks = taskRepository.selectByNameAndLevelWithPagination(name, level, paging);
		}
		else if((name!=null) && (topic!=null) && (level!=null)) {
			pageTasks = taskRepository.selectByNameAndLevelAndTopicWithPagination(name, topic, level, paging);
		}
		else if((name==null) && (topic!=null) && (level==null)) {
			pageTasks = taskRepository.selectByTopicWithPagination(topic, paging);
		}
		else if((name==null) && (topic==null) && (level!=null)) {
			pageTasks = taskRepository.selectByLevelWithPagination(level, paging);
		}
		else {
			pageTasks = taskRepository.selectAllWithPagination(paging);
		}

		try {
        
			tasks = pageTasks.getContent();			
	        Map<String, Object> response = new HashMap<>();	        
	        response.put("tasks", tasks);	        
	        response.put("currentPage", pageTasks.getNumber());	        
	        response.put("totalItems", pageTasks.getTotalElements());      
	        response.put("totalPages", pageTasks.getTotalPages());
	        return new ResponseEntity<>(response, HttpStatus.OK);
		}
		catch(Exception e){
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	} */
	
	@GetMapping("/difficulty")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<TaskDifficulty>> getAllDifficultyLevels(){
		List<TaskDifficulty> difficulties = taskDifficultyRepository.findAll();
	    return new ResponseEntity<>(difficulties, HttpStatus.OK);
	}
	
	@PostMapping("/create")
	@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Task> createTask(@Valid @RequestBody TaskRequest taskRequest) {
		
		if(taskRepository.existsByname(taskRequest.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task name already taken");
		}
        Optional<Topic> topic = Optional.ofNullable(topicRepository.findById(taskRequest.getTopicId()).orElseThrow(() ->
        	new ResponseStatusException(HttpStatus.NOT_FOUND, "No topic found with id=" + taskRequest.getTopicId() )));

        Optional<TaskDifficulty> difficulty = Optional.ofNullable(taskDifficultyRepository.findById(taskRequest.getDifficultyId()).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, "No difficulty level found with id=" + taskRequest.getDifficultyId() )));
        
        Task task = new Task(
        		taskRequest.getName(),
        		topic.get(),
        		difficulty.get(),
        		taskRequest.getDescription());
        
        List<TestInputRequest> testInputList = taskRequest.getTaskTestInput();
        for(int i=0; i<testInputList.size(); i++) {
        	String input = testInputList.get(i).getInput();
        	String output = testInputList.get(i).getOutput();
        	TaskTestInput testInput = new TaskTestInput(input, output);
        	task.getTaskTestInput().add(testInput);
        }
        return new ResponseEntity<>(taskRepository.save(task), HttpStatus.CREATED);
    }
	
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Task> updateTask(@PathVariable("id") Long id, @Valid @RequestBody TaskRequest taskRequest){
		
		Optional<Task> oldTask = Optional.ofNullable(taskRepository.findById(id).orElseThrow(() ->
		new ResponseStatusException(HttpStatus.NOT_FOUND, "No task found with id=" + id)));
		Task newTask = oldTask.get();
				
        Optional<Topic> topic = Optional.ofNullable(topicRepository.findById(taskRequest.getTopicId()).orElseThrow(() ->
    	new ResponseStatusException(HttpStatus.NOT_FOUND, "No topic found with id=" + taskRequest.getTopicId() )));
        
        Optional<TaskDifficulty> difficulty = Optional.ofNullable(taskDifficultyRepository.findById(taskRequest.getDifficultyId()).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, "No difficulty level found with id=" + taskRequest.getDifficultyId() )));
        
        newTask.getTaskTestInput().clear();
        List<TestInputRequest> testInputList = taskRequest.getTaskTestInput();
        for(int i=0; i<testInputList.size(); i++) {
        	String input = testInputList.get(i).getInput();
        	String output = testInputList.get(i).getOutput();
        	TaskTestInput testInput = new TaskTestInput(input, output);
        	newTask.getTaskTestInput().add(testInput);
        }
        
	    newTask.setName(taskRequest.getName());
	    newTask.setTopic(topic.get());
	    newTask.setTaskDifficulty(difficulty.get());
	    newTask.setDescription(taskRequest.getDescription());
	    return new ResponseEntity<>(taskRepository.save(newTask), HttpStatus.OK);
	}
	
	//soft delete
	@PutMapping("/delete/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Task> deleteTask(@PathVariable ("id") Long id){
		Optional<Task> oldTask = Optional.ofNullable(taskRepository.findById(id).orElseThrow(() ->
		new ResponseStatusException(HttpStatus.NOT_FOUND, "No task found with id=" + id)));
        Task newTask = oldTask.get();
        newTask.setDeleted(true);
        return new ResponseEntity<>(taskRepository.save(newTask), HttpStatus.OK);
	}
	
	
	
	
	

}
