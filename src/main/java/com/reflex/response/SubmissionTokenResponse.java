package com.reflex.response;

public class SubmissionTokenResponse {
	
	private String token;

	public SubmissionTokenResponse() {
		
	}
	
	public SubmissionTokenResponse(String token) {
		this.token = token;
	}
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
}
