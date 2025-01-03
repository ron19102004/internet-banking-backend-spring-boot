package com.ronial.internet_banking.common.exceptions;

public class AuthException extends ApplicationException{
    public AuthException(AuthExceptionMessage authExceptionMessage) {
        super(authExceptionMessage.getMessage(), authExceptionMessage.getCode());
    }
}
