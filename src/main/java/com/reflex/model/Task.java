package com.reflex.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.reflex.model.enums.TaskDifficulty;

import jakarta.persistence.*;

@Entity
@Table(name="tasks")
public class Task {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable=false, unique=true)
	private String name;
	
	@ManyToOne(fetch=FetchType.EAGER, optional=false)
	@JoinColumn(name="topic_id", nullable=false)
	@OnDelete(action=OnDeleteAction.NO_ACTION)
	private Topic topic;
	
	@Column(name="task_difficulty", nullable=false)
	@Enumerated(EnumType.STRING)
	private TaskDifficulty taskDifficulty;
	
	@Column(nullable=false)
	private String description;
	
	@Column(nullable=false)
	private String tests;

	public Task() {
		
	}
	
	public Task(String name, Topic topic, TaskDifficulty taskDifficulty, String description, String tests) {

		this.name = name;
		this.topic = topic;
		this.taskDifficulty = taskDifficulty;
		this.description = description;
		this.tests = tests;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Topic getTopic() {
		return topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}

	public TaskDifficulty getTaskDifficulty() {
		return taskDifficulty;
	}

	public void setTaskDifficulty(TaskDifficulty taskDifficulty) {
		this.taskDifficulty = taskDifficulty;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTests() {
		return tests;
	}

	public void setTests(String tests) {
		this.tests = tests;
	}
	
}
