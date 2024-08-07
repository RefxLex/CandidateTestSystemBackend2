package com.reflex.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
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
	
	@GetMapping
	@PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<List<Topic>> getAllTopics(){
		List<Topic> topicList = new ArrayList<Topic>();
		topicList = topicRepository.findAll();
		return new ResponseEntity<>(topicList, HttpStatus.OK);
	}
	
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Topic> createTopic(@Valid @RequestBody TopicRequest topicRequest){
		Optional<Topic> duplicateTopic = topicRepository.findByname(topicRequest.getName());
		if(duplicateTopic.isPresent()) {		
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
	
	@DeleteMapping("{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> softDeleteTopic(@PathVariable("id") Long topicId){
		Optional<Topic> oldTopic = topicRepository.findById(topicId);
		if(oldTopic.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No topic found with id=" + topicId);
		}
		topicRepository.delete(oldTopic.get());
		return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
	}
	
}
