package com.reflex.model.enums;

import java.util.Arrays;
import java.util.Optional;

public enum TaskDifficulty {
	
	JC("junior_beginner"),
    JB("junior_intermediate"),
    JA("junior_advanced"),
    MC("middle_beginner"),
    MB("middle_intermediate"),
    MA("middle_advanced"),
    SC("senior_beginner"),
    SB("senior_intermediate"),
    SA("senior_advanced");
                                 
    private String fullName;
                                 
    TaskDifficulty(String fullName) {
        this.fullName = fullName;
    }
                                 
    public String getFullName() {
        return fullName;
    }
    
    public static Optional<TaskDifficulty> byNameIgnoreCase(String givenName) {
        return Arrays.stream(values()).filter(it -> it.name().equalsIgnoreCase(givenName)).findAny();
    }

    public static Optional<TaskDifficulty> byFullNameIgnoreCase(String givenFullName) {
        return Arrays.stream(values()).filter(it -> it.fullName.equalsIgnoreCase(givenFullName)).findAny();
    }
}
