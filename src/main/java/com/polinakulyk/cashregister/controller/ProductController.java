package com.polinakulyk.cashregister.controller;

import com.polinakulyk.cashregister.controller.dto.FindProductsDto;
import com.polinakulyk.cashregister.db.entity.Product;
import com.polinakulyk.cashregister.exception.CashRegisterException;
import com.polinakulyk.cashregister.service.api.ProductService;
import com.polinakulyk.cashregister.util.CashRegisterUtil;
import java.util.List;
import java.util.Set;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
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
import static com.polinakulyk.cashregister.util.CashRegisterUtil.quote;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.strip;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@Controller
@RequestMapping("/api/products")
// TODO configure CORS
@CrossOrigin
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @RolesAllowed({MERCH, TELLER, SR_TELLER})
    public @ResponseBody Iterable<Product> listProducts() {
        Iterable<Product> products = productService.findAll();
        products.forEach(CashRegisterUtil::strip);
        return products;
    }

    @PostMapping
    @RolesAllowed({MERCH})
    public @ResponseBody Product createProduct(@Valid @RequestBody Product product) {
        try {
            return productService.create(product);
        } catch (DataIntegrityViolationException e) {
            throw new CashRegisterException(BAD_REQUEST, quote(
                    "Product with this code already exists", product.getCode()));
        }
    }

    @GetMapping("/{id}")
    @RolesAllowed({MERCH, TELLER, SR_TELLER})
    public @ResponseBody Product getProduct(@PathVariable String id) {
        return strip(productService.findById(id).orElseThrow(() ->
                new CashRegisterException(NOT_FOUND, quote("Product does not exist", id))));
    }

    @PostMapping("/find")
    @RolesAllowed({MERCH, TELLER, SR_TELLER})
    public @ResponseBody List<Product> findProducts(
            @RequestBody FindProductsDto findProductsDto) {
        List<Product> products = productService.findByFilter(findProductsDto);
        products.forEach(CashRegisterUtil::strip);
        return products;
    }

    @PutMapping("/{id}")
    @RolesAllowed({MERCH})
    public @ResponseBody String updateProduct(
            @PathVariable String id,
            @Valid @RequestBody Product product,
            HttpServletResponse response
    ) {
        if (!id.equals(product.getId())) {
            throw new CashRegisterException(BAD_REQUEST, quote("Product id does not match", id));
        }
        productService.update(product);
        response.setStatus(NO_CONTENT.value());
        return "";
    }
}