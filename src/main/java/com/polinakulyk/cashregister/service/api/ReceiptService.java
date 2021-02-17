package com.polinakulyk.cashregister.service.api;

import com.polinakulyk.cashregister.db.entity.Receipt;
import java.math.BigDecimal;
import java.util.List;

public interface ReceiptService {
    Iterable<Receipt> findAll();

    List<Receipt> findAllByTellerId(String tellerId);

    Receipt findExistingById(String id);

    Receipt createReceipt(String userId);

    Receipt completeReceipt(String receiptId);

    Receipt cancelReceipt(String receiptId);

    Receipt addReceiptItem(
            String receiptId, String receiptItemProductId, BigDecimal receiptItemAmount);

    Receipt cancelReceiptItem(String receiptId, String receiptItemId);

    Receipt updateReceiptItemAmount(
            String receiptId, String receiptItemId, BigDecimal newAmount);
}
