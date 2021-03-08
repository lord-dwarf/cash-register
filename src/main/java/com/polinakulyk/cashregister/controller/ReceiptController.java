package com.polinakulyk.cashregister.controller;

import com.polinakulyk.cashregister.controller.dto.AddReceiptItemRequestDto;
import com.polinakulyk.cashregister.controller.dto.UpdateReceiptItemRequestDto;
import com.polinakulyk.cashregister.manager.api.ReceiptManager;
import com.polinakulyk.cashregister.security.api.AuthHelper;
import com.polinakulyk.cashregister.service.api.dto.ReceiptDto;

import java.util.List;
import java.util.Map;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.polinakulyk.cashregister.security.dto.UserRole.Value.SR_TELLER;
import static com.polinakulyk.cashregister.security.dto.UserRole.Value.TELLER;

@CrossOrigin
@Controller
@RequestMapping("/api/receipts")
public class ReceiptController {
    private final ReceiptManager receiptManager;
    private final AuthHelper authHelper;

    public ReceiptController(ReceiptManager receiptManager, AuthHelper authHelper) {
        this.receiptManager = receiptManager;
        this.authHelper = authHelper;
    }

    @GetMapping
    @RolesAllowed({SR_TELLER})
    public @ResponseBody
    List<ReceiptDto> listReceipts() {
        return receiptManager.findAll();
    }

    @GetMapping("/by-teller")
    @RolesAllowed({TELLER, SR_TELLER})
    public @ResponseBody
    List<ReceiptDto> listReceiptsByTeller() {
        return receiptManager.findAllByTellerId(authHelper.getUserId());
    }

    @PostMapping
    @RolesAllowed({TELLER, SR_TELLER})
    public @ResponseBody
    ReceiptDto createReceipt(@RequestBody Map<?, ?> emptyRequestBody) {
        return receiptManager.createReceipt(authHelper.getUserId());
    }

    @GetMapping("/{id}")
    @RolesAllowed({TELLER, SR_TELLER})
    public @ResponseBody
    ReceiptDto getReceipt(@PathVariable String id) {
        return receiptManager.findExistingById(id);
    }

    @PatchMapping("/{id}/complete")
    @RolesAllowed({TELLER, SR_TELLER})
    public @ResponseBody
    ReceiptDto completeReceipt(@PathVariable String id, @RequestBody Map<?, ?> emptyRequestBody) {
        return receiptManager.completeReceipt(id);
    }

    @PatchMapping("/{id}/cancel")
    @RolesAllowed({SR_TELLER})
    public @ResponseBody
    ReceiptDto cancelReceipt(@PathVariable String id, @RequestBody Map<?, ?> emptyRequestBody) {
        return receiptManager.cancelReceipt(id);
    }

    @PostMapping("/{receiptId}/items")
    @RolesAllowed({TELLER, SR_TELLER})
    public @ResponseBody
    ReceiptDto addReceiptItem(
            @PathVariable String receiptId,
            @Valid @RequestBody AddReceiptItemRequestDto addReceiptItemRequestDto
    ) {
        return receiptManager.addReceiptItem(
                receiptId,
                addReceiptItemRequestDto.getProductId(),
                addReceiptItemRequestDto.getReceiptItemAmountAmount());
    }

    @DeleteMapping("/{receiptId}/items/{receiptItemId}")
    @RolesAllowed({SR_TELLER})
    public @ResponseBody
    ReceiptDto cancelReceiptItem(
            @PathVariable String receiptId, @PathVariable String receiptItemId) {
        return receiptManager.cancelReceiptItem(receiptId, receiptItemId);
    }

    @PatchMapping("/{receiptId}/items/{receiptItemId}")
    @RolesAllowed({TELLER, SR_TELLER})
    public @ResponseBody
    ReceiptDto updateReceiptItem(
            @PathVariable String receiptId,
            @PathVariable String receiptItemId,
            @Valid @RequestBody UpdateReceiptItemRequestDto updateReceiptItemRequestDto
    ) {
        return receiptManager.updateReceiptItemAmount(
                receiptId, receiptItemId, updateReceiptItemRequestDto.getAmount());
    }
}
