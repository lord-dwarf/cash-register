package com.polinakulyk.cashregister.service.api;

import com.polinakulyk.cashregister.controller.dto.FindProductsDto;
import com.polinakulyk.cashregister.db.entity.Product;
import com.polinakulyk.cashregister.db.entity.User;
import java.util.List;

public interface ProductService {
    Product create(Product product);
    List<Product> findAll();
    List<Product> findByFilter(FindProductsDto findProductsDto);
    void update(Product product);
}
