package com.polinakulyk.cashregister.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CashRegisterException extends ResponseStatusException {

    public CashRegisterException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public CashRegisterException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }

    public CashRegisterException(HttpStatus httpStatus, Throwable cause) {
        super(httpStatus, cause.getMessage(), cause);
    }
}
