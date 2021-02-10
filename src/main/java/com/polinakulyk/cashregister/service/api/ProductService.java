package com.polinakulyk.cashregister.service.api;

import com.polinakulyk.cashregister.controller.dto.FindProductsDto;
import com.polinakulyk.cashregister.db.entity.Product;
import com.polinakulyk.cashregister.db.entity.User;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    Product create(Product product);
    List<Product> findAll();
    Optional<Product> findById(String id);
    List<Product> findByFilter(FindProductsDto findProductsDto);
    void update(Product product);
}
