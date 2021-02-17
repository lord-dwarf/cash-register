package com.polinakulyk.cashregister.service.api;

import com.polinakulyk.cashregister.db.entity.Product;
import java.util.List;

public interface ProductService {
    Product create(Product product);
    Iterable<Product> findAll();
    Product findExistingById(String id);
    List<Product> findByFilter(ProductFilterKind filterKind, String filterValue);
    void update(Product product);
}
