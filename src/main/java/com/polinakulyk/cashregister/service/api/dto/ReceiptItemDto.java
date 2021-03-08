package com.polinakulyk.cashregister.service.api.dto;

import com.polinakulyk.cashregister.db.dto.ProductAmountUnit;

import java.math.BigDecimal;

public class ReceiptItemDto {
    private String id;
    private ProductDto product;
    private String name;
    private BigDecimal amount;
    private ProductAmountUnit amountUnit;
    private BigDecimal price;

    public String getId() {
        return id;
    }

    public ReceiptItemDto setId(String id) {
        this.id = id;
        return this;
    }

    public ProductDto getProduct() {
        return product;
    }

    public ReceiptItemDto setProduct(ProductDto product) {
        this.product = product;
        return this;
    }

    public String getName() {
        return name;
    }

    public ReceiptItemDto setName(String name) {
        this.name = name;
        return this;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public ReceiptItemDto setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public ProductAmountUnit getAmountUnit() {
        return amountUnit;
    }

    public ReceiptItemDto setAmountUnit(ProductAmountUnit amountUnit) {
        this.amountUnit = amountUnit;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public ReceiptItemDto setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }
}
