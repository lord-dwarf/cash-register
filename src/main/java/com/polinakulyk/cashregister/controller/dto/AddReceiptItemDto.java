package com.polinakulyk.cashregister.controller.dto;

import java.util.StringJoiner;

public class AddReceiptItemDto {

    private String productId;
    private Integer receiptItemAmount;

    public String getProductId() {
        return productId;
    }

    public AddReceiptItemDto setProductId(String productId) {
        this.productId = productId;
        return this;
    }

    public Integer getReceiptItemAmountAmount() {
        return receiptItemAmount;
    }

    public AddReceiptItemDto setReceiptItemAmount(Integer receiptItemAmount) {
        this.receiptItemAmount = receiptItemAmount;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AddReceiptItemDto that = (AddReceiptItemDto) o;

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
                ", ", AddReceiptItemDto.class.getSimpleName() + "[", "]")
                .add("productId='" + productId + "'")
                .add("receiptItemAmount=" + receiptItemAmount)
                .toString();
    }
}
