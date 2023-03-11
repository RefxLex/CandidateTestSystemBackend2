package com.reflex.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TaskRequest {
	
	@NotBlank
	@Size(max=50)
	private String name;
	
	@NotBlank
	private Long topicId;
	
	@NotBlank
	@Size(max=30)
	private String taskDifficulty;
	
	private String description;
	
	@NotBlank
	private String tests;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getTopicId() {
		return topicId;
	}

	public void setTopicId(Long topicId) {
		this.topicId = topicId;
	}

	public String getTaskDifficulty() {
		return taskDifficulty;
	}

	public void setTaskDifficulty(String taskDifficulty) {
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
