package com.reflex.request;

import jakarta.validation.constraints.*;
 
public class SignupRequest {
    
	
	//@NotBlank
    //@Size(min = 3, max = 20)
    //private String username;
 
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;
    
    //@NotBlank
    //@Size(min = 6, max = 40)
    //private String password;
    
	@NotBlank
	@Size(max=60)
	private String fullName;
	
	@NotBlank
	@Size(max=20)
	private String phone;
	
	private String info;
  
	/*
    public String getUsername() {
        return username;
    }
 
    public void setUsername(String username) {
        this.username = username;
    }*/
 
    public String getEmail() {
        return email;
    }
 
    public void setEmail(String email) {
        this.email = email;
    }
    /*
    public String getPassword() {
        return password;
    }
 
    public void setPassword(String password) {
        this.password = password;
    }*/

	public String getPhone() {
		return phone;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
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
    
}