package com.reflex.response;

import java.util.Date;

public class SubmissionResultResponse {
	
	private String stdout;
	private String stderr;
	private String compile_output;
	private String message;
	private int exit_code;
	private int exit_signal;
	private SubmissionStatusResponse status;
	private Date created_at;
	private Date finished_at;
	private String token;
	private float time;
	private float wall_time;
	private float memory;
	
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
	public SubmissionStatusResponse getStatus() {
		return status;
	}
	public void setStatus(SubmissionStatusResponse status) {
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
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
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
