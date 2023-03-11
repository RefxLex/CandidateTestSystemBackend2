package com.reflex.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import com.reflex.model.Topic;
import com.reflex.model.User;
import com.reflex.model.enums.TaskDifficulty;
import com.reflex.repository.TaskRepository;
import com.reflex.repository.TopicRepository;
import com.reflex.request.TaskRequest;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/task")
public class TaskController {
	
	@Autowired
	TaskRepository taskRepository;
	
	@Autowired 
	TopicRepository topicRepository;
	
	@GetMapping("/{id}")
	public ResponseEntity<Task> getTaskById(@PathVariable ("id") Long id){
        Optional<Task> task = Optional.ofNullable(taskRepository.findById(id).orElseThrow(() ->
    	new ResponseStatusException(HttpStatus.NOT_FOUND, "No task found with id=" + id)));
        return new ResponseEntity<>(task.get(), HttpStatus.OK);
	}
	
	@GetMapping("/find")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<Map<String, Object>> getTaskByFilter(
			@RequestParam (required=false) String name,
			@RequestParam String topic,
			@RequestParam String difficulty,
			@RequestParam (defaultValue = "0") int page,
			@RequestParam (defaultValue = "10") int size){
		
		List<Task> tasks = new ArrayList<Task>();
		Pageable paging = PageRequest.of(page, size);
		Page<Task> pageTasks;
		try {
			if(name==null) {
				pageTasks = taskRepository.findBytopicIdAndBytaskDifficultyAndBynameLikeOrderBynameDesc(topic, difficulty, name, paging);
			}
			else {
				pageTasks = taskRepository.findBytopicIdAndBytaskDifficultyOrderBynameDesc(topic, difficulty, paging);
			}
			
			tasks = pageTasks.getContent();
	        Map<String, Object> response = new HashMap<>();
	        response.put("tutorials", tasks);
	        response.put("currentPage", pageTasks.getNumber());
	        response.put("totalItems", pageTasks.getTotalElements());
	        response.put("totalPages", pageTasks.getTotalPages());
	        return new ResponseEntity<>(response, HttpStatus.OK);
			
			
		}
    	catch (Exception e) {
    		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    	}
			
	}
	
	@GetMapping("/difficulty")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<TaskDifficulty>> getAllDifficultyLevels(){
		List<TaskDifficulty> difficulties = Arrays.asList(TaskDifficulty.values());
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

        Optional<TaskDifficulty> difficulty = TaskDifficulty.byNameIgnoreCase(taskRequest.getTaskDifficulty());
        if(difficulty.isPresent()) {
            Task task = new Task(taskRequest.getName(), topic.get(), difficulty.get(), taskRequest.getDescription(), taskRequest.getTests());
            return new ResponseEntity<>(taskRepository.save(task), HttpStatus.CREATED);
        }
        else {
        	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No difficulty level found with name=" + taskRequest.getTaskDifficulty());
        }
        
    }
	
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Task> updateTask(@PathVariable("id") Long id, @Valid @RequestBody TaskRequest taskRequest){
		Optional<Task> oldTask = Optional.ofNullable(taskRepository.findById(id).orElseThrow(() ->
		new ResponseStatusException(HttpStatus.NOT_FOUND, "No task found with id=" + id)));
		
		if(taskRepository.existsByname(taskRequest.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task name already taken");
		}
        Optional<Topic> topic = Optional.ofNullable(topicRepository.findById(taskRequest.getTopicId()).orElseThrow(() ->
    	new ResponseStatusException(HttpStatus.NOT_FOUND, "No topic found with id=" + taskRequest.getTopicId() )));

	    Optional<TaskDifficulty> difficulty = TaskDifficulty.byNameIgnoreCase(taskRequest.getTaskDifficulty());
	    if(difficulty.isPresent()) {
	    	
	        Task newTask = oldTask.get();
	        newTask.setName(taskRequest.getName());
	        newTask.setTopic(topic.get());
	        newTask.setTaskDifficulty(difficulty.get());
	        newTask.setDescription(taskRequest.getDescription());
	        newTask.setTests(taskRequest.getTests());
	  
	        return new ResponseEntity<>(taskRepository.save(newTask), HttpStatus.OK);
	    }
	    else {
	    	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No difficulty level found with name=" + taskRequest.getTaskDifficulty());
	    }
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
