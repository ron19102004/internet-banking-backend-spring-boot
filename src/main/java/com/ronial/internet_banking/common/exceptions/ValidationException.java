package com.ronial.internet_banking.common.exceptions;


public class ValidationException extends ApplicationException{
    public ValidationException(String message, int errorCode) {
        super(message, errorCode);
    }
}
