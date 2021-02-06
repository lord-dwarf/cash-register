package com.polinakulyk.cashregister.controller.dto;

import java.util.StringJoiner;

public class ProductSoldResponseDto {
    private String productId;
    private String productCode;
    private String productCategory;
    private String productName;
    private int productPrice;
    private String productAmountUnit;
    private int productAmountAvailable;
    private int productSoldSumTotal;

    public String getProductId() {
        return productId;
    }

    public ProductSoldResponseDto setProductId(String productId) {
        this.productId = productId;
        return this;
    }

    public String getProductCode() {
        return productCode;
    }

    public ProductSoldResponseDto setProductCode(String productCode) {
        this.productCode = productCode;
        return this;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public ProductSoldResponseDto setProductCategory(String productCategory) {
        this.productCategory = productCategory;
        return this;
    }

    public String getProductName() {
        return productName;
    }

    public ProductSoldResponseDto setProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public ProductSoldResponseDto setProductPrice(int productPrice) {
        this.productPrice = productPrice;
        return this;
    }

    public String getProductAmountUnit() {
        return productAmountUnit;
    }

    public ProductSoldResponseDto setProductAmountUnit(String productAmountUnit) {
        this.productAmountUnit = productAmountUnit;
        return this;
    }

    public int getProductAmountAvailable() {
        return productAmountAvailable;
    }

    public ProductSoldResponseDto setProductAmountAvailable(int productAmountAvailable) {
        this.productAmountAvailable = productAmountAvailable;
        return this;
    }

    public int getProductSoldSumTotal() {
        return productSoldSumTotal;
    }

    public ProductSoldResponseDto setProductSoldSumTotal(int productSoldSumTotal) {
        this.productSoldSumTotal = productSoldSumTotal;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductSoldResponseDto that = (ProductSoldResponseDto) o;

        return productId.equals(that.productId);
    }

    @Override
    public int hashCode() {
        return productId.hashCode();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ProductSoldResponseDto.class.getSimpleName() + "[", "]")
                .add("productId='" + productId + "'")
                .add("productCode='" + productCode + "'")
                .add("productCategory='" + productCategory + "'")
                .add("productName='" + productName + "'")
                .add("productPrice=" + productPrice)
                .add("productAmountUnit='" + productAmountUnit + "'")
                .add("productAmountAvailable=" + productAmountAvailable)
                .add("productSoldSumTotal=" + productSoldSumTotal)
                .toString();
    }
}
