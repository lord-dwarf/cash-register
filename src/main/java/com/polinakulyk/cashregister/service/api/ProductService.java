package com.polinakulyk.cashregister.service.api;

import com.polinakulyk.cashregister.db.entity.Product;
import com.polinakulyk.cashregister.service.api.dto.ProductFilterKind;

import java.util.List;
import javax.validation.Valid;

public interface ProductService {
    Product create(@Valid Product product);

    List<Product> findAll();

    Product findExistingById(String id);

    List<Product> findByFilter(ProductFilterKind filterKind, String filterValue);

    void update(@Valid Product product);
}
