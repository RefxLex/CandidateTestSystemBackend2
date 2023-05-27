package com.reflex.request;

import jakarta.validation.constraints.NotNull;

public class CreateUserTaskRequest {
	
	@NotNull
	private Long taskId;
	
	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}
	
}
