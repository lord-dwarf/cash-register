package com.polinakulyk.cashregister.exception;

import static com.polinakulyk.cashregister.util.CashRegisterUtil.quote;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * The exception to be thrown when receipt item not found in DB.
 */
public class CashRegisterReceiptItemNotFoundException extends CashRegisterException {

    public CashRegisterReceiptItemNotFoundException(String receiptItemId) {

        // by default, the associated HTTP status points to HTTP 404 Not Found
        // as a general interpretation of exception for client
        super(NOT_FOUND, quote("Receipt item not found", receiptItemId));
    }
}
