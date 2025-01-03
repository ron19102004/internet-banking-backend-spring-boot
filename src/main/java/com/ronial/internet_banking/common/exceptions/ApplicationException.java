package com.ronial.internet_banking.common.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApplicationException extends RuntimeException {
    private final String message;
    private final int errorCode;
    public ApplicationException(final String message, final int errorCode) {
        super();
        this.message = message;
        this.errorCode = errorCode;
    }
}
