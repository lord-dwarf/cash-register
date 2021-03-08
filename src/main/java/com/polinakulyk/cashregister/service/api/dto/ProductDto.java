package com.polinakulyk.cashregister.service.api.dto;

import com.polinakulyk.cashregister.db.dto.ProductAmountUnit;

import java.math.BigDecimal;

public class ProductDto {
    private String id;
    private String code;
    private String category;
    private String name;
    private String details;
    private BigDecimal price;
    private ProductAmountUnit amountUnit;
    private BigDecimal amountAvailable;

    public String getId() {
        return id;
    }

    public ProductDto setId(String id) {
        this.id = id;
        return this;
    }

    public String getCode() {
        return code;
    }

    public ProductDto setCode(String code) {
        this.code = code;
        return this;
    }

    public String getCategory() {
        return category;
    }

    public ProductDto setCategory(String category) {
        this.category = category;
        return this;
    }

    public String getName() {
        return name;
    }

    public ProductDto setName(String name) {
        this.name = name;
        return this;
    }

    public String getDetails() {
        return details;
    }

    public ProductDto setDetails(String details) {
        this.details = details;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public ProductDto setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public ProductAmountUnit getAmountUnit() {
        return amountUnit;
    }

    public ProductDto setAmountUnit(ProductAmountUnit amountUnit) {
        this.amountUnit = amountUnit;
        return this;
    }

    public BigDecimal getAmountAvailable() {
        return amountAvailable;
    }

    public ProductDto setAmountAvailable(BigDecimal amountAvailable) {
        this.amountAvailable = amountAvailable;
        return this;
    }
}
