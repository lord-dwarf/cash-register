package com.polinakulyk.cashregister.controller;

import com.polinakulyk.cashregister.controller.dto.FindProductsRequestDto;
import com.polinakulyk.cashregister.db.entity.Product;
import com.polinakulyk.cashregister.service.ServiceHelper;
import com.polinakulyk.cashregister.service.api.ProductService;

import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.polinakulyk.cashregister.security.dto.UserRole.Value.MERCH;
import static com.polinakulyk.cashregister.security.dto.UserRole.Value.SR_TELLER;
import static com.polinakulyk.cashregister.security.dto.UserRole.Value.TELLER;
import static com.polinakulyk.cashregister.service.ServiceHelper.strip;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@CrossOrigin
@Controller
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @RolesAllowed({MERCH, TELLER, SR_TELLER})
    public @ResponseBody
    Iterable<Product> listProducts() {
        return productService.findAll()
                .stream()
                .map(ServiceHelper::strip)
                .collect(toList());
    }

    @PostMapping
    @RolesAllowed({MERCH})
    public @ResponseBody
    Product createProduct(@Valid @RequestBody Product product) {
        return productService.create(product);
    }

    @GetMapping("/{id}")
    @RolesAllowed({MERCH, TELLER, SR_TELLER})
    public @ResponseBody
    Product getProduct(@PathVariable String id) {
        return strip(productService.findExistingById(id));
    }

    @PostMapping("/find")
    @RolesAllowed({MERCH, TELLER, SR_TELLER})
    public @ResponseBody
    List<Product> findProducts(
            @Valid @RequestBody FindProductsRequestDto findProductsRequestDto
    ) {
        return productService.findByFilter(
                findProductsRequestDto.getFilterKind(),
                findProductsRequestDto.getFilterValue())
                .stream()
                .map(ServiceHelper::strip)
                .collect(toList());
    }

    @PutMapping("/{id}")
    @RolesAllowed({MERCH})
    public ResponseEntity updateProduct(
            @PathVariable String id,
            @Valid @RequestBody Product product
    ) {
        productService.update(product.setId(id));
        return new ResponseEntity((String)null, NO_CONTENT);
    }
}