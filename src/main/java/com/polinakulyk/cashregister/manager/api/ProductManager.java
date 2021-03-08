package com.polinakulyk.cashregister.manager.api;

import com.polinakulyk.cashregister.service.api.dto.ProductDto;
import com.polinakulyk.cashregister.service.api.dto.ProductFilterKind;

import java.util.List;

public interface ProductManager {
    ProductDto create(ProductDto product);

    List<ProductDto> findAll();

    ProductDto findExistingById(String id);

    List<ProductDto> findByFilter(ProductFilterKind filterKind, String filterValue);

    void update(ProductDto product);
}
