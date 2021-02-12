package com.polinakulyk.cashregister.service;

import com.polinakulyk.cashregister.db.dto.ProductAmountUnit;
import com.polinakulyk.cashregister.exception.CashRegisterException;

import static com.polinakulyk.cashregister.util.CashRegisterUtil.quote;

public class ServiceHelper {

    private ServiceHelper() {
        throw new UnsupportedOperationException("Cannot instantiate");
    }

    public static int calcCost(int price, int amount, ProductAmountUnit amountUnit) {
        switch (amountUnit) {
            case GRAM: return amount * price / 1000;
            case UNIT: return amount * price;
            default: throw new CashRegisterException(quote(
                    "Amount unit not supported", amountUnit));
        }
    }
}
