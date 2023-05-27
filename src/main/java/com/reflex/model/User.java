package com.reflex.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.reflex.model.enums.UserStatus;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="user_profiles")
public class User {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
	@Size(max=50)
	@Email
	@Column(nullable=false, unique=true)
	private String email;
	
	@NotBlank
	@Size(max=20)
	@Column(name="user_name", nullable=false, unique=true)
	private String userName;
	
	@NotBlank
	@Size(max=120)
	@Column(nullable=false)
	private String password;
	
	@NotBlank
	@Size(max=60)
	@Column(name="full_name",nullable=false)
	private String fullName;
	
	@NotBlank
	@Size(max=20)
	@Column(nullable=false, unique=true)
	private String phone;
	
	@Column(nullable=true, columnDefinition="TEXT")
	private String info;
	
	@Column(name="user_status", nullable=false)
	private String userStatus;
	
	@Column(nullable=true, columnDefinition="timestamptz")
	private Date lastActivity;
	
	@Column(name="last_score", nullable=true)
	private String lastScore;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "user_roles", 
	             joinColumns = @JoinColumn(name = "user_id"),
	             inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();

	public User() {
		
	}

	public User(@NotBlank String userName, @Email @NotBlank String email, @NotBlank String password, @NotBlank String fullName,
			@NotBlank String phone, String info, String userStatus) {
		
		this.userName = userName;
		this.email = email;
		this.password = password;
		this.fullName = fullName;
		this.phone = phone;
		this.info = info;
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

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Date getLastActivity() {
		return lastActivity;
	}

	public void setLastActivity(Date lastActivity) {
		this.lastActivity = lastActivity;
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
	
	public Set<Role> getRoles() {
		    return roles;
	}

	public void setRoles(Set<Role> roles) {
		    this.roles = roles;
	}

	public String getLastScore() {
		return lastScore;
	}

	public void setLastScore(String lastScore) {
		this.lastScore = lastScore;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", email=" + email + ", userName=" + userName + ", password=" + password
				+ ", fullName=" + fullName + ", phone=" + phone + ", info=" + info + ", userStatus=" + userStatus
				+ ", lastActivity=" + lastActivity + ", lastScore=" + lastScore + ", roles=" + roles + "]";
	}

}
