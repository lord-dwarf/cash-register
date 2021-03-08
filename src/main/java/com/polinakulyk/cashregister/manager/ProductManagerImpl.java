package com.polinakulyk.cashregister.manager;

import com.polinakulyk.cashregister.manager.api.ProductManager;
import com.polinakulyk.cashregister.manager.mapper.ProductMapper;
import com.polinakulyk.cashregister.service.api.ProductService;
import com.polinakulyk.cashregister.service.api.dto.ProductDto;
import com.polinakulyk.cashregister.service.api.dto.ProductFilterKind;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * The purpose of the class is to isolate {@link ProductService} entities from
 * controller layer:
 * - transaction boundary is left on the underlying service layer;
 * - non-transactional manager layer allows fine-grained control over what data is available
 *   to controller layer (to prevent unnecessary data to be loaded from a database).
 */
@Component
public class ProductManagerImpl implements ProductManager {

    private final ProductService productService;
    private final ProductMapper productMapper;

    public ProductManagerImpl(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @Override
    public ProductDto create(ProductDto productDto) {
        var product = productMapper.productDtoToProduct(productDto);
        product = productService.create(product);
        return productMapper.productToProductDto(product);
    }

    @Override
    public List<ProductDto> findAll() {
        return productService.findAll().stream()
                .map(productMapper::productToProductDto)
                .collect(Collectors.toList());

    }

    @Override
    public ProductDto findExistingById(String id) {
        var product = productService.findExistingById(id);
        return productMapper.productToProductDto(product);
    }

    @Override
    public List<ProductDto> findByFilter(ProductFilterKind filterKind, String filterValue) {
        var products = productService.findByFilter(filterKind, filterValue);
        return products.stream()
                .map(productMapper::productToProductDto)
                .collect(Collectors.toList());
    }

    @Override
    public void update(ProductDto productDto) {
        var product = productMapper.productDtoToProduct(productDto);
        productService.update(product);
    }
}
