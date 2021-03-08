package com.polinakulyk.cashregister.service.api.dto;

import com.polinakulyk.cashregister.db.dto.ReceiptStatus;
import com.polinakulyk.cashregister.db.entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class ReceiptDto {
    private String id;
    private LocalDateTime createdTime;
    private LocalDateTime checkoutTime;
    private ReceiptStatus status;
    private BigDecimal sumTotal;
    private Set<ReceiptItemDto> receiptItems = new HashSet<>();
    private User user;

    public String getId() {
        return id;
    }

    public ReceiptDto setId(String id) {
        this.id = id;
        return this;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public ReceiptDto setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
        return this;
    }

    public LocalDateTime getCheckoutTime() {
        return checkoutTime;
    }

    public ReceiptDto setCheckoutTime(LocalDateTime checkoutTime) {
        this.checkoutTime = checkoutTime;
        return this;
    }

    public ReceiptStatus getStatus() {
        return status;
    }

    public ReceiptDto setStatus(ReceiptStatus status) {
        this.status = status;
        return this;
    }

    public BigDecimal getSumTotal() {
        return sumTotal;
    }

    public ReceiptDto setSumTotal(BigDecimal sumTotal) {
        this.sumTotal = sumTotal;
        return this;
    }

    public Set<ReceiptItemDto> getReceiptItems() {
        return receiptItems;
    }

    public ReceiptDto setReceiptItems(Set<ReceiptItemDto> receiptItems) {
        this.receiptItems = receiptItems;
        return this;
    }

    public User getUser() {
        return user;
    }

    public ReceiptDto setUser(User user) {
        this.user = user;
        return this;
    }
}
