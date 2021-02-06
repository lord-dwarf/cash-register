package com.polinakulyk.cashregister.service.api;

import com.polinakulyk.cashregister.controller.dto.ProductSoldResponseDto;
import com.polinakulyk.cashregister.db.entity.Product;
import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    List<ProductSoldResponseDto> listProductsSold(LocalDate start, LocalDate end);
    List<Product> listProductsNotSold(LocalDate start, LocalDate end);
}
