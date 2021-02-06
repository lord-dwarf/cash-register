package com.polinakulyk.cashregister.controller.dto;

import java.util.StringJoiner;

public class UpdateReceiptItemDto {

    private Integer amount;

    public Integer getAmount() {
        return amount;
    }

    public UpdateReceiptItemDto setAmount(Integer amount) {
        this.amount = amount;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UpdateReceiptItemDto that = (UpdateReceiptItemDto) o;

        return amount != null ? amount.equals(that.amount) : that.amount == null;
    }

    @Override
    public int hashCode() {
        return amount != null ? amount.hashCode() : 0;
    }

    @Override
    public String toString() {
        return new StringJoiner(
                ", ", UpdateReceiptItemDto.class.getSimpleName() + "[", "]")
                .add("amount=" + amount)
                .toString();
    }
}
