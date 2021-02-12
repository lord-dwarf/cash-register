package com.polinakulyk.cashregister.controller;

import com.polinakulyk.cashregister.controller.dto.UpdateReceiptItemDto;
import com.polinakulyk.cashregister.db.entity.Receipt;
import com.polinakulyk.cashregister.db.entity.ReceiptItem;
import com.polinakulyk.cashregister.exception.CashRegisterException;
import com.polinakulyk.cashregister.security.api.AuthHelper;
import com.polinakulyk.cashregister.service.api.ReceiptService;
import com.polinakulyk.cashregister.util.CashRegisterUtil;
import java.util.List;
import java.util.Map;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
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
import static com.polinakulyk.cashregister.util.CashRegisterUtil.*;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.quote;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Controller
@RequestMapping("/api/receipts")
// TODO configure CORS
@CrossOrigin
public class ReceiptController {
    private final ReceiptService receiptService;
    private final AuthHelper authHelper;

    public ReceiptController(ReceiptService receiptService, AuthHelper authHelper) {
        this.receiptService = receiptService;
        this.authHelper = authHelper;
    }

    @GetMapping
    @RolesAllowed({SR_TELLER})
    public @ResponseBody List<Receipt> listReceipts() {
        List<Receipt> receiptsStripped = receiptService.findAll();
        receiptsStripped.forEach(CashRegisterUtil::strip);
        return receiptsStripped;
    }

    @GetMapping("/by-teller")
    @RolesAllowed({TELLER, SR_TELLER})
    public @ResponseBody List<Receipt> listReceiptsByTeller() {
        String tellerId = authHelper.getUserId();
        List<Receipt> receiptsStripped = receiptService.findAllByTellerId(tellerId);
        receiptsStripped.forEach(CashRegisterUtil::strip);
        return receiptsStripped;
    }

    @PostMapping
    @RolesAllowed({TELLER, SR_TELLER})
    public @ResponseBody Receipt createReceipt(@RequestBody Map emptyRequestBody) {
        if (!emptyRequestBody.isEmpty()) {
            throw new CashRegisterException(BAD_REQUEST, "Request body must be empty");
        }
        String userId = authHelper.getUserId();
        return strip(receiptService.createReceipt(userId));
    }

    @GetMapping("/{id}")
    @RolesAllowed({TELLER, SR_TELLER})
    public @ResponseBody Receipt getReceipt(@PathVariable String id) {
        return strip(receiptService.findById(id).orElseThrow(() ->
                new CashRegisterException(NOT_FOUND, quote("Receipt does not exist", id))));
    }

    @PatchMapping("/{id}/complete")
    @RolesAllowed({TELLER, SR_TELLER})
    public @ResponseBody Receipt completeReceipt(
            @PathVariable String id, HttpServletResponse response) {
        return strip(receiptService.complete(id));
    }

    @PatchMapping("/{id}/cancel")
    @RolesAllowed({SR_TELLER})
    public @ResponseBody Receipt cancelReceipt(
            @PathVariable String id, HttpServletResponse response) {
        return strip(receiptService.cancel(id));
    }

    @PostMapping("/{receiptId}/items")
    @RolesAllowed({TELLER, SR_TELLER})
    public @ResponseBody Receipt addReceiptItem(
            @PathVariable String receiptId, @RequestBody ReceiptItem receiptItem) {
        if (null != receiptItem.getId()) {
            throw new CashRegisterException(
                    BAD_REQUEST,
                    quote("Receipt item id must not be set", receiptItem.getId()));
        }
        if (null != receiptItem.getReceipt()) {
            throw new CashRegisterException(BAD_REQUEST, "Receipt must not be set");
        }
        if (null == receiptItem.getProduct() || null == receiptItem.getProduct().getId()) {
            throw new CashRegisterException(BAD_REQUEST, "Product id must be set");
        }
        return strip(receiptService.add(receiptId, receiptItem));
    }

    @DeleteMapping("/{receiptId}/items/{receiptItemId}")
    @RolesAllowed({SR_TELLER})
    public @ResponseBody Receipt cancelReceiptItem(
            @PathVariable String receiptId, @PathVariable String receiptItemId) {
        return strip(receiptService.cancel(receiptId, receiptItemId));
    }

    @PatchMapping("/{receiptId}/items/{receiptItemId}")
    @RolesAllowed({TELLER, SR_TELLER})
    public @ResponseBody Receipt updateReceiptItem(
            @PathVariable String receiptId,
            @PathVariable String receiptItemId,
            @RequestBody UpdateReceiptItemDto updateReceiptItemDto
    ) {
        return strip(receiptService.update(receiptId, receiptItemId, updateReceiptItemDto));
    }

}
