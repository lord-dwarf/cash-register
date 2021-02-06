package com.polinakulyk.cashregister.db.repository;

import com.polinakulyk.cashregister.db.entity.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, String> {
}
