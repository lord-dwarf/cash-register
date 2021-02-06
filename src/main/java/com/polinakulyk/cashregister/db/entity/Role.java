package com.polinakulyk.cashregister.db.entity;

public enum Role {

    MERCH(Code.MERCH),
    TELLER(Code.TELLER),
    SR_TELLER(Code.SR_TELLER);

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static class Code {

        public static final String MERCH = "merch";
        public static final String TELLER = "teller";
        public static final String SR_TELLER = "sr_teller";

        private Code() {
            throw new UnsupportedOperationException();
        }
    }
}
