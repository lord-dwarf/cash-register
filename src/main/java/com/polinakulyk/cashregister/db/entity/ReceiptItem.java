package com.polinakulyk.cashregister.db.entity;

import com.polinakulyk.cashregister.db.dto.ProductAmountUnit;

import java.math.BigDecimal;
import java.util.StringJoiner;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PreRemove;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static com.polinakulyk.cashregister.util.CashRegisterUtil.AMOUNT_KILO_SCALE;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.MONEY_SCALE;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.PRECISION;

import static javax.persistence.FetchType.LAZY;

@Entity
public class ReceiptItem {
    @Id
    private String id;

    @ManyToOne(fetch = LAZY)
    @NotNull(message = "Receipt cannot be null")
    private Receipt receipt;

    @ManyToOne
    @NotNull(message = "Product cannot be null")
    private Product product;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @Column(precision = PRECISION, scale = AMOUNT_KILO_SCALE)
    @DecimalMin(value = "0.001", message = "Amount must be greater than 0")
    @DecimalMax(value = "999.999", message = "Amount must be less than 1000")
    private BigDecimal amount;

    @NotNull(message = "Amount unit cannot be null")
    private ProductAmountUnit amountUnit;

    @Column(precision = PRECISION, scale = MONEY_SCALE)
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @DecimalMax(value = "99999.99", message = "Price must be less than 100k")
    private BigDecimal price;

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

    public BigDecimal getAmount() {
        return amount;
    }

    public ReceiptItem setAmount(BigDecimal amount) {
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

    public BigDecimal getPrice() {
        return price;
    }

    public ReceiptItem setPrice(BigDecimal price) {
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

    /*
     * WORKAROUND for receipt item still present in receipt after deletion via Jpa/Crud repository.
     *
     * Prior to deleting receipt item, remove all object model bindings
     * between receipt item and its parent receipt.
     */
    @PreRemove
    private void removeReceiptBinding() {
        getReceipt().getReceiptItems().remove(this);
        setReceipt(null);
    }
}
