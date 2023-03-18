package com.reflex.request;

import java.util.ArrayList;
import java.util.Set;

import com.reflex.model.TaskTestInput;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
	
	@NotEmpty
	private Set<TaskTestInput> taskTestInput;

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

	public Set<TaskTestInput> getTaskTestInput() {
		return taskTestInput;
	}

	public void setTaskTestInput(Set<TaskTestInput> taskTestInput) {
		this.taskTestInput = taskTestInput;
	}

}
