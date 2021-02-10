package com.polinakulyk.cashregister.service;

import com.polinakulyk.cashregister.controller.dto.FindProductsDto;
import com.polinakulyk.cashregister.db.entity.Product;
import com.polinakulyk.cashregister.db.repository.ProductRepository;
import com.polinakulyk.cashregister.exception.CashRegisterException;
import com.polinakulyk.cashregister.service.api.ProductService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.polinakulyk.cashregister.util.CashRegisterUtil.quote;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class ProductServiceImpl implements ProductService {

    private static final int FIND_BY_FILTER_LIMIT = 5;

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public Product create(Product product) {
        if (null != product.getId()) {
            throw new CashRegisterException(
                    BAD_REQUEST,
                    quote("Product id must not be set", product.getId()));
        }
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        productRepository.findAll().forEach(products::add);
        return products;
    }

    @Override
    @Transactional
    public Optional<Product> findById(String id) {
        return productRepository.findById(id);
    }

    @Override
    @Transactional
    public List<Product> findByFilter(FindProductsDto findProductsDto) {
        if (findProductsDto.getCodeFilter() != null && findProductsDto.getNameFilter() != null) {
            throw new CashRegisterException(
                    HttpStatus.BAD_REQUEST,
                    quote("Only 1 filter allowed", findProductsDto));
        }
        List<Product> productsFound = new ArrayList<>();
        Pattern codeFilterPattern = findProductsDto.getCodeFilter() != null
                ? Pattern.compile(
                        findProductsDto.getCodeFilter() + ".*", Pattern.CASE_INSENSITIVE)
                : null;
        Pattern nameFilterPattern = findProductsDto.getNameFilter() != null
                ? Pattern.compile(
                        findProductsDto.getNameFilter() + ".*", Pattern.CASE_INSENSITIVE)
                : null;
        productRepository.findAll().forEach((product) -> {
            if (productsFound.size() < FIND_BY_FILTER_LIMIT) {
                boolean isAddProduct = false;
                if (codeFilterPattern != null) {
                    isAddProduct = codeFilterPattern.matcher(product.getCode()).matches();
                } else if (nameFilterPattern != null) {
                    isAddProduct = nameFilterPattern.matcher(product.getName()).matches();
                }
                if (isAddProduct) {
                    productsFound.add(product);
                }
            }
        });
        return productsFound;
    }

    @Override
    @Transactional
    public void update(Product product) {
        if (productRepository.findById(product.getId()).isEmpty()) {
            throw new CashRegisterException(NOT_FOUND, quote("Product not found", product.getId()));
        }
        productRepository.save(product);
    }
}
