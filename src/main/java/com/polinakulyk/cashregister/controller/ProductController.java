package com.polinakulyk.cashregister.controller;

import com.polinakulyk.cashregister.controller.dto.FindProductsRequestDto;
import com.polinakulyk.cashregister.manager.api.ProductManager;
import com.polinakulyk.cashregister.service.api.dto.ProductDto;

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

import static org.springframework.http.HttpStatus.NO_CONTENT;

@CrossOrigin
@Controller
@RequestMapping("/api/products")
public class ProductController {
    private final ProductManager productManager;

    public ProductController(ProductManager productManager) {
        this.productManager = productManager;
    }

    @GetMapping
    @RolesAllowed({MERCH, TELLER, SR_TELLER})
    public @ResponseBody
    List<ProductDto> listProducts() {
        return productManager.findAll();
    }

    @PostMapping
    @RolesAllowed({MERCH})
    public @ResponseBody
    ProductDto createProduct(@RequestBody ProductDto productDto) {
        return productManager.create(productDto);
    }

    @GetMapping("/{id}")
    @RolesAllowed({MERCH, TELLER, SR_TELLER})
    public @ResponseBody
    ProductDto getProduct(@PathVariable String id) {
        return productManager.findExistingById(id);
    }

    @PostMapping("/find")
    @RolesAllowed({MERCH, TELLER, SR_TELLER})
    public @ResponseBody
    List<ProductDto> findProducts(
            @Valid @RequestBody FindProductsRequestDto findProductsRequestDto
    ) {
        return productManager.findByFilter(
                findProductsRequestDto.getFilterKind(),
                findProductsRequestDto.getFilterValue());
    }

    @PutMapping("/{id}")
    @RolesAllowed({MERCH})
    public ResponseEntity<String> updateProduct(
            @PathVariable String id,
            @RequestBody ProductDto productDto
    ) {
        productManager.update(productDto.setId(id));
        return new ResponseEntity<>((String) null, NO_CONTENT);
    }
}