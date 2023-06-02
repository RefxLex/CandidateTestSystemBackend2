package com.reflex.model;

import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
@Table(name="user_tasks")
public class UserTask {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@JoinColumn(name="user_id", nullable=false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JsonIgnore
	private User user;
	
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@JoinColumn(name="task_id", nullable=false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private Task task;
	
	@Column(name="assign_date", nullable=false, columnDefinition="timestamptz")
	private Instant assignDate;
	
	@Column(name="start_date", nullable=true, columnDefinition="timestamptz")
	private Instant startDate;
	
	@Column(name="submit_date", nullable=true, columnDefinition="timestamptz")
	private Instant submitDate;
	
	@Column(name="time_spent", nullable=true)
	private String timeSpent;
	
	@Column(nullable=true, columnDefinition="TEXT")
	private String comment;
	
	@Column(name="tests_passed", nullable=false)
	private Integer testsPassed;
	
	@Column(name="tests_failed", nullable=false)
	private Integer testsFailed;
	
	@Column(name="overall_test_count", nullable=false)
	private Integer overallTestsCount;
	
	@Column(nullable=true)
	private String compilationResult;
	
	@Column(name="result_report", columnDefinition="TEXT", nullable=true)
	private String resultReport;
	
	@Column(nullable=false)
	private boolean completed = false;
	
	@Column(nullable=false)
	private boolean analyzed = false;
	
	@Column(nullable=true)
	private String sonarKey ="";
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name="user_task_id")
	private Set<UserTaskSolution> userTaskSolution = new HashSet<>();
	
	public UserTask() {
		
	}

	public UserTask(User user, Task task, Instant assignDate) {
		
		this.user = user;
		this.task = task;
		this.assignDate = assignDate;
		this.overallTestsCount = 0;
		this.testsPassed = 0;
		this.testsFailed = 0;
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Integer getTestsFailed() {
		return testsFailed;
	}

	public void setTestsFailed(Integer testsFailed) {
		this.testsFailed = testsFailed;
	}

	public void setTestsPassed(Integer testsPassed) {
		this.testsPassed = testsPassed;
	}
	
	public Integer getTestsPassed() {
		return testsPassed;
	}
	
	public String getTimeSpent() {
		return timeSpent;
	}

	public void setTimeSpent(String timeSpent) {
		this.timeSpent = timeSpent;
	}
	
	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	
	public boolean isAnalyzed() {
		return analyzed;
	}

	public void setAnalyzed(boolean analyzed) {
		this.analyzed = analyzed;
	}

	public String getSonarKey() {
		return sonarKey;
	}

	public void setSonarKey(String sonarKey) {
		this.sonarKey = sonarKey;
	}
	
	public Set<UserTaskSolution> getUserTaskSolution() {
		return userTaskSolution;
	}

	public void setUserTaskSolution(Set<UserTaskSolution> userTaskSolution) {
		this.userTaskSolution = userTaskSolution;
	}
	
	public String getResultReport() {
		return resultReport;
	}

	public void setResultReport(String resultReport) {
		this.resultReport = resultReport;
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

	@Override
	public String toString() {
		return "UserTask [id=" + id + ", user=" + user + ", task=" + task + ", assignDate=" + assignDate + ", startDate=" + startDate 
				+ ", submitDate=" + submitDate + ", comment=" + comment + "]";
	}
	
}
