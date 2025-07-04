package com.example.constant;

public enum Status {
    
	ACTIVE("A"),
    INACTIVE("IA"),
    DEACTIVATED("D"),
    PENDING("P"),
    PERMANENTLY_DELETED("X"),
    INITIATED("I"),
    CONFIRM("C");

    private final String code;

    Status(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}

