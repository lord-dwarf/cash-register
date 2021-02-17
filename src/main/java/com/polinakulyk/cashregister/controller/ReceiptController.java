package com.polinakulyk.cashregister.controller;

import com.polinakulyk.cashregister.controller.dto.AddReceiptItemRequestDto;
import com.polinakulyk.cashregister.controller.dto.UpdateReceiptItemRequestDto;
import com.polinakulyk.cashregister.db.entity.Receipt;
import com.polinakulyk.cashregister.security.api.AuthHelper;
import com.polinakulyk.cashregister.service.ServiceHelper;
import com.polinakulyk.cashregister.service.api.ReceiptService;
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
import static com.polinakulyk.cashregister.service.ServiceHelper.strip;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

@Controller
@RequestMapping("/api/receipts")
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
        return stream(receiptService.findAll().spliterator(), false)
                .map(ServiceHelper::strip)
                .collect(toList());
    }

    @GetMapping("/by-teller")
    @RolesAllowed({TELLER, SR_TELLER})
    public @ResponseBody
    List<Receipt> listReceiptsByTeller() {
        return stream(
                receiptService.findAllByTellerId(authHelper.getUserId()).spliterator(),
                false)
                .map(ServiceHelper::strip)
                .collect(toList());
    }

    @PostMapping
    @RolesAllowed({TELLER, SR_TELLER})
    public @ResponseBody
    Receipt createReceipt(@RequestBody Map emptyRequestBody) {
        return strip(receiptService.createReceipt(authHelper.getUserId()));
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
    Receipt completeReceipt(@PathVariable String id) {
        return strip(receiptService.completeReceipt(id));
    }

    @PatchMapping("/{id}/cancel")
    @RolesAllowed({SR_TELLER})
    public @ResponseBody
    Receipt cancelReceipt(@PathVariable String id) {
        return strip(receiptService.cancelReceipt(id));
    }

    @PostMapping("/{receiptId}/items")
    @RolesAllowed({TELLER, SR_TELLER})
    public @ResponseBody
    Receipt addReceiptItem(
            @PathVariable String receiptId,
            @Valid @RequestBody AddReceiptItemRequestDto addReceiptItemRequestDto
    ) {
        return strip(receiptService.addReceiptItem(
                receiptId,
                addReceiptItemRequestDto.getProductId(),
                addReceiptItemRequestDto.getReceiptItemAmountAmount()));
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
            @Valid @RequestBody UpdateReceiptItemRequestDto updateReceiptItemRequestDto
    ) {
        return strip(receiptService.updateReceiptItemAmount(
                receiptId, receiptItemId, updateReceiptItemRequestDto.getAmount()));
    }

}
