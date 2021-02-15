package com.polinakulyk.cashregister.db.entity;

import com.polinakulyk.cashregister.db.dto.ProductAmountUnit;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.GenericGenerator;

@Entity
public class Product {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @Column(unique = true)
    @NotBlank(message = "Code cannot be blank")
    private String code;

    @NotBlank(message = "Category cannot be blank")
    private String category;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "Name cannot be blank")
    private String details;

    @NotNull(message = "Price cannot be null")
    @Min(value = 1, message = "Price must be greater than 0")
    @Max(value = 9999999, message = "Price must be less than 100k")
    private Integer price;

    @NotNull(message = "Amount unit cannot be null")
    private ProductAmountUnit amountUnit;

    @NotNull(message = "Amount available cannot be null")
    @Min(value = 0, message = "Amount available must be non-negative")
    @Max(value = 9999999, message = "Amount available must be less than 10k")
    private Integer amountAvailable;

    @OneToMany(mappedBy = "product")
    @NotNull(message = "Receipt items cannot be null")
    private Set<ReceiptItem> receiptItems = new HashSet<>();

    public String getId() {
        return id;
    }

    public Product setId(String id) {
        this.id = id;
        return this;
    }

    public String getCode() {
        return code;
    }

    public Product setCode(String code) {
        this.code = code;
        return this;
    }

    public String getCategory() {
        return category;
    }

    public Product setCategory(String category) {
        this.category = category;
        return this;
    }

    public String getName() {
        return name;
    }

    public Product setName(String name) {
        this.name = name;
        return this;
    }

    public String getDetails() {
        return details;
    }

    public Product setDetails(String details) {
        this.details = details;
        return this;
    }

    public Integer getPrice() {
        return price;
    }

    public Product setPrice(Integer price) {
        this.price = price;
        return this;
    }

    public ProductAmountUnit getAmountUnit() {
        return amountUnit;
    }

    public Product setAmountUnit(ProductAmountUnit unit) {
        this.amountUnit = unit;
        return this;
    }

    public Integer getAmountAvailable() {
        return amountAvailable;
    }

    public Product setAmountAvailable(Integer amountAvailable) {
        this.amountAvailable = amountAvailable;
        return this;
    }

    public Set<ReceiptItem> getReceiptItems() {
        return receiptItems;
    }

    public Product setReceiptItems(Set<ReceiptItem> items) {
        this.receiptItems = items;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        return id.equals(product.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return new StringJoiner(
                ", ", Product.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("code='" + code + "'")
                .add("category='" + category + "'")
                .add("name='" + name + "'")
                .add("details='" + details + "'")
                .add("price=" + price)
                .add("amountUnit=" + amountUnit)
                .add("amountAvailable=" + amountAvailable)
                .add("receiptItems=" + receiptItems)
                .toString();
    }
}
