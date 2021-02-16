package com.polinakulyk.cashregister.exception;

import static com.polinakulyk.cashregister.util.CashRegisterUtil.quote;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * The exception to be thrown when user not found in DB.
 */
public class CashRegisterUserNotFoundException extends CashRegisterException {

    public CashRegisterUserNotFoundException(String userIdentifier) {

        // by default, the associated HTTP status points to HTTP 401 Unauthorized
        // as a general interpretation of exception for client
        super(UNAUTHORIZED, quote("User not found", userIdentifier));
    }
}
