package com.polinakulyk.cashregister.db.entity;

import com.polinakulyk.cashregister.db.dto.ReceiptStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.annotations.GenericGenerator;

@Entity
public class Receipt {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    private LocalDateTime createdTime;
    private LocalDateTime checkoutTime;
    private ReceiptStatus status;
    private int sumTotal;

    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL)
    private List<ReceiptItem> receiptItems = new ArrayList<>();

    @ManyToOne
    private User user;

    public String getId() {
        return id;
    }

    public Receipt setId(String id) {
        this.id = id;
        return this;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public Receipt setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
        return this;
    }

    public LocalDateTime getCheckoutTime() {
        return checkoutTime;
    }

    public Receipt setCheckoutTime(LocalDateTime checkoutTime) {
        this.checkoutTime = checkoutTime;
        return this;
    }

    public ReceiptStatus getStatus() {
        return status;
    }

    public Receipt setStatus(ReceiptStatus status) {
        this.status = status;
        return this;
    }

    public int getSumTotal() {
        return sumTotal;
    }

    public Receipt setSumTotal(int total) {
        this.sumTotal = total;
        return this;
    }

    public List<ReceiptItem> getReceiptItems() {
        return receiptItems;
    }

    public Receipt setReceiptItems(List<ReceiptItem> items) {
        this.receiptItems = items;
        return this;
    }

    public User getUser() {
        return user;
    }

    public Receipt setUser(User user) {
        this.user = user;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Receipt receipt = (Receipt) o;

        return id.equals(receipt.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return new StringJoiner(
                ", ", Receipt.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("createdTime=" + createdTime)
                .add("checkoutTime=" + checkoutTime)
                .add("status='" + status + "'")
                .add("sumTotal=" + sumTotal)
                .add("receiptItems=" + receiptItems)
                .add("user.id=" + (user != null ? user.getId() : null))
                .toString();
    }
}
