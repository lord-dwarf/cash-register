package com.polinakulyk.cashregister.controller;

import com.polinakulyk.cashregister.controller.dto.AddReceiptItemDto;
import com.polinakulyk.cashregister.controller.dto.UpdateReceiptItemDto;
import com.polinakulyk.cashregister.db.entity.Receipt;
import com.polinakulyk.cashregister.exception.CashRegisterException;
import com.polinakulyk.cashregister.security.api.AuthHelper;
import com.polinakulyk.cashregister.service.ServiceHelper;
import com.polinakulyk.cashregister.service.api.ReceiptService;
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
import static com.polinakulyk.cashregister.service.ServiceHelper.strip;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

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
    public @ResponseBody
    Iterable<Receipt> listReceipts() {
        Iterable<Receipt> receiptsStripped = receiptService.findAll();
        receiptsStripped.forEach(ServiceHelper::strip);
        return receiptsStripped;
    }

    @GetMapping("/by-teller")
    @RolesAllowed({TELLER, SR_TELLER})
    public @ResponseBody
    List<Receipt> listReceiptsByTeller() {
        String tellerId = authHelper.getUserId();
        List<Receipt> receiptsStripped = receiptService.findAllByTellerId(tellerId);
        receiptsStripped.forEach(ServiceHelper::strip);
        return receiptsStripped;
    }

    @PostMapping
    @RolesAllowed({TELLER, SR_TELLER})
    public @ResponseBody
    Receipt createReceipt(@RequestBody Map emptyRequestBody) {
        if (!emptyRequestBody.isEmpty()) {
            throw new CashRegisterException(BAD_REQUEST, "Request body must be empty");
        }
        String userId = authHelper.getUserId();
        return strip(receiptService.createReceipt(userId));
    }

    @GetMapping("/{id}")
    @RolesAllowed({TELLER, SR_TELLER})
    public @ResponseBody
    Receipt getReceipt(@PathVariable String id) {
        return strip(receiptService.findExistingById(id));
    }

    @PatchMapping("/{id}/complete")
    @RolesAllowed({TELLER, SR_TELLER})
    public @ResponseBody
    Receipt completeReceipt(
            @PathVariable String id, HttpServletResponse response) {
        return strip(receiptService.completeReceipt(id));
    }

    @PatchMapping("/{id}/cancel")
    @RolesAllowed({SR_TELLER})
    public @ResponseBody
    Receipt cancelReceipt(
            @PathVariable String id, HttpServletResponse response) {
        return strip(receiptService.cancelReceipt(id));
    }

    @PostMapping("/{receiptId}/items")
    @RolesAllowed({TELLER, SR_TELLER})
    public @ResponseBody
    Receipt addReceiptItem(
            @PathVariable String receiptId, @RequestBody AddReceiptItemDto addReceiptItemDto) {
        if (null == addReceiptItemDto.getProductId()) {
            throw new CashRegisterException(BAD_REQUEST, "Product id must be set");
        }
        return strip(receiptService.addReceiptItem(
                receiptId,
                addReceiptItemDto.getProductId(),
                addReceiptItemDto.getReceiptItemAmountAmount()));
    }

    @DeleteMapping("/{receiptId}/items/{receiptItemId}")
    @RolesAllowed({SR_TELLER})
    public @ResponseBody
    Receipt cancelReceiptItem(
            @PathVariable String receiptId, @PathVariable String receiptItemId) {
        return strip(receiptService.cancelReceiptItem(receiptId, receiptItemId));
    }

    @PatchMapping("/{receiptId}/items/{receiptItemId}")
    @RolesAllowed({TELLER, SR_TELLER})
    public @ResponseBody
    Receipt updateReceiptItem(
            @PathVariable String receiptId,
            @PathVariable String receiptItemId,
            @RequestBody UpdateReceiptItemDto updateReceiptItemDto
    ) {
        return strip(receiptService.updateReceiptItemAmount(
                receiptId, receiptItemId, updateReceiptItemDto.getAmount()));
    }

}
