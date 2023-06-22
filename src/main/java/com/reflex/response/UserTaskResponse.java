package com.reflex.response;

import java.time.Instant;
import java.util.List;
import java.util.ArrayList;


public class UserTaskResponse {

	private Long id;
	
	private String taskName;
			
	private String taskDescription;
	
	private String taskLanguageName;
	
	private Instant assignDate;
	
	private Instant startDate;
	
	private Instant submitDate;
	
	private String timeSpent;
	
	private String comment;
	
	private Integer testsPassed;
	
	private Integer testsFailed;
	
	private Integer overallTestsCount;
	
	private String compilationResult;
	
	private String resultReport;
	
	private boolean completed;
	
	private List<UserTaskSolutionResponse> userTaskSolution = new ArrayList<>();

	public UserTaskResponse() {
		
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getTaskDescription() {
		return taskDescription;
	}

	public void setTaskDescription(String taskDescription) {
		this.taskDescription = taskDescription;
	}

	public String getTaskLanguageName() {
		return taskLanguageName;
	}

	public void setTaskLanguageName(String taskLanguageName) {
		this.taskLanguageName = taskLanguageName;
	}

	public Instant getAssignDate() {
		return assignDate;
	}

	public void setAssignDate(Instant assignDate) {
		this.assignDate = assignDate;
	}

	public Instant getStartDate() {
		return startDate;
	}

	public void setStartDate(Instant startDate) {
		this.startDate = startDate;
	}

	public Instant getSubmitDate() {
		return submitDate;
	}

	public void setSubmitDate(Instant submitDate) {
		this.submitDate = submitDate;
	}

	public String getTimeSpent() {
		return timeSpent;
	}

	public void setTimeSpent(String timeSpent) {
		this.timeSpent = timeSpent;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Integer getTestsPassed() {
		return testsPassed;
	}

	public void setTestsPassed(Integer testsPassed) {
		this.testsPassed = testsPassed;
	}

	public Integer getTestsFailed() {
		return testsFailed;
	}

	public void setTestsFailed(Integer testsFailed) {
		this.testsFailed = testsFailed;
	}

	public Integer getOverallTestsCount() {
		return overallTestsCount;
	}

	public void setOverallTestsCount(Integer overallTestsCount) {
		this.overallTestsCount = overallTestsCount;
	}

	public String getCompilationResult() {
		return compilationResult;
	}

	public void setCompilationResult(String compilationResult) {
		this.compilationResult = compilationResult;
	}

	public String getResultReport() {
		return resultReport;
	}

	public void setResultReport(String resultReport) {
		this.resultReport = resultReport;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<UserTaskSolutionResponse> getUserTaskSolution() {
		return userTaskSolution;
	}

	public void setUserTaskSolution(List<UserTaskSolutionResponse> userTaskSolution) {
		this.userTaskSolution = userTaskSolution;
	}
	
}
