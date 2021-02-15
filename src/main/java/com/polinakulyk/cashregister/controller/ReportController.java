package com.polinakulyk.cashregister.controller;

import com.polinakulyk.cashregister.controller.dto.DateRangeDto;
import com.polinakulyk.cashregister.controller.dto.XZReportDto;
import com.polinakulyk.cashregister.db.entity.Product;
import com.polinakulyk.cashregister.service.api.ReportService;
import com.polinakulyk.cashregister.util.CashRegisterUtil;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.polinakulyk.cashregister.security.dto.UserRole.Value.SR_TELLER;

@Controller
@RequestMapping("/api/reports")
// TODO configure CORS
@CrossOrigin
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/x")
    @RolesAllowed({SR_TELLER})
    public @ResponseBody
    XZReportDto reportX() {
        return reportService.createXZReport("X");
    }

    @GetMapping("/z")
    @RolesAllowed({SR_TELLER})
    public @ResponseBody
    XZReportDto reportZ() {
        return reportService.createXZReport("Z");
    }
}