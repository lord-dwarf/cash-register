package com.polinakulyk.cashregister.service.api;

import com.polinakulyk.cashregister.controller.dto.XZReportDto;
import com.polinakulyk.cashregister.db.entity.Product;
import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    XZReportDto createXZReport(String reportKind);
}
