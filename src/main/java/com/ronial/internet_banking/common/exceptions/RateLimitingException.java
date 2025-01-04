package com.ronial.internet_banking.common.exceptions;

public class RateLimitingException extends ApplicationException{
    public RateLimitingException(String message) {
        super(message, 429);
    }
}
