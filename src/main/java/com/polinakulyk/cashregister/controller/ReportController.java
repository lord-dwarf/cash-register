package com.polinakulyk.cashregister.controller;

import com.polinakulyk.cashregister.controller.dto.DateRangeDto;
import com.polinakulyk.cashregister.controller.dto.ProductSoldResponseDto;
import com.polinakulyk.cashregister.db.entity.Product;
import com.polinakulyk.cashregister.db.entity.Receipt;
import com.polinakulyk.cashregister.service.api.ReportService;
import com.polinakulyk.cashregister.util.CashRegisterUtil;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.polinakulyk.cashregister.db.entity.Role.Code.MERCH;
import static com.polinakulyk.cashregister.db.entity.Role.Code.SR_TELLER;
import static com.polinakulyk.cashregister.db.entity.Role.Code.TELLER;

@Controller
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/products-sold")
    @RolesAllowed({MERCH})
    public @ResponseBody List<ProductSoldResponseDto> listProductsSold(
            @RequestBody DateRangeDto dateRangeDto) {
        return reportService.listProductsSold(
                dateRangeDto.getStart(), dateRangeDto.getEnd());
    }

    @GetMapping("/products-not-sold")
    @RolesAllowed({MERCH})
    public @ResponseBody List<Product> listProductsNotSold(
            @RequestBody DateRangeDto dateRangeDto) {
        List<Product> products = reportService.listProductsNotSold(
                dateRangeDto.getStart(), dateRangeDto.getEnd());
        products.forEach(CashRegisterUtil::strip);
        return products;
    }

}