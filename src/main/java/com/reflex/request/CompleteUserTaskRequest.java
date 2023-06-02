package com.reflex.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

public class CompleteUserTaskRequest {
	
	@NotEmpty
	private List<SolutionRequest> solution;
	
	public List<SolutionRequest> getSolution() {
		return solution;
	}

	public void setSolution(List<SolutionRequest> solution) {
		this.solution = solution;
	}
	
}
