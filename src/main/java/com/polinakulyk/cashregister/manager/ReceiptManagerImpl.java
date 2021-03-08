package com.polinakulyk.cashregister.manager;

import com.polinakulyk.cashregister.manager.api.ReceiptManager;
import com.polinakulyk.cashregister.service.api.ReceiptService;
import com.polinakulyk.cashregister.service.api.dto.ReceiptDto;
import com.polinakulyk.cashregister.manager.mapper.ReceiptMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * The purpose of the class is to isolate {@link ReceiptService} entities from
 * controller layer:
 * - transaction boundary is left on the underlying service layer;
 * - non-transactional manager layer allows fine-grained control over what data is available
 *   to controller layer (to prevent unnecessary data to be loaded from a database).
 */
@Component
public class ReceiptManagerImpl implements ReceiptManager {

    private final ReceiptService receiptService;
    private final ReceiptMapper receiptMapper;

    public ReceiptManagerImpl(ReceiptService receiptService, ReceiptMapper receiptMapper) {
        this.receiptService = receiptService;
        this.receiptMapper = receiptMapper;
    }

    @Override
    public List<ReceiptDto> findAll() {
        return receiptService.findAll().stream()
                .map(r -> receiptMapper.receiptToReceiptDto(
                        // prevent loading of receipt items
                        r.setReceiptItems(null)))
                .collect(Collectors.toList());
    }

    @Override
    public List<ReceiptDto> findAllByTellerId(String tellerId) {
        return receiptService.findAllByTellerId(tellerId).stream()
                .map(r -> receiptMapper.receiptToReceiptDto(
                        // prevent loading of receipt items
                        r.setReceiptItems(null)))
                .collect(Collectors.toList());
    }

    @Override
    public ReceiptDto findExistingById(String id) {
        var receipt = receiptService.findExistingById(id);
        return receiptMapper.receiptToReceiptDto(receipt);
    }

    @Override
    public ReceiptDto createReceipt(String userId) {
        var receipt = receiptService.createReceipt(userId);
        return receiptMapper.receiptToReceiptDto(receipt);
    }

    @Override
    public ReceiptDto completeReceipt(String receiptId) {
        var receipt = receiptService.completeReceipt(receiptId);
        return receiptMapper.receiptToReceiptDto(receipt);
    }

    @Override
    public ReceiptDto cancelReceipt(String receiptId) {
        var receipt = receiptService.cancelReceipt(receiptId);
        return receiptMapper.receiptToReceiptDto(receipt);
    }

    @Override
    public ReceiptDto addReceiptItem(String receiptId, String receiptItemProductId, BigDecimal receiptItemAmount) {
        var receipt = receiptService.addReceiptItem(receiptId, receiptItemProductId, receiptItemAmount);
        return receiptMapper.receiptToReceiptDto(receipt);
    }

    @Override
    public ReceiptDto cancelReceiptItem(String receiptId, String receiptItemId) {
        var receipt = receiptService.cancelReceiptItem(receiptId, receiptItemId);
        return receiptMapper.receiptToReceiptDto(receipt);
    }

    @Override
    public ReceiptDto updateReceiptItemAmount(String receiptId, String receiptItemId, BigDecimal newAmount) {
        var receipt = receiptService.updateReceiptItemAmount(receiptId, receiptItemId, newAmount);
        return receiptMapper.receiptToReceiptDto(receipt);
    }
}
