package com.reflex.controller;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
import com.reflex.model.TaskReferenceSolution;
//import com.reflex.model.TaskTestInput;
import com.reflex.model.TaskUnitTest;
import com.reflex.model.Topic;
import com.reflex.model.User;
import com.reflex.model.enums.SupportedLanguages;
import com.reflex.model.enums.UserStatus;
import com.reflex.repository.TaskDifficultyRepository;
import com.reflex.repository.TaskRepository;
//import com.reflex.repository.TaskTestInputRepository;
import com.reflex.repository.TopicRepository;
import com.reflex.request.SolutionRequest;
import com.reflex.request.TaskRequest;
//import com.reflex.request.TestInputRequest;
import com.reflex.request.UnitTestRequest;

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
		
	@GetMapping("/{id}")
	@PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<Task> getTaskById(@PathVariable ("id") Long id){
        Optional<Task> task = Optional.ofNullable(taskRepository.findById(id).orElseThrow(() ->
    	new ResponseStatusException(HttpStatus.NOT_FOUND, "No task found with id=" + id)));
        return new ResponseEntity<>(task.get(), HttpStatus.OK);
	}
	
	@GetMapping("/languages")
	public ResponseEntity<?> getSupportedLanguages(){
		List<SupportedLanguages> langList = Arrays.asList(SupportedLanguages.class.getEnumConstants());
        return new ResponseEntity<>(langList, HttpStatus.OK);
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
	
	@PostMapping("/create")
	@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Task> createTask(@Valid @RequestBody TaskRequest taskRequest) {
		
        Optional<Topic> topic = Optional.ofNullable(topicRepository.findById(taskRequest.getTopicId()).orElseThrow(() ->
        	new ResponseStatusException(HttpStatus.NOT_FOUND, "No topic found with id=" + taskRequest.getTopicId() )));

        Optional<TaskDifficulty> difficulty = Optional.ofNullable(taskDifficultyRepository.findById(taskRequest.getDifficultyId()).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, "No difficulty level found with id=" + taskRequest.getDifficultyId() )));
        
        Optional<SupportedLanguages> lang = SupportedLanguages.byNameIgnoreCase(taskRequest.getLanguageName());
        int langCode = 0;
        if(lang.isPresent()) {
        	langCode = lang.get().getCode();
        }else {
        	throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        
        Task task = new Task(
        		taskRequest.getName(),
        		topic.get(),
        		difficulty.get(),
        		taskRequest.getDescription(),
        		langCode,
        		taskRequest.getLanguageName());

        List<UnitTestRequest> unitTestList = taskRequest.getUnitTest();
        List<SolutionRequest> refSolutiomList = taskRequest.getRefSolution();
        for(UnitTestRequest iterator: unitTestList) {
        	TaskUnitTest unitTest = new TaskUnitTest(iterator.getCode());
        	task.getUnitTest().add(unitTest);
        }
        for(SolutionRequest iterator: refSolutiomList) {
        	TaskReferenceSolution refSolution = new TaskReferenceSolution(iterator.getCode());
        	task.getRefSolution().add(refSolution);
        }
             
        Task savedTask = null;
        try {
        	savedTask = taskRepository.save(task);
        }
        catch(DataIntegrityViolationException e) {
        	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate task");
        }
        
        return new ResponseEntity<>(savedTask, HttpStatus.CREATED);
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
        
        Optional<SupportedLanguages> lang = SupportedLanguages.byNameIgnoreCase(taskRequest.getLanguageName());
        int langCode = 0;
        if(lang.isPresent()) {
        	langCode = lang.get().getCode();
        }else {
        	throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        
	    newTask.setName(taskRequest.getName());
	    newTask.setTopic(topic.get());
	    newTask.setTaskDifficulty(difficulty.get());
	    newTask.setDescription(taskRequest.getDescription());
	    newTask.setTaskCodeLanguageId(langCode);
	    newTask.setLanguageName(taskRequest.getLanguageName());
        
        newTask.getRefSolution().clear();
        newTask.getUnitTest().clear();
        List<UnitTestRequest> unitTestList = taskRequest.getUnitTest();
        List<SolutionRequest> refSolutiomList = taskRequest.getRefSolution();
        for(UnitTestRequest iterator: unitTestList) {
        	TaskUnitTest unitTest = new TaskUnitTest(iterator.getCode());
        	newTask.getUnitTest().add(unitTest);
        }
        for(SolutionRequest iterator: refSolutiomList) {
        	TaskReferenceSolution refSolution = new TaskReferenceSolution(iterator.getCode());
        	newTask.getRefSolution().add(refSolution);
        }
              
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
