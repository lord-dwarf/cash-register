package com.polinakulyk.cashregister.service.api;

import com.polinakulyk.cashregister.controller.dto.FindProductsDto;
import com.polinakulyk.cashregister.controller.dto.ProductFilterKind;
import com.polinakulyk.cashregister.db.entity.Product;
import com.polinakulyk.cashregister.db.entity.User;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    Product create(Product product);
    Iterable<Product> findAll();
    Product findExistingById(String id);
    List<Product> findByFilter(ProductFilterKind filterKind, String filterValue);
    void update(Product product);
}
