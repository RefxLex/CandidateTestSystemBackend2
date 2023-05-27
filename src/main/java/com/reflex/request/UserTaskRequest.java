package com.reflex.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

public class UserTaskRequest {
	
	@NotEmpty
	private List<SolutionRequest> solution;
	
	private List<UnitTestRequest> unitTest;

	public List<SolutionRequest> getSolution() {
		return solution;
	}

	public void setSolution(List<SolutionRequest> solution) {
		this.solution = solution;
	}

	public List<UnitTestRequest> getUnitTest() {
		return unitTest;
	}

	public void setUnitTest(List<UnitTestRequest> unitTest) {
		this.unitTest = unitTest;
	}
	
}
