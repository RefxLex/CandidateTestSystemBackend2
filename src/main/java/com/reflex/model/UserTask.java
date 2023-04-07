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
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@JsonIgnore
	private User user;
	
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@JoinColumn(name="task_id", nullable=false)
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private Task task;
	
	@Column(name="assign_date", nullable=false, columnDefinition="timestamptz")
	private Instant assignDate;
	
	@Column(nullable=true, columnDefinition="TEXT")
	private String code;
	
	@Column(name="start_date", nullable=true, columnDefinition="timestamptz")
	private Instant startDate;
	
	@Column(name="submit_date", nullable=true, columnDefinition="timestamptz")
	private Instant submitDate;
	
	@Column(name="time_spent", nullable=true)
	private String timeSpent;
	
	/*
	@Column(name="start_date", nullable=true, columnDefinition="text")
	private String startDate;
	
	@Column(name="submit_date", nullable=true, columnDefinition="text")
	private String submitDate; */
	
	@Column(nullable=true, columnDefinition="TEXT")
	private String comment;
	
	@Column(name="language_id", nullable=false)
	private int taskCodeLanguageId;
	
	@Column(name="language_name", nullable=false)
	private String languageName;
	
	@Column(name="tests_passed", nullable=false)
	private Integer testsPassed;
	
	@Column(name="tests_failed", nullable=false)
	private Integer testsFailed;
	
	@Column(name="overall_test_count", nullable=false)
	private int overallTestsCount;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name="user_task_id")
	private Set<UserTaskResult> userTaskResult = new HashSet<>();
	
	public UserTask() {
		
	}

	public UserTask(User user, Task task, Instant assignDate, int taskCodeLanguageId, String languageName, int overallTestsCount) {
		
		this.user = user;
		this.task = task;
		this.assignDate = assignDate;
		this.taskCodeLanguageId = taskCodeLanguageId;
		this.languageName = languageName;
		this.overallTestsCount = overallTestsCount;
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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

	public int getTaskCodeLanguageId() {
		return taskCodeLanguageId;
	}

	public void setTaskCodeLanguageId(int taskCodeLanguageId) {
		this.taskCodeLanguageId = taskCodeLanguageId;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Set<UserTaskResult> getUserTaskResult() {
		return userTaskResult;
	}

	public void setUserTaskResult(Set<UserTaskResult> userTaskResult) {
		this.userTaskResult = userTaskResult;
	}
	
	public int getOverallTestsCount() {
		return overallTestsCount;
	}

	public void setOverallTestsCount(int overallTestsCount) {
		this.overallTestsCount = overallTestsCount;
	}
	
	public String getLanguageName() {
		return languageName;
	}

	public void setLanguageName(String languageName) {
		this.languageName = languageName;
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
	
	/*
	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getSubmitDate() {
		return submitDate;
	}

	public void setSubmitDate(String submitDate) {
		this.submitDate = submitDate; */
	
	public String getTimeSpent() {
		return timeSpent;
	}

	public void setTimeSpent(String timeSpent) {
		this.timeSpent = timeSpent;
	}

	@Override
	public String toString() {
		return "UserTask [id=" + id + ", user=" + user + ", task=" + task + ", assignDate=" + assignDate + ", code="
				+ code + ", startDate=" + startDate + ", submitDate=" + submitDate + ", comment=" + comment
				+ ", taskCodeLanguageId=" + taskCodeLanguageId + ", userTaskResult=" + userTaskResult + "]";
	}
	
}
