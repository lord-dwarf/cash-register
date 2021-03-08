package com.polinakulyk.cashregister.manager.mapper;

import com.polinakulyk.cashregister.db.entity.Product;
import com.polinakulyk.cashregister.service.api.dto.ProductDto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * The purpose of this class is to map {@link Product} to {@link ProductDto} and vice versa.
 * With MapStruct we only need to create the interface, and the library will
 * automatically create a concrete implementation during compile time.
 */
@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mappings({
            @Mapping(source = "product.id", target = "id"),
            @Mapping(source = "product.code", target = "code"),
            @Mapping(source = "product.category", target = "category"),
            @Mapping(source = "product.name", target = "name"),
            @Mapping(source = "product.details", target = "details"),
            @Mapping(source = "product.price", target = "price"),
            @Mapping(source = "product.amountUnit", target = "amountUnit"),
            @Mapping(source = "product.amountAvailable", target = "amountAvailable"),
    })
    ProductDto productToProductDto(Product product);

    @Mappings({
            @Mapping(source = "productDto.id", target = "id"),
            @Mapping(source = "productDto.code", target = "code"),
            @Mapping(source = "productDto.category", target = "category"),
            @Mapping(source = "productDto.name", target = "name"),
            @Mapping(source = "productDto.details", target = "details"),
            @Mapping(source = "productDto.price", target = "price"),
            @Mapping(source = "productDto.amountUnit", target = "amountUnit"),
            @Mapping(source = "productDto.amountAvailable", target = "amountAvailable"),
    })
    Product productDtoToProduct(ProductDto productDto);
}
