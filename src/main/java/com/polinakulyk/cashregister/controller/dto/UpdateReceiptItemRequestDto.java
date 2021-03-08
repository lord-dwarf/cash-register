package com.polinakulyk.cashregister.controller.dto;

import java.math.BigDecimal;
import java.util.StringJoiner;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

public class UpdateReceiptItemRequestDto {

    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.001", message = "Amount must be greater than 0")
    @DecimalMax(value = "999.999", message = "Amount must be less than 1000")
    private BigDecimal amount;

    public BigDecimal getAmount() {
        return amount;
    }

    public UpdateReceiptItemRequestDto setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UpdateReceiptItemRequestDto that = (UpdateReceiptItemRequestDto) o;

        return amount != null ? amount.equals(that.amount) : that.amount == null;
    }

    @Override
    public int hashCode() {
        return amount != null ? amount.hashCode() : 0;
    }

    @Override
    public String toString() {
        return new StringJoiner(
                ", ", UpdateReceiptItemRequestDto.class.getSimpleName() + "[", "]")
                .add("amount=" + amount)
                .toString();
    }
}
