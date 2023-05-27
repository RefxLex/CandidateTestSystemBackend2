package com.reflex.request;


import java.util.List;

import jakarta.validation.constraints.NotBlank;
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
	
	@NotNull
	private int taskCodeLanguageId;
	
	@NotBlank
	private String languageName;
	
	private List<UnitTestRequest> unitTest;
	
	private List<SolutionRequest> refSolution;
	
	//private List<TestInputRequest> taskTestInput;

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

	public Long getDifficultyId() {
		return difficultyId;
	}

	public void setDifficultyId(Long difficultyId) {
		this.difficultyId = difficultyId;
	}

	public int getTaskCodeLanguageId() {
		return taskCodeLanguageId;
	}

	public void setTaskCodeLanguageId(int taskCodeLanguageId) {
		this.taskCodeLanguageId = taskCodeLanguageId;
	}

	public String getLanguageName() {
		return languageName;
	}

	public void setLanguageName(String languageName) {
		this.languageName = languageName;
	}

	public List<UnitTestRequest> getUnitTest() {
		return unitTest;
	}

	public void setUnitTest(List<UnitTestRequest> unitTest) {
		this.unitTest = unitTest;
	}

	public List<SolutionRequest> getRefSolution() {
		return refSolution;
	}

	public void setRefSolution(List<SolutionRequest> refSolution) {
		this.refSolution = refSolution;
	}
	
}
