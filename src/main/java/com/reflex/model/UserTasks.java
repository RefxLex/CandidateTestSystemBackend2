package com.reflex.model;

import java.time.Instant;
import java.util.Date;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.reflex.model.enums.TaskStatus;

import jakarta.persistence.*;

@Entity
@Table(name="user_tasks")
public class UserTasks {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	/*
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@JoinColumn(name="user_id", nullable=false)
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@JsonIgnore
	private User user; */
	
	@ManyToOne(fetch=FetchType.EAGER, optional=false)
	@JoinColumn(name="task_id", nullable=false)
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	private Task task;
	
	/*
	@Column(name="task_status", nullable=false)
	@Enumerated(EnumType.STRING)
	private TaskStatus taskStatus; */
	
	@Column(name="assing_date", nullable=false)
	private Instant assignDate;
	
	@Column(nullable=true, columnDefinition="TEXT")
	private String result;
	
	@Column(nullable=true, columnDefinition="TEXT")
	private String code;
	
	@Column(name="start_date", nullable=true)
	private Instant startDate;
	
	@Column(name="submit_date", nullable=true)
	private Instant submitDate;
	
	@Column(nullable=true, columnDefinition="TEXT")
	private String comment;
	
	public UserTasks() {
		
	}

	public UserTasks(Task task, Instant assignDate) {

		this.task = task;
		this.assignDate = assignDate;

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

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
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
	
}
