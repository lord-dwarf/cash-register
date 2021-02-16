package com.polinakulyk.cashregister.service.api;

import com.polinakulyk.cashregister.controller.dto.UpdateReceiptItemDto;
import com.polinakulyk.cashregister.db.entity.Product;
import com.polinakulyk.cashregister.db.entity.Receipt;
import com.polinakulyk.cashregister.db.entity.ReceiptItem;
import com.polinakulyk.cashregister.db.entity.User;
import java.util.List;
import java.util.Optional;

public interface ReceiptService {
    Iterable<Receipt> findAll();
    List<Receipt> findAllByTellerId(String tellerId);
    Receipt findExistingById(String id);
    Receipt createReceipt(String userId);
    Receipt completeReceipt(String receiptId);
    Receipt cancelReceipt(String receiptId);
    Receipt addReceiptItem(String receiptId, String receiptItemProductId, Integer receiptItemAmount);
    Receipt cancelReceiptItem(String receiptId, String receiptItemId);
    Receipt updateReceiptItemAmount(
            String receiptId, String receiptItemId, Integer newAmount);
}
