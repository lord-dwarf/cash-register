package com.polinakulyk.cashregister.service;

import com.polinakulyk.cashregister.controller.dto.UpdateReceiptItemDto;
import com.polinakulyk.cashregister.db.dto.ShiftStatus;
import com.polinakulyk.cashregister.db.entity.Product;
import com.polinakulyk.cashregister.db.entity.Receipt;
import com.polinakulyk.cashregister.db.entity.ReceiptItem;
import com.polinakulyk.cashregister.db.entity.User;
import com.polinakulyk.cashregister.db.repository.ReceiptItemRepository;
import com.polinakulyk.cashregister.db.repository.ReceiptRepository;
import com.polinakulyk.cashregister.exception.CashRegisterException;
import com.polinakulyk.cashregister.service.api.ProductService;
import com.polinakulyk.cashregister.service.api.ReceiptService;
import com.polinakulyk.cashregister.service.api.UserService;
import com.polinakulyk.cashregister.util.CashRegisterUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.polinakulyk.cashregister.db.dto.ReceiptStatus.CANCELED;
import static com.polinakulyk.cashregister.db.dto.ReceiptStatus.COMPLETED;
import static com.polinakulyk.cashregister.db.dto.ReceiptStatus.CREATED;
import static com.polinakulyk.cashregister.service.ServiceHelper.calcCost;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.now;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.quote;

@Service
public class ReceiptServiceImpl implements ReceiptService {
    private static final Logger LOG = LoggerFactory.getLogger(ReceiptServiceImpl.class);

    private final ReceiptRepository receiptRepository;
    private final ReceiptItemRepository receiptItemRepository;
    private final ProductService productService;
    private final UserService userService;

    public ReceiptServiceImpl(
            ReceiptRepository receiptRepository,
            ReceiptItemRepository receiptItemRepository,
            ProductService productService,
            UserService userService
    ) {
        this.receiptRepository = receiptRepository;
        this.receiptItemRepository = receiptItemRepository;
        this.productService = productService;
        this.userService = userService;
    }

    @Override
    @Transactional
    public Iterable<Receipt> findAll() {
        return receiptRepository.findAll();
    }

    @Override
    public List<Receipt> findAllByTellerId(String tellerId) {
        // validate tellerId
        userService.findById(tellerId).orElseThrow(() ->
                new CashRegisterException(
                        HttpStatus.FORBIDDEN,
                        quote("User not found", tellerId)));

        // filter teller's receipts that belong to active shift
        List<Receipt> receipts = new ArrayList<>();
        receiptRepository.findAll().forEach((receipt) -> {
            if (tellerId.equals(receipt.getUser().getId())
                    && CashRegisterUtil.isReceiptInActiveShift(receipt)) {
                receipts.add(receipt);
            }
        });
        return receipts;
    }

    @Override
    @Transactional
    public Optional<Receipt> findById(String id) {
        return receiptRepository.findById(id);
    }

