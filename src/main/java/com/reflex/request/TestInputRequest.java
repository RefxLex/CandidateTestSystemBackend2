package com.reflex.request;

import jakarta.validation.constraints.NotBlank;

public class TestInputRequest {
	
	private Long id;
	
	@NotBlank
	private String input;
	
	@NotBlank
	private String output;

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
}
