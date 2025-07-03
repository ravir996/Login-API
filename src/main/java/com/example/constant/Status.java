package com.example.constant;

public enum Status {
    
    ACTIVE("A"),
    INACTIVE("I"),
    DEACTIVATED("D"),
    PENDING("P"),
    PERMANENTLY_DELETED("X");

    private final String code;

    Status(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}

