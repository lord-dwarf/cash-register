package com.polinakulyk.cashregister.db.dto;

public enum ProductAmountUnit {

    UNIT(Value.UNIT),
    GRAM(Value.GRAM);

    private final String value;

    ProductAmountUnit(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static class Value {

        public static final String UNIT = "UNIT";
        public static final String GRAM = "GRAM";

        private Value() {
            throw new UnsupportedOperationException();
        }
    }
}
