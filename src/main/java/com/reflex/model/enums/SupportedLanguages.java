package com.reflex.model.enums;

import java.util.Arrays;
import java.util.Optional;

public enum SupportedLanguages {
	
	Java("Oracle JDK 17.0.6", 1);
	
    private final String name;
    private final int code;

    SupportedLanguages(String name, int code) {
        this.name = name;
        this.code = code;
    }
    
    
    public static Optional<SupportedLanguages> findByCode(int code) {
        return Arrays.stream(values()).filter(month -> month.getCode() == code).findFirst();
    }
    
    public static Optional<SupportedLanguages> byNameIgnoreCase(String givenName) {
        return Arrays.stream(values()).filter(it -> it.name().equalsIgnoreCase(givenName)).findAny();
    }

	public String getName() {
		return name;
	}

	public int getCode() {
		return code;
	}
    
}
