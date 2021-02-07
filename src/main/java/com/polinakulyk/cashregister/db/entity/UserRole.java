package com.polinakulyk.cashregister.db.entity;

public enum UserRole {

    MERCH(Value.MERCH),
    TELLER(Value.TELLER),
    SR_TELLER(Value.SR_TELLER);

    private final String value;

    UserRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static class Value {

        public static final String MERCH = "merch";
        public static final String TELLER = "teller";
        public static final String SR_TELLER = "sr_teller";

        private Value() {
            throw new UnsupportedOperationException();
        }
    }
}
