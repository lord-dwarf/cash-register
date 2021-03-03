package com.polinakulyk.cashregister.exception;

import static com.polinakulyk.cashregister.util.CashRegisterUtil.quote;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * The exception to be thrown when product not found in DB.
 */
public class CashRegisterProductNotFoundException extends CashRegisterException {

    public CashRegisterProductNotFoundException(String productId) {

        // by default, the associated HTTP status points to HTTP 404 Not Found
        // as a general interpretation of exception for client
        super(NOT_FOUND, quote("Product not found", productId));
    }
}
