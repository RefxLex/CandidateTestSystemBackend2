package com.reflex.model.enums;

import java.util.Arrays;
import java.util.Optional;

public enum TaskDifficulty {
	
	junior_beginner,
    junior_intermediate,
    junior_advanced,
    middle_beginner,
    middle_intermediate,
    middle_advanced,
    senior_beginner,
    senior_intermediate,
    senior_advanced;
	
    public static Optional<TaskDifficulty> byNameIgnoreCase(String givenName) {
        return Arrays.stream(values()).filter(it -> it.name().equalsIgnoreCase(givenName)).findAny();
    }

}
