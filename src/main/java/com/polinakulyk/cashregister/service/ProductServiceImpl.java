package com.polinakulyk.cashregister.service;

import com.polinakulyk.cashregister.db.entity.Product;
import com.polinakulyk.cashregister.db.repository.ProductRepository;
import com.polinakulyk.cashregister.exception.CashRegisterProductNotFoundException;
import com.polinakulyk.cashregister.service.api.ProductFilterKind;
import com.polinakulyk.cashregister.service.api.ProductService;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.polinakulyk.cashregister.util.CashRegisterUtil.quote;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

/**
 * Product service.
 */
@Service
public class ProductServiceImpl implements ProductService {
    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

    private static final int FOUND_PRODUCTS_LIMIT = 5;

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public Product create(Product product) {
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Iterable<Product> findAll() {
        return productRepository.findAll();
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
        return productRepository.findById(productId).orElseThrow(() ->
                new CashRegisterProductNotFoundException(productId));
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
        return stream(productRepository.findAll().spliterator(), false)
                .filter(p -> filterPattern.matcher(getProductFieldFun.apply(p)).matches())
                .limit(FOUND_PRODUCTS_LIMIT)
                .collect(toList());
    }

    @Override
    @Transactional
    public void update(Product product) {
        // we omit upsert, because the creation of a new product would violate business logic
        if (!productRepository.existsById(product.getId())) {
            throw new CashRegisterProductNotFoundException(product.getId());
        }
        productRepository.save(product);
    }
}
