package com.reflex.model.enums;

import java.util.Arrays;
import java.util.Optional;

public enum UserStatus {
	
	invited,  		// user registered in the system by moderator from job website
	started,		// a task was assigned to user by moderator
	submitted, 		// user submitted answers to ALL assigned tasks
	approved,		// moderator invited user to job interview	 (user successfully completed task)
	rejected, 		// moderator was unsatisfied with user score (user failed task)
	none;			// for admins, and moderators
	
    public static Optional<UserStatus> byNameIgnoreCase(String givenFullName) {
        return Arrays.stream(values()).filter(it -> it.name().equalsIgnoreCase(givenFullName)).findAny();
    }
}
