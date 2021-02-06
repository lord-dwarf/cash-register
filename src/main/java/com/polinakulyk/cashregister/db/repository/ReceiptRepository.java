package com.polinakulyk.cashregister.db.repository;

import com.polinakulyk.cashregister.db.entity.Receipt;
import org.springframework.data.repository.CrudRepository;

public interface ReceiptRepository extends CrudRepository<Receipt, String> {
}
