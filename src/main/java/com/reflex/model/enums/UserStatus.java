package com.reflex.model.enums;

import java.util.Arrays;
import java.util.Optional;

public enum UserStatus {
	
	invited,
	started,
	submitted, 
	approved,
	rejected, 
	none;
	
	/*
	INV("invited"),
	ST("started"),
	SUB("submitted"), 
	APP("approved"),
	RJ("rejected"), 
	NE("none");
	
    private final String status;

	UserStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    } */
    
    public static Optional<UserStatus> byNameIgnoreCase(String givenFullName) {
        return Arrays.stream(values()).filter(it -> it.name().equalsIgnoreCase(givenFullName)).findAny();
    }
}
