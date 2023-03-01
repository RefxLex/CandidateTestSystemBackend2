package com.reflex.model;

import com.reflex.model.enums.UserRole;
import com.reflex.model.enums.UserStatus;

import jakarta.persistence.*;

@Entity
@Table(name="user_profile")
public class User {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable=false, unique=true)
	private String email;
	
	@Column(name="user_name", nullable=false, unique=true)
	private String userName;
	
	@Column(nullable=false, unique=true)
	private String password;
	
	@Column(name="first_name",nullable=false)
	private String firstName;
	
	@Column(name="second_name", nullable=false)
	private String secondName;
	
	@Column(name="last_name", nullable=false)
	private String lastName;
	
	@Column(nullable=false, unique=true)
	private String phone;
	
	@Column(nullable=true)
	private String info;
	
	@Column(name="user_role", nullable=false)
	@Enumerated(EnumType.STRING)
	private UserRole userRole;
	
	@Column(name="user_status", nullable=false)
	@Enumerated(EnumType.STRING)
	private UserStatus userStatus;

	public User() {
		
	}

	public User(String email, String userName, String password, String firstName, String secondName,
			String lastName, String phone, String info, UserRole userRole, UserStatus userStatus) {
		
		this.email = email;
		this.userName = userName;
		this.password = password;
		this.firstName = firstName;
		this.secondName = secondName;
		this.lastName = lastName;
		this.phone = phone;
		this.info = info;
		this.userRole = userRole;
		this.userStatus = userStatus;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
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

	public UserRole getUserRole() {
		return userRole;
	}

	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}

	public UserStatus getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(UserStatus userStatus) {
		this.userStatus = userStatus;
	}
	
}
