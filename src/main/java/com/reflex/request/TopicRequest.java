package com.reflex.request;

import jakarta.validation.constraints.NotBlank;

public class TopicRequest {
	
	@NotBlank
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
