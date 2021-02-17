package com.polinakulyk.cashregister.controller.dto;

import java.math.BigDecimal;
import java.util.StringJoiner;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class AddReceiptItemRequestDto {

    @NotBlank(message = "Product id cannot be blank")
    private String productId;

    @NotNull(message = "Receipt item amount cannot be null")
    private BigDecimal receiptItemAmount;

    public String getProductId() {
        return productId;
    }

    public AddReceiptItemRequestDto setProductId(String productId) {
        this.productId = productId;
        return this;
    }

    public BigDecimal getReceiptItemAmountAmount() {
        return receiptItemAmount;
    }

    public AddReceiptItemRequestDto setReceiptItemAmount(BigDecimal receiptItemAmount) {
        this.receiptItemAmount = receiptItemAmount;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AddReceiptItemRequestDto that = (AddReceiptItemRequestDto) o;

        if (!productId.equals(that.productId)) return false;
        return receiptItemAmount.equals(that.receiptItemAmount);
    }

    @Override
    public int hashCode() {
        int result = productId.hashCode();
        result = 31 * result + receiptItemAmount.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner(
                ", ", AddReceiptItemRequestDto.class.getSimpleName() + "[", "]")
                .add("productId='" + productId + "'")
                .add("receiptItemAmount=" + receiptItemAmount)
                .toString();
    }
}
