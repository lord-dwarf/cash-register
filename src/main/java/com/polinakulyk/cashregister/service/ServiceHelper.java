package com.polinakulyk.cashregister.service;

import com.polinakulyk.cashregister.exception.CashRegisterException;

import static com.polinakulyk.cashregister.db.entity.ProductAmountUnit.Value.GRAM;
import static com.polinakulyk.cashregister.db.entity.ProductAmountUnit.Value.UNIT;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.quote;

public class ServiceHelper {

    private ServiceHelper() {
        throw new UnsupportedOperationException("Cannot instantiate");
    }

    public static int calcCost(int price, int amount, String amountUnit) {
        switch (amountUnit) {
            case GRAM: return amount * price / 1000;
            case UNIT: return amount * price;
            default: throw new CashRegisterException(quote(
                    "Amount unit not supported", amountUnit));
        }
    }
}
