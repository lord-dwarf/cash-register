package com.polinakulyk.cashregister.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * Base class for CashRegister exceptions.
 *
 * Extends Spring's {@link ResponseStatusException} for the sake of associating
 * the {@link HttpStatus} with exception.
 */
public class CashRegisterException extends ResponseStatusException {

    public CashRegisterException(String message) {

        // by default, the associated HTTP status points to internal server error
        // as a general interpretation of exception for client
        super(INTERNAL_SERVER_ERROR, message);
    }

    public CashRegisterException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }

    public CashRegisterException(HttpStatus httpStatus, Throwable cause) {
        super(httpStatus, cause.getMessage(), cause);
    }

    /*
     * The method is overridden to get rid of HTTP status code appended to reason
     * by a base exception class, and thus to get only the text message.
     */
    @Override
    public String getMessage() {
        return getReason();
    }
}
