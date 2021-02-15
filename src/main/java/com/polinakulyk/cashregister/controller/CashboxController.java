package com.polinakulyk.cashregister.controller;

import com.polinakulyk.cashregister.controller.dto.CashboxShiftStatusDto;
import com.polinakulyk.cashregister.exception.CashRegisterException;
import com.polinakulyk.cashregister.security.api.AuthHelper;
import com.polinakulyk.cashregister.service.api.CashboxService;
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
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Controller
@RequestMapping("/api/cashbox")
// TODO configure CORS
@CrossOrigin
public class CashboxController {

    private final CashboxService cashboxService;

    public CashboxController(CashboxService cashboxService) {
        this.cashboxService = cashboxService;
    }

    @PatchMapping("/activate-shift")
    @RolesAllowed({TELLER, SR_TELLER})
    public @ResponseBody
    CashboxShiftStatusDto activateShift(@RequestBody Map emptyRequestBody) {
        if (!emptyRequestBody.isEmpty()) {
            throw new CashRegisterException(BAD_REQUEST, "Request body must be empty");
        }
        return cashboxService.activateShift();
    }

    @GetMapping("/shift-status")
    @RolesAllowed({TELLER, SR_TELLER})
    public @ResponseBody
    CashboxShiftStatusDto getShiftStatusAndElapsedTime() {
        return cashboxService.getShiftStatusAndElapsedTime();
    }
}