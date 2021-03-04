package com.polinakulyk.cashregister.controller;

import com.polinakulyk.cashregister.service.api.CashboxService;
import com.polinakulyk.cashregister.service.api.dto.ShiftStatusSummaryResponseDto;

import java.util.Map;
import javax.annotation.security.RolesAllowed;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.polinakulyk.cashregister.security.dto.UserRole.Value.SR_TELLER;
import static com.polinakulyk.cashregister.security.dto.UserRole.Value.TELLER;

@CrossOrigin
@Controller
@RequestMapping("/api/cashbox")
public class CashboxController {
    private final CashboxService cashboxService;

    public CashboxController(CashboxService cashboxService) {
        this.cashboxService = cashboxService;
    }

    @PatchMapping("/activate-shift")
    @RolesAllowed({TELLER, SR_TELLER})
    public @ResponseBody
    ShiftStatusSummaryResponseDto activateShift(@RequestBody Map<?, ?> emptyRequestBody) {
        return cashboxService.activateShift();
    }

    @GetMapping("/shift-status")
    @RolesAllowed({TELLER, SR_TELLER})
    public @ResponseBody
    ShiftStatusSummaryResponseDto pollShiftStatusAndElapsedTime() {
        return cashboxService.getShiftStatusSummary();
    }
}