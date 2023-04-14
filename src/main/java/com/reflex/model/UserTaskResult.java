package com.reflex.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
@Table(name="user_task_result")
public class UserTaskResult {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable=true, columnDefinition="TEXT")
	private String stdout;
	
	@Column(nullable=true, columnDefinition="TEXT")
	private String stderr;
	
	@Column(nullable=true, columnDefinition="TEXT")
	private String compile_output;
	
	@Column(nullable=true, columnDefinition="TEXT")
	private String message;
	
	@Column(nullable=true)
	private int exit_code;
	
	@Column(nullable=true)
	private int exit_signal;
	
	@Column(nullable=true, columnDefinition="TEXT")
	private String status;
	
	@Column(nullable=true, columnDefinition="timestamptz")
	private Date created_at;
	
	@Column(nullable=true, columnDefinition="timestamptz")
	private Date finished_at;
	
	@Column(nullable=true)
	private float time;
	
	@Column(nullable=true)
	private float wall_time;
	
	@Column(nullable=true)
	private float memory;
	
	@Column(name="submission_token", nullable=false)
	private String submissionToken;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="input_id")
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	//@JsonIgnore
	private TaskTestInput taskTestInput;
	
	public UserTaskResult() {
		
	}
	public UserTaskResult(TaskTestInput taskTestInput,String submissionToken) {
		this.taskTestInput = taskTestInput;
		this.submissionToken = submissionToken;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSubmissionToken() {
		return submissionToken;
	}
	public void setSubmissionToken(String submissionToken) {
		this.submissionToken = submissionToken;
	}
	public TaskTestInput getTaskTestInput() {
		return taskTestInput;
	}
	public void setTaskTestInput(TaskTestInput taskTestInput) {
		this.taskTestInput = taskTestInput;
	}
	public String getStdout() {
		return stdout;
	}
	public void setStdout(String stdout) {
		this.stdout = stdout;
	}
	public String getStderr() {
		return stderr;
	}
	public void setStderr(String stderr) {
		this.stderr = stderr;
	}
	public String getCompile_output() {
		return compile_output;
	}
	public void setCompile_output(String compile_output) {
		this.compile_output = compile_output;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getExit_code() {
		return exit_code;
	}
	public void setExit_code(int exit_code) {
		this.exit_code = exit_code;
	}
	public int getExit_signal() {
		return exit_signal;
	}
	public void setExit_signal(int exit_signal) {
		this.exit_signal = exit_signal;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	public Date getFinished_at() {
		return finished_at;
	}
	public void setFinished_at(Date finished_at) {
		this.finished_at = finished_at;
	}
	public float getTime() {
		return time;
	}
	public void setTime(float time) {
		this.time = time;
	}
	public float getWall_time() {
		return wall_time;
	}
	public void setWall_time(float wall_time) {
		this.wall_time = wall_time;
	}
	public float getMemory() {
		return memory;
	}
	public void setMemory(float memory) {
		this.memory = memory;
	}
	
}
