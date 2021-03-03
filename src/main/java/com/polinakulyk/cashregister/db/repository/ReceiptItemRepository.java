package com.polinakulyk.cashregister.db.repository;

import com.polinakulyk.cashregister.db.entity.ReceiptItem;

import org.springframework.data.repository.CrudRepository;

public interface ReceiptItemRepository extends CrudRepository<ReceiptItem, String> {
}
