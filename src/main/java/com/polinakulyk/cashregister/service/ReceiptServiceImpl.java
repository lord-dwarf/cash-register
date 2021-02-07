package com.polinakulyk.cashregister.service;

import com.polinakulyk.cashregister.controller.dto.UpdateReceiptItemDto;
import com.polinakulyk.cashregister.db.entity.Product;
import com.polinakulyk.cashregister.db.entity.ProductAmountUnit;
import com.polinakulyk.cashregister.db.entity.Receipt;
import com.polinakulyk.cashregister.db.entity.ReceiptItem;
import com.polinakulyk.cashregister.db.repository.ProductRepository;
import com.polinakulyk.cashregister.db.repository.ReceiptItemRepository;
import com.polinakulyk.cashregister.db.repository.ReceiptRepository;
import com.polinakulyk.cashregister.exception.CashRegisterException;
import com.polinakulyk.cashregister.service.api.ReceiptService;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.polinakulyk.cashregister.db.entity.ProductAmountUnit.*;
import static com.polinakulyk.cashregister.db.entity.ProductAmountUnit.Value.*;
import static com.polinakulyk.cashregister.db.entity.ProductAmountUnit.Value.GRAM;
import static com.polinakulyk.cashregister.db.entity.ProductAmountUnit.Value.GRAM;
import static com.polinakulyk.cashregister.db.entity.ProductAmountUnit.Value.GRAM;
import static com.polinakulyk.cashregister.db.entity.ProductAmountUnit.Value.UNIT;
import static com.polinakulyk.cashregister.db.entity.ProductAmountUnit.Value.UNIT;
import static com.polinakulyk.cashregister.db.entity.ProductAmountUnit.Value.UNIT;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.now;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.quote;

@Service
public class ReceiptServiceImpl implements ReceiptService {
    private final ReceiptRepository receiptRepository;
    private final ReceiptItemRepository receiptItemRepository;
    private final ProductRepository productRepository;

