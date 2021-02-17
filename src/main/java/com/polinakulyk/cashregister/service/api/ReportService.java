package com.polinakulyk.cashregister.service.api;

import com.polinakulyk.cashregister.service.api.dto.XZReportResponseDto;

public interface ReportService {
    XZReportResponseDto createXZReport(ReportKind reportKind);
}
