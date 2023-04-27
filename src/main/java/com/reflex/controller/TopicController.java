package com.reflex.controller;

import java.util.ArrayList;
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

import com.reflex.model.Topic;
import com.reflex.repository.TopicRepository;
import com.reflex.request.TopicRequest;

import jakarta.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api/topic")
public class TopicController {
	
	@Autowired
	TopicRepository topicRepository;
	
	@GetMapping("/{id}")
	@PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<Topic> getTopicById(@PathVariable("id") Long id){
        Optional<Topic> topic = Optional.ofNullable(topicRepository.findById(id).orElseThrow(() ->
        	new ResponseStatusException(HttpStatus.NOT_FOUND, "No such Topic")));
        return new ResponseEntity<>(topic.get(), HttpStatus.OK);
	}
	
	@GetMapping("/all")
	@PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<List<Topic>> getAllTopics(){
		List<Topic> topicList = new ArrayList<Topic>();
		topicList = topicRepository.findAll();
		return new ResponseEntity<>(topicList, HttpStatus.OK);
	}
	
	@PostMapping("/create")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Topic> createTopic(@Valid @RequestBody TopicRequest topicRequest){
		
		if(topicRepository.existsByname(topicRequest.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Topic already exist");
		}
		Topic topic = new Topic(topicRequest.getName());
		return new ResponseEntity<>(topicRepository.save(topic), HttpStatus.CREATED);
	}
	
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Topic> updateTopic(@PathVariable("id") Long id, @Valid @RequestBody TopicRequest topicRequest){
		if(topicRepository.existsById(id)==false) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No topic found with id=" + id);
		}
		Optional<Topic> oldTopic = topicRepository.findById(id);
		Topic newTopic = oldTopic.get();
		newTopic.setName(topicRequest.getName());
		return new ResponseEntity<>(topicRepository.save(newTopic), HttpStatus.OK);
	}
	
	// soft delete
	/*
	@PutMapping("/delete/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> deleteTopic(@PathVariable("id") Long id){
		if(topicRepository.existsById(id)==false) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No topic found with id=" + id);
		}
		Optional<Topic> oldTopic = topicRepository.findById(id);
		Topic newTopic = oldTopic.get();
		newTopic.setDeleted(true);
		return new ResponseEntity<>(topicRepository.save(newTopic), HttpStatus.OK);
	} */
	
}