    public ReceiptServiceImpl(
            ReceiptRepository receiptRepository,
            ReceiptItemRepository receiptItemRepository,
            ProductRepository productRepository
    ) {
        this.receiptRepository = receiptRepository;
        this.receiptItemRepository = receiptItemRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public List<Receipt> findAll() {
        List<Receipt> receipts = new ArrayList<>();
        receiptRepository.findAll().forEach(receipts::add);
        return receipts;
    }

    @Override
    @Transactional
    public Receipt createReceipt() {
        Receipt receipt = new Receipt();
        receipt.setStatus("CREATED");
        receipt.setCreatedTime(now());
        return receiptRepository.save(receipt);
    }

    @Override
    @Transactional
    public Receipt complete(String receiptId) {
        Optional<Receipt> receiptOpt = receiptRepository.findById(receiptId);
        if (receiptOpt.isEmpty()) {
            throw new CashRegisterException(
                    HttpStatus.NOT_FOUND, // 404 here, because receipt must be present
                    quote("Receipt not found", receiptId));
        }
        Receipt receipt = receiptOpt.get();
        if (!("CREATED".equals(receipt.getStatus()) || "COMPLETED".equals(receipt.getStatus()))) {
            throw new CashRegisterException(
                    quote("Receipt status transition not allowed", receipt.getStatus()));
        }
        if ("COMPLETED".equals(receipt.getStatus())) {
            return receipt;
        }

        // set receipt status COMPLETED
        receipt.setStatus("COMPLETED");
        receipt.setCheckoutTime(now());

        // decrease amount available for products in receipt
        for (ReceiptItem receiptItem : receipt.getItems()) {

            Product receiptItemProduct = receiptItem.getProduct();
            int productAmountAvailable = receiptItemProduct.getAmountAvailable();

            // validate that receipt item amount does not exceed the product amount available
            if (receiptItem.getAmount() > productAmountAvailable) {
                throw new CashRegisterException(quote(
                        "Receipt item amount exceeds product amount available",
                        receiptItem.getAmount()));
            }

            // decrease amount available for product
            receiptItemProduct.setAmountAvailable(productAmountAvailable - receiptItem.getAmount());
            productRepository.save(receiptItemProduct);
        }

        return receiptRepository.save(receipt);
    }

    @Override
    @Transactional
    public Receipt cancel(String receiptId) {
        Optional<Receipt> receiptOpt = receiptRepository.findById(receiptId);
        if (receiptOpt.isEmpty()) {
            throw new CashRegisterException(
                    HttpStatus.NOT_FOUND, // 404 here, because receipt must be present
                    quote("Receipt not found", receiptId));
        }
        Receipt receipt = receiptOpt.get();
        // can cancel from all statuses
        if ("CANCELED".equals(receipt.getStatus())) {
            return receipt;
        }

        // set receipt status CANCELED
        receipt.setStatus("CANCELED");
        receipt.setCheckoutTime(now());

        // increase amount available for products in receipt
        for (ReceiptItem receiptItem : receipt.getItems()) {

            // increase amount available for product
            Product receiptItemProduct = receiptItem.getProduct();
            receiptItemProduct.setAmountAvailable(
                    receiptItemProduct.getAmountAvailable() + receiptItem.getAmount());
            productRepository.save(receiptItemProduct);
        }

        return receiptRepository.save(receipt);
    }

    @Override
    @Transactional
    public Receipt add(String receiptId, ReceiptItem receiptItem) {

        // load receipt and product
        Optional<Receipt> receiptOpt = receiptRepository.findById(receiptId);
        if (receiptOpt.isEmpty()) {
            throw new CashRegisterException(
                    HttpStatus.NOT_FOUND, // 404 here, because receipt must be present
                    quote("Receipt not found", receiptId));
        }
        Receipt receipt = receiptOpt.get();
        Optional<Product> productOpt = productRepository.findById(receiptItem.getProduct().getId());
        if (productOpt.isEmpty()) {
            throw new CashRegisterException(
                    quote("Product not found", receiptItem.getProduct().getId()));
        }
        Product product = productOpt.get();

        // validate receipt status
        if (!"CREATED".equals(receipt.getStatus())) {
            throw new CashRegisterException(quote(
                    "Receipt status does not allow adding of item", receipt.getStatus()));
        }

        // save receipt item amount
        int receiptItemAmountAdded = receiptItem.getAmount();

        // find an existing receipt item
        Optional<ReceiptItem> existingReceiptItemOpt = receipt.getItems().stream()
                .filter((existingReceiptItem) ->
                        product.getId().equals(existingReceiptItem.getProduct().getId())
                ).findFirst();

        // init receipt item
        if (existingReceiptItemOpt.isEmpty()) {

            // init via creating a new receipt item via copying of product
            receiptItem
                    .setReceipt(receipt)
                    .setProduct(product)
                    .setName(product.getName())
                    .setAmountUnit(product.getAmountUnit())
                    .setPrice(product.getPrice());
            receipt.getItems().add(receiptItem);
        } else {

            // init via merging into existing receipt item
            receiptItem = existingReceiptItemOpt.get();
            receiptItem.setAmount(receiptItem.getAmount() + receiptItemAmountAdded);
        }

        // validate that receipt item amount does not exceed the product amount available
        if (receiptItem.getAmount() > receiptItem.getProduct().getAmountAvailable()) {
            throw new CashRegisterException(quote(
                    "Receipt item amount exceeds product amount available",
                    receiptItem.getAmount()));
        }

        // increase receipt price total
        int priceTotalIncrease;
        switch (receiptItem.getAmountUnit()) {
            case GRAM: {
                priceTotalIncrease = receiptItemAmountAdded * receiptItem.getPrice() / 1000;
                break;
            }
            case UNIT: {
                priceTotalIncrease = receiptItemAmountAdded * receiptItem.getPrice();
                break;
            }
            default: throw new CashRegisterException(quote(
                    "Receipt item amount unit not supported",
                    receiptItem.getAmountUnit()));
        }
        receipt.setSumTotal(receipt.getSumTotal() + priceTotalIncrease);

        // save receipt and associated receipt item
        return receiptRepository.save(receipt);
    }

    @Override
    @Transactional
    public Receipt cancel(String receiptId, String receiptItemId) {
        Optional<Receipt> receiptOpt = receiptRepository.findById(receiptId);
        if (receiptOpt.isEmpty()) {
            throw new CashRegisterException(
                    HttpStatus.NOT_FOUND, // 404 here, because receipt must be present
                    quote("Receipt not found", receiptId));
        }
        Receipt receipt = receiptOpt.get();

        // validate receipt status
        if (!"CREATED".equals(receipt.getStatus())) {
            throw new CashRegisterException(quote(
                    "Receipt status does not allow receipt item cancellation",
                    receipt.getStatus()));
        }

        // find receipt item
        Optional<ReceiptItem> receiptItemOpt = receipt.getItems().stream().filter((receiptItem) ->
                receiptItemId.equals(receiptItem.getId())).findFirst();

        if (receiptItemOpt.isPresent()) {
            ReceiptItem receiptItem = receiptItemOpt.get();

            // decrease receipt price total
            int priceTotalDecrease;
            switch (receiptItem.getAmountUnit()) {
                case GRAM: {
                    priceTotalDecrease = receiptItem.getAmount() * receiptItem.getPrice() / 1000;
                    break;
                }
                case UNIT: {
                    priceTotalDecrease = receiptItem.getAmount() * receiptItem.getPrice();
                    break;
                }
                default: throw new CashRegisterException(quote(
                        "Receipt item amount unit not supported",
                        receiptItem.getAmountUnit()));
            }
            receipt.setSumTotal(receipt.getSumTotal() - priceTotalDecrease);
            receipt = receiptRepository.save(receipt);

            // delete receipt item
            receiptItemRepository.delete(receiptItem);
            receipt.setItems(receipt.getItems().stream().filter((item) ->
                    !receiptItemId.equals(item.getId())).collect(Collectors.toList()));
        }
        return receipt;
    }

    @Override
    @Transactional
    public Receipt update(
            String receiptId, String receiptItemId, UpdateReceiptItemDto updateReceiptItemDto) {
        Optional<Receipt> receiptOpt = receiptRepository.findById(receiptId);
        if (receiptOpt.isEmpty()) {
            throw new CashRegisterException(
                    HttpStatus.NOT_FOUND, // 404 here, because receipt must be present
                    quote("Receipt not found", receiptId));
        }
        Receipt receipt = receiptOpt.get();

        // validate receipt status
        if (!"CREATED".equals(receipt.getStatus())) {
            throw new CashRegisterException(quote(
                    "Receipt status does not allow receipt item update",
                    receipt.getStatus()));
        }

        // find receipt item
        Optional<ReceiptItem> receiptItemOpt = receipt.getItems().stream().filter((receiptItem) ->
                receiptItemId.equals(receiptItem.getId())).findFirst();
        if (!receiptItemOpt.isPresent()) {
            throw new CashRegisterException(
                    HttpStatus.NOT_FOUND, // 404 here, because receipt item must be present
                    quote("Receipt item not found", receiptItemId));
        }
        ReceiptItem receiptItem = receiptItemOpt.get();

        // validate that receipt item amount does not exceed the product amount available
        if (updateReceiptItemDto.getAmount() > receiptItem.getProduct().getAmountAvailable()) {
            throw new CashRegisterException(quote(
                    "Receipt item amount exceeds product amount available",
                    receiptItem.getAmount()));
        }

        // determine amount diff
        int amountDiff = updateReceiptItemDto.getAmount() - receiptItem.getAmount();
        if (amountDiff == 0) {
            return receipt;
        }

        // change receipt price total
        int priceTotalDiff;
        switch (receiptItem.getAmountUnit()) {
            case GRAM: {
                priceTotalDiff = amountDiff * receiptItem.getPrice() / 1000;
                break;
            }
            case UNIT: {
                priceTotalDiff = amountDiff * receiptItem.getPrice();
                break;
            }
            default:
                throw new CashRegisterException(quote(
                        "Receipt item amount unit not supported",
                        receiptItem.getAmountUnit()));
        }
        receipt.setSumTotal(receipt.getSumTotal() + priceTotalDiff);

        // modify receipt item
        receiptItem.setAmount(updateReceiptItemDto.getAmount());

        return receiptRepository.save(receipt);
    }
}