    @Override
    @Transactional
    public Receipt createReceipt(String userId) {
        User user = userService.findById(userId).orElseThrow(() ->
                new CashRegisterException(
                        HttpStatus.FORBIDDEN, quote("User not found", userId)));

        // cashbox shift must be active
        validateCashboxShiftActive(user);
        Receipt receipt = new Receipt()
                .setStatus(CREATED)
                .setCreatedTime(now())
                .setUser(user);
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

        // cashbox shift must be active
        validateCashboxShiftActive(receipt.getUser());

        // receipt shift must be active
        validateReceiptShiftActive(receipt);

        if (!(CREATED == receipt.getStatus() || COMPLETED == receipt.getStatus())) {
            throw new CashRegisterException(
                    quote("Receipt status transition not allowed", receipt.getStatus()));
        }
        // empty receipt can only be canceled
        if (receipt.getReceiptItems().isEmpty()) {
            throw new CashRegisterException(quote(
                    "Receipt without items cannot be completed", receipt.getStatus()));
        }
        if (COMPLETED == receipt.getStatus()) {
            return receipt;
        }

        // set receipt status COMPLETED and update checkout time
        receipt.setStatus(COMPLETED);
        receipt.setCheckoutTime(now());

        // decrease amount available for products in receipt
        for (ReceiptItem receiptItem : receipt.getReceiptItems()) {

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
            productService.update(receiptItemProduct);
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

        // cashbox shift must be active
        validateCashboxShiftActive(receipt.getUser());

        // receipt shift must be active
        validateReceiptShiftActive(receipt);

        // can cancel from all statuses
        if (CANCELED == receipt.getStatus()) {
            return receipt;
        }

        // set receipt status CANCELED
        receipt.setStatus(CANCELED);
        receipt.setCheckoutTime(now());

        // increase amount available for products in receipt
        for (ReceiptItem receiptItem : receipt.getReceiptItems()) {

            // increase amount available for product
            Product receiptItemProduct = receiptItem.getProduct();
            receiptItemProduct.setAmountAvailable(
                    receiptItemProduct.getAmountAvailable() + receiptItem.getAmount());
            productService.update(receiptItemProduct);
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

        // cashbox shift must be active
        validateCashboxShiftActive(receipt.getUser());

        // receipt shift must be active
        validateReceiptShiftActive(receipt);

        Optional<Product> productOpt = productService.findById(receiptItem.getProduct().getId());
        if (productOpt.isEmpty()) {
            throw new CashRegisterException(
                    quote("Product not found", receiptItem.getProduct().getId()));
        }
        Product product = productOpt.get();

        // validate receipt status
        if (CREATED != receipt.getStatus()) {
            throw new CashRegisterException(quote(
                    "Receipt status does not allow adding of item", receipt.getStatus()));
        }

        // save receipt item amount
        int receiptItemAmountAdded = receiptItem.getAmount();

        // find an existing receipt item
        Optional<ReceiptItem> existingReceiptItemOpt = receipt.getReceiptItems().stream()
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
            receipt.getReceiptItems().add(receiptItem);
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
        int priceTotalIncrease = calcCost(
                receiptItem.getPrice(), receiptItemAmountAdded, receiptItem.getAmountUnit());
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

        // cashbox shift must be active
        validateCashboxShiftActive(receipt.getUser());

        // receipt shift must be active
        validateReceiptShiftActive(receipt);

        // validate receipt status
        if (CREATED != receipt.getStatus()) {
            throw new CashRegisterException(quote(
                    "Receipt status does not allow receipt item cancellation",
                    receipt.getStatus()));
        }

        // find receipt item
        Optional<ReceiptItem> receiptItemOpt = receipt.getReceiptItems().stream().filter((receiptItem) ->
                receiptItemId.equals(receiptItem.getId())).findFirst();

        if (receiptItemOpt.isPresent()) {
            ReceiptItem receiptItem = receiptItemOpt.get();

            // decrease receipt price total
            int priceTotalDecrease = calcCost(
                    receiptItem.getPrice(), receiptItem.getAmount(), receiptItem.getAmountUnit());
            receipt.setSumTotal(receipt.getSumTotal() - priceTotalDecrease);
            receipt = receiptRepository.save(receipt);

            // delete receipt item
            receiptItemRepository.delete(receiptItem);
            receipt.setReceiptItems(receipt.getReceiptItems().stream().filter((item) ->
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

        // cashbox shift must be active
        validateCashboxShiftActive(receipt.getUser());

        // receipt shift must be active
        validateReceiptShiftActive(receipt);

        // validate receipt status
        if (CREATED != receipt.getStatus()) {
            throw new CashRegisterException(quote(
                    "Receipt status does not allow receipt item update",
                    receipt.getStatus()));
        }

        // find receipt item
        Optional<ReceiptItem> receiptItemOpt = receipt.getReceiptItems().stream().filter((receiptItem) ->
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
        int priceTotalDiff = calcCost(
                receiptItem.getPrice(), amountDiff, receiptItem.getAmountUnit());
        receipt.setSumTotal(receipt.getSumTotal() + priceTotalDiff);

        // modify receipt item
        receiptItem.setAmount(updateReceiptItemDto.getAmount());

        return receiptRepository.save(receipt);
    }

    private void validateCashboxShiftActive(User user) {
        if (ShiftStatus.ACTIVE != user.getCashbox().getShiftStatus()) {
            throw new CashRegisterException("User cashbox shift status must be active");
        }
    }

    private void validateReceiptShiftActive(Receipt receipt) {
        if (!CashRegisterUtil.isReceiptInActiveShift(receipt)) {
            throw new CashRegisterException("Receipt shift status must be active");
        }
    }
}
