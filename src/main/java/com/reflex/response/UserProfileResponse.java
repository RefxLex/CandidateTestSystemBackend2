package com.reflex.response;

import java.util.Date;

public class UserProfileResponse {
	
	private Long id;
	private String email;
	private String userName;
	private String fullName;
	private String phone;
	private String info;
	private String userStatus;
	private Date lastActivity;
	private String lastScore;
	
	public UserProfileResponse() {
		
	}

	public UserProfileResponse(Long id, String email, String userName, String fullName, String phone, String info, String userStatus,
			Date lastActivity, String lastScore) {

		this.id = id;
		this.email = email;
		this.userName = userName;
		this.fullName = fullName;
		this.phone = phone;
		this.info = info;
		this.userStatus = userStatus;
		this.lastActivity = lastActivity;
		this.lastScore = lastScore;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

	public Date getLastActivity() {
		return lastActivity;
	}

	public void setLastActivity(Date lastActivity) {
		this.lastActivity = lastActivity;
	}

	public String getLastScore() {
		return lastScore;
	}

	public void setLastScore(String lastScore) {
		this.lastScore = lastScore;
	}
	
}
