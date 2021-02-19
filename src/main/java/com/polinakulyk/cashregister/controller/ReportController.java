package com.polinakulyk.cashregister.controller;

import com.polinakulyk.cashregister.service.api.ReportService;
import com.polinakulyk.cashregister.service.api.dto.ReportKind;
import com.polinakulyk.cashregister.service.api.dto.XZReportResponseDto;
import javax.annotation.security.RolesAllowed;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.polinakulyk.cashregister.security.dto.UserRole.Value.SR_TELLER;

@CrossOrigin
@Controller
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/x")
    @RolesAllowed({SR_TELLER})
    public @ResponseBody
    XZReportResponseDto reportX() {
        return reportService.createXZReport(ReportKind.X);
    }

    @GetMapping("/z")
    @RolesAllowed({SR_TELLER})
    public @ResponseBody
    XZReportResponseDto reportZ() {
        return reportService.createXZReport(ReportKind.Z);
    }
}