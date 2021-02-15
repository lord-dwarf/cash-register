package com.polinakulyk.cashregister.db.entity;

import com.polinakulyk.cashregister.db.dto.ProductAmountUnit;
import java.util.StringJoiner;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.GenericGenerator;

@Entity
public class ReceiptItem {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @ManyToOne
    private Receipt receipt;

    @ManyToOne
    private Product product;

    private String name;
    private int amount;
    private ProductAmountUnit amountUnit;
    private int price;

    public String getId() {
        return id;
    }

    public ReceiptItem setId(String id) {
        this.id = id;
        return this;
    }

    public Receipt getReceipt() {
        return receipt;
    }

    public ReceiptItem setReceipt(Receipt receipt) {
        this.receipt = receipt;
        return this;
    }

    public Product getProduct() {
        return product;
    }

    public ReceiptItem setProduct(Product product) {
        this.product = product;
        return this;
    }

    public String getName() {
        return name;
    }

    public ReceiptItem setName(String productName) {
        this.name = productName;
        return this;
    }

    public int getAmount() {
        return amount;
    }

    public ReceiptItem setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public ProductAmountUnit getAmountUnit() {
        return amountUnit;
    }

    public ReceiptItem setAmountUnit(ProductAmountUnit amountUnit) {
        this.amountUnit = amountUnit;
        return this;
    }

    public int getPrice() {
        return price;
    }

    public ReceiptItem setPrice(int price) {
        this.price = price;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReceiptItem that = (ReceiptItem) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ReceiptItem.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("receipt.id='" + (receipt != null ? receipt.getId() : null) + "'")
                .add("product.id='" + (product != null ? product.getId() : null) + "'")
                .add("name='" + name + "'")
                .add("amount=" + amount)
                .add("amountUnit='" + amountUnit + "'")
                .add("price=" + price)
                .toString();
    }
}
