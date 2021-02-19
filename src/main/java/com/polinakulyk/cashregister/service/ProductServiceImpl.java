package com.polinakulyk.cashregister.service;

import com.polinakulyk.cashregister.db.entity.Product;
import com.polinakulyk.cashregister.db.repository.ProductRepository;
import com.polinakulyk.cashregister.exception.CashRegisterException;
import com.polinakulyk.cashregister.exception.CashRegisterProductNotFoundException;
import com.polinakulyk.cashregister.security.api.AuthHelper;
import com.polinakulyk.cashregister.service.api.dto.ProductFilterKind;
import com.polinakulyk.cashregister.service.api.ProductService;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.Util;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.polinakulyk.cashregister.util.CashRegisterUtil.quote;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

/**
 * Product service.
 */
@Slf4j
@Service
public class ProductServiceImpl implements ProductService {
    private static final int FOUND_PRODUCTS_LIMIT = 5;

    private final ProductRepository productRepository;
    private final AuthHelper authHelper;

    public ProductServiceImpl(ProductRepository productRepository, AuthHelper authHelper) {
        this.productRepository = productRepository;
        this.authHelper = authHelper;
    }

    @Override
    @Transactional
    public Product create(Product product) {
        var userId = authHelper.getUserId();
        log.debug("BEGIN Create product by user: '{}'", userId);

        product = productRepository.save(product);

        log.info("DONE Create product by user: '{}', product: '{}'",
                userId, product.getId());
        return product;
    }

    @Override
    @Transactional
    public List<Product> findAll() {
        var products = stream(productRepository.findAll().spliterator(), false)
                .collect(toList());

        log.debug("DONE Find products: {}", products.size());
        return products;
    }

    /**
     * Find the existing product by id, otherwise throw
     * {@link CashRegisterProductNotFoundException}.
     * <p>
     * Used as a way to retrieve the product entity that must be present. Otherwise the specific
     * exception is thrown, that will result in HTTP 404.
     *
     * @param productId
     * @return
     */
    @Override
    @Transactional
    public Product findExistingById(String productId) {
        var product = productRepository.findById(productId).orElseThrow(() ->
                new CashRegisterProductNotFoundException(productId));

        log.debug("DONE Find product: '{}'", productId);
        return product;
    }

    /**
     * Provides list of products selected by a given filter. The product field for filtering
     * depends on a supplied product filter kind.
     *
     * @param filterKind
     * @param filterValue
     * @return
     */
    @Override
    @Transactional
    public List<Product> findByFilter(ProductFilterKind filterKind, String filterValue) {
        Pattern filterPattern = Pattern.compile(filterValue + ".*", Pattern.CASE_INSENSITIVE);
        Function<Product, String> fun;
        switch (filterKind) {
            case CODE: {
                fun = Product::getCode;
                break;
            }
            case NAME: {
                fun = Product::getName;
                break;
            }
            default:
                throw new UnsupportedOperationException(quote(
                        "Product filter kind not supported", filterKind));
        }
        var getProductFieldFun = fun;
        var filteredProducts = stream(productRepository.findAll().spliterator(), false)
                .filter(p -> filterPattern.matcher(getProductFieldFun.apply(p)).matches())
                .limit(FOUND_PRODUCTS_LIMIT)
                .collect(toList());

        log.debug("DONE Filter products: {}", filteredProducts.size());
        return filteredProducts;
    }

    @Override
    @Transactional
    public void update(Product product) {
        var userId = authHelper.getUserId();
        log.debug("BEGIN Update product by user: '{}', product: '{}'",
                userId, product.getId());

        // we omit upsert, because the creation of a new product would violate business logic
        if (!productRepository.existsById(product.getId())) {
            throw new CashRegisterProductNotFoundException(product.getId());
        }
        productRepository.save(product);

        log.info("DONE Update product by user: '{}', product: '{}'",
                userId, product.getId());
    }
}
