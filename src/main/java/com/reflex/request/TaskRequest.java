package com.reflex.request;


import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TaskRequest {
	
	@NotBlank
	@Size(max=50)
	private String name;
	
	@NotNull
	private Long topicId;
	
	@NotNull
	private Long difficultyId;
	
	private String description;
	
	@NotEmpty
	private List<TestInputRequest> taskTestInput;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<TestInputRequest> getTaskTestInput() {
		return taskTestInput;
	}

	public void setTaskTestInput(List<TestInputRequest> taskTestInput) {
		this.taskTestInput = taskTestInput;
	}

	public Long getDifficultyId() {
		return difficultyId;
	}

	public void setDifficultyId(Long difficultyId) {
		this.difficultyId = difficultyId;
	}
	
}
