package com.polinakulyk.cashregister.manager.api;

import com.polinakulyk.cashregister.db.entity.Receipt;
import com.polinakulyk.cashregister.service.api.dto.ReceiptDto;

import java.math.BigDecimal;
import java.util.List;

public interface ReceiptManager {
    List<ReceiptDto> findAll();

    List<ReceiptDto> findAllByTellerId(String tellerId);

    ReceiptDto findExistingById(String id);

    ReceiptDto createReceipt(String userId);

    ReceiptDto completeReceipt(String receiptId);

    ReceiptDto cancelReceipt(String receiptId);

    ReceiptDto addReceiptItem(
            String receiptId, String receiptItemProductId, BigDecimal receiptItemAmount);

    ReceiptDto cancelReceiptItem(String receiptId, String receiptItemId);

    ReceiptDto updateReceiptItemAmount(
            String receiptId, String receiptItemId, BigDecimal newAmount);
}
