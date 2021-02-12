package com.polinakulyk.cashregister.service;

import com.polinakulyk.cashregister.controller.dto.ProductSoldResponseDto;
import com.polinakulyk.cashregister.db.entity.Product;
import com.polinakulyk.cashregister.db.entity.Receipt;
import com.polinakulyk.cashregister.db.entity.ReceiptItem;
import com.polinakulyk.cashregister.db.repository.ProductRepository;
import com.polinakulyk.cashregister.db.repository.ReceiptRepository;
import com.polinakulyk.cashregister.exception.CashRegisterException;
import com.polinakulyk.cashregister.service.api.ReportService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.polinakulyk.cashregister.service.ServiceHelper.calcCost;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.*;

@Service
public class ReportServiceImpl implements ReportService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportServiceImpl.class);

    // we use repos, because we need an iterator that may potentially be lazy,
    // while services provide eager lists, instead of iterator
    private final ReceiptRepository receiptRepository;
    private final ProductRepository productRepository;

    public ReportServiceImpl(
            ReceiptRepository receiptRepository, ProductRepository productRepository) {
        this.receiptRepository = receiptRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public List<ProductSoldResponseDto> listProductsSold(LocalDate start, LocalDate end) {

        // correct and validate time bounds
        if (start == null) {
            start = now().toLocalDate();
        }
        if (end == null) {
            end = now().toLocalDate();
        }
        if (start.isAfter(end)) {
            throw new CashRegisterException(
                    HttpStatus.BAD_REQUEST,
                    quote("Start date must not be after end date", start, end));
        }

        // select COMPLETED receipts
        List<Receipt> receipts = new ArrayList<>();
        for (Receipt receipt : receiptRepository.findAll()) {
            if ("COMPLETED".equals(receipt.getStatus())) {
                LocalDate receiptDate =  receipt.getCheckoutTime().toLocalDate();
                if ((start.isBefore(receiptDate) || start.isEqual(receiptDate))
                        && (end.isAfter(receiptDate) || end.isEqual(receiptDate))) {
                    receipts.add(receipt);
                }
            }
        }

        // collect products sold, calculate total cost of each product sold
        Map<String, ProductSoldResponseDto> productIdToProductSold = new HashMap<>();
        for (Receipt receipt : receipts) {
            for (ReceiptItem receiptItem : receipt.getReceiptItems()) {
                int receiptItemCost = calcCost(
                        receiptItem.getPrice(),
                        receiptItem.getAmount(),
                        receiptItem.getAmountUnit());
                Product product = receiptItem.getProduct();
                String productId = product.getId();
                ProductSoldResponseDto productSold = productIdToProductSold.get(productId);
                if (productSold == null) {
                    productSold = new ProductSoldResponseDto()
                            .setProductId(productId)
                            .setProductCode(product.getCode())
                            .setProductCategory(product.getCategory())
                            .setProductName(product.getName())
                            .setProductAmountUnit(product.getAmountUnit())
                            .setProductAmountAvailable(product.getAmountAvailable())
                            .setProductPrice(product.getPrice());
                    productIdToProductSold.put(productId, productSold);
                }
                productSold.setProductSoldSumTotal(
                        productSold.getProductSoldSumTotal() + receiptItemCost);
            }
        }
        return new ArrayList<>(productIdToProductSold.values());
    }

    @Override
    public List<Product> listProductsNotSold(LocalDate start, LocalDate end) {

        // correct and validate time bounds
        if (start == null) {
            start = now().toLocalDate();
        }
        if (end == null) {
            end = now().toLocalDate();
        }
        if (start.isAfter(end)) {
            throw new CashRegisterException(
                    HttpStatus.BAD_REQUEST,
                    quote("Start date must not be after end date", start, end));
        }

        // select products
        List<Product> products = new ArrayList<>();
        outer:
        for (Product product : productRepository.findAll()) {
            for (ReceiptItem receiptItem : product.getReceiptItems()) {
                Receipt receipt = receiptItem.getReceipt();
                if ("COMPLETED".equals(receipt.getStatus())) {
                    LocalDate receiptDate = receipt.getCheckoutTime().toLocalDate();
                    if ((start.isBefore(receiptDate) || start.isEqual(receiptDate))
                            && (end.isAfter(receiptDate) || end.isEqual(receiptDate))) {
                        continue outer;
                    }
                }
            }
            products.add(product);
        }

        return products;
    }
}
