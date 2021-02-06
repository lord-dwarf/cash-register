package com.polinakulyk.cashregister.service.api;

import com.polinakulyk.cashregister.controller.dto.FindProductsDto;
import com.polinakulyk.cashregister.db.entity.Product;
import java.util.List;

public interface ProductService {
    List<Product> findAll();
    List<Product> findByFilter(FindProductsDto findProductsDto);
    void update(Product product);
}
