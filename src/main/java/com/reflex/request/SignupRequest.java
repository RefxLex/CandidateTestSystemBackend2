package com.reflex.request;

import jakarta.validation.constraints.*;
 
public class SignupRequest {
    
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;
     
	@NotBlank
	@Size(max=60)
	private String fullName;
	
	@NotBlank
	@Size(max=20)
	private String phone;
	
	private String info;
	
    public String getEmail() {
        return email;
    }
 
    public void setEmail(String email) {
        this.email = email;
    }

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