package com.ronial.internet_banking.common.exceptions;

public enum AuthExceptionMessage {
    REQUIRE_AUTHENTICATION("Require authentication", 403),
    FORBIDDEN("Forbidden. You don't permission", 403),
    AUTHENTICATION_FAILED("Authentication failed", 401),
    SESSION_EXISTS("Session already exists", 401),
    TOKEN_NOT_EXISTS("Token does not exist", 401),
    TOKEN_INCORRECT("Token is incorrect", 401),
    SESSION_EXPIRED("Session expired", 401),;
    private final String message;
    private final int code;

    AuthExceptionMessage(String message, int code) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}
