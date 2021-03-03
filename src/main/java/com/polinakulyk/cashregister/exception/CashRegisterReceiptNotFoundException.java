package com.polinakulyk.cashregister.exception;

import static com.polinakulyk.cashregister.util.CashRegisterUtil.quote;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * The exception to be thrown when receipt not found in DB.
 */
public class CashRegisterReceiptNotFoundException extends CashRegisterException {

    public CashRegisterReceiptNotFoundException(String receiptId) {

        // by default, the associated HTTP status points to HTTP 404 Not Found
        // as a general interpretation of exception for client
        super(NOT_FOUND, quote("Receipt not found", receiptId));
    }
}
