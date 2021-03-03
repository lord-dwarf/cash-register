package com.polinakulyk.cashregister.service;

import com.polinakulyk.cashregister.db.dto.ReceiptStatus;
import com.polinakulyk.cashregister.db.entity.Product;
import com.polinakulyk.cashregister.db.entity.Receipt;
import com.polinakulyk.cashregister.db.entity.ReceiptItem;
import com.polinakulyk.cashregister.db.entity.User;
import com.polinakulyk.cashregister.db.repository.ReceiptItemRepository;
import com.polinakulyk.cashregister.db.repository.ReceiptRepository;
import com.polinakulyk.cashregister.exception.CashRegisterException;
import com.polinakulyk.cashregister.exception.CashRegisterReceiptItemNotFoundException;
import com.polinakulyk.cashregister.exception.CashRegisterReceiptNotFoundException;
import com.polinakulyk.cashregister.security.api.AuthHelper;
import com.polinakulyk.cashregister.service.api.ProductService;
import com.polinakulyk.cashregister.service.api.ReceiptService;
import com.polinakulyk.cashregister.service.api.UserService;
import com.polinakulyk.cashregister.util.CashRegisterUtil;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.Util;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.polinakulyk.cashregister.db.dto.ReceiptStatus.CANCELED;
import static com.polinakulyk.cashregister.db.dto.ReceiptStatus.COMPLETED;
import static com.polinakulyk.cashregister.db.dto.ReceiptStatus.CREATED;
import static com.polinakulyk.cashregister.db.dto.ShiftStatus.ACTIVE;
import static com.polinakulyk.cashregister.service.ServiceHelper.calcCostByPriceAndAmount;
import static com.polinakulyk.cashregister.service.ServiceHelper.isReceiptInActiveShift;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.ZERO_MONEY;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.add;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.generateUuid;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.now;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.quote;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.subtract;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

/**
 * Receipt service, which also includes receipt items logic.
 */
@Slf4j
@Service
public class ReceiptServiceImpl implements ReceiptService {
    private final ReceiptRepository receiptRepository;
    private final ReceiptItemRepository receiptItemRepository;
    private final ProductService productService;
    private final UserService userService;
    private final AuthHelper authHelper;

    public ReceiptServiceImpl(
            ReceiptRepository receiptRepository,
            ReceiptItemRepository receiptItemRepository,
            ProductService productService,
            UserService userService,
            AuthHelper authHelper
    ) {
        this.receiptRepository = receiptRepository;
        this.receiptItemRepository = receiptItemRepository;
        this.productService = productService;
        this.userService = userService;
        this.authHelper = authHelper;
    }

    @Override
    @Transactional
    public List<Receipt> findAll() {
        var receipts = stream(receiptRepository.findAll().spliterator(), false)
                .collect(toList());

        log.debug("DONE Find receipts: {}", receipts.size());
        return receipts;
    }

    @Override
    @Transactional
    public List<Receipt> findAllByTellerId(String tellerId) {

        // filter teller's receipts that belong to the active shift
        var receipts = stream(receiptRepository.findAll().spliterator(), false)
                .filter(r -> tellerId.equals(r.getUser().getId()) && isReceiptInActiveShift(r))
                .collect(toList());

        log.debug("DONE Find receipts by teller: '{}', size: {}", tellerId, receipts.size());
        return receipts;
    }

    /**
     * Find the existing receipt by id, otherwise throw
     * {@link CashRegisterReceiptNotFoundException}.
     * <p>
     * Used as a way to retrieve the receipt entity that must be present. Otherwise the specific
     * exception is thrown, that will result in HTTP 404.
     *
     * @param receiptId
     * @return
     */
    @Override
    @Transactional
    public Receipt findExistingById(String receiptId) {
        var receipt = receiptRepository.findById(receiptId).orElseThrow(() ->
                new CashRegisterReceiptNotFoundException(receiptId));

        log.debug("DONE Find existing receipt: '{}'", receiptId);
        return receipt;
    }

    /**
     * Creates a new receipt.
     *
     * @param userId
     * @return
     */
    @Override
    @Transactional
    public Receipt createReceipt(String userId) {
        log.debug("BEGIN Create receipt by user: '{}'", userId);
        User user = userService.findExistingById(userId);

        validateIsUserShiftActive(user);

        var receipt = receiptRepository.save(new Receipt()
                .setStatus(CREATED)
                .setCreatedTime(now())
                .setUser(user)
                .setSumTotal(ZERO_MONEY)
        );

        log.info("DONE Create receipt by user: '{}', receipt: '{}'", userId, receipt.getId());
        return receipt;
    }

    /**
     * Completes a given receipt.
     *
     * Important: at this point amount of products available may change depending on receipt items.
     *
     * @param receiptId
     * @return
     */
    @Override
    @Transactional
    public Receipt completeReceipt(String receiptId) {
        var userId = authHelper.getUserId();
        log.debug("BEGIN Complete receipt by user: '{}', receipt: '{}'", userId, receiptId);

        Receipt receipt = findExistingById(receiptId);

        validateShiftStatus(receipt);
        validateReceiptStatusTransitionToCompleted(receipt);

        // decrease amount available for products in receipt items
        for (ReceiptItem receiptItem : receipt.getReceiptItems()) {

            validateProductAmountNotExceeded(receiptItem);

            // decrease amount available for product
            Product receiptItemProduct = receiptItem.getProduct();
            receiptItemProduct.setAmountAvailable(
                    subtract(receiptItemProduct.getAmountAvailable(), receiptItem.getAmount()));
        }

        // set status
        receipt.setStatus(COMPLETED);
        receipt.setCheckoutTime(now());

        receipt = receiptRepository.save(receipt);

        log.info("DONE Complete receipt by user: '{}', receipt: '{}'", userId, receiptId);
        return receipt;
    }

    /**
     * Cancels a given receipt.
     *
     * Important: at this point amount of products available may change depending on receipt items.
     *
     * @param receiptId
     * @return
     */
    @Override
    @Transactional
    public Receipt cancelReceipt(String receiptId) {
        var userId = authHelper.getUserId();
        log.debug("BEGIN Cancel receipt by user: '{}', receipt: '{}'", userId, receiptId);

        Receipt receipt = findExistingById(receiptId);

        validateShiftStatus(receipt);
        validateReceiptStatusTransitionToCanceled(receipt);

        if (COMPLETED == receipt.getStatus()) {

            // increase amount available for products in receipt items
            for (ReceiptItem receiptItem : receipt.getReceiptItems()) {

                // increase amount available for product
                Product receiptItemProduct = receiptItem.getProduct();
                receiptItemProduct.setAmountAvailable(
                        add(receiptItemProduct.getAmountAvailable(), receiptItem.getAmount()));
            }
        }

        // set status
        receipt.setStatus(CANCELED);
        receipt.setCheckoutTime(now());

        receipt = receiptRepository.save(receipt);

        log.info("DONE Cancel receipt by user: '{}', receipt: '{}'", userId, receiptId);
        return receipt;
    }

    /**
     * Add receipt item to a given receipt, based on item product details.
     *
     * @param receiptId
     * @param receiptItemProductId
     * @param receiptItemAmount
     * @return
     */
    @Override
    @Transactional
    public Receipt addReceiptItem(
            String receiptId, String receiptItemProductId, BigDecimal receiptItemAmount) {
        var userId = authHelper.getUserId();
        log.debug("BEGIN Add receipt item by user: '{}', in receipt: '{}', for product: '{}'",
                userId, receiptId, receiptItemProductId);

        Receipt receipt = findExistingById(receiptId);

        validateShiftStatus(receipt);
        validateIsReceiptItemsModificationAllowed(receipt);

        Product product = productService.findExistingById(receiptItemProductId);

        // find an already existing receipt item with the same product,
        // to prevent adding duplicate receipt items
        Optional<ReceiptItem> existingReceiptItemOpt = receipt.getReceiptItems().stream()
                .filter(ri -> product.getId().equals(ri.getProduct().getId()))
                .findFirst();

        ReceiptItem receiptItem;
        if (existingReceiptItemOpt.isPresent()) {

            // update existing receipt item
            receiptItem = existingReceiptItemOpt.get();
            receiptItem.setAmount(add(receiptItem.getAmount(), receiptItemAmount));
        } else {

            // create receipt item by copying some fields from product, then add to receipt
            receiptItem = new ReceiptItem()
                    .setId(generateUuid())
                    .setReceipt(receipt)
                    .setProduct(product)
                    .setName(product.getName())
                    .setAmount(receiptItemAmount)
                    .setAmountUnit(product.getAmountUnit())
                    .setPrice(product.getPrice());
            receipt.getReceiptItems().add(receiptItem);
        }

        // validate product amount at the stage after receipt item amount finalized
        validateProductAmountNotExceeded(receiptItem);

        // calculate and increase receipt price total
        BigDecimal sumTotalIncrease = calcCostByPriceAndAmount(
                receiptItem.getPrice(), receiptItemAmount);
        receipt.setSumTotal(add(receipt.getSumTotal(), sumTotalIncrease));

        // update receipt and associated receipt item
        receipt = receiptRepository.save(receipt);

        log.info("DONE Add receipt item by user: '{}', in receipt: '{}', receipt item: '{}'",
                userId, receiptId, receiptItem.getId());
        return receipt;
    }

    @Override
    @Transactional
    public Receipt cancelReceiptItem(String receiptId, String receiptItemId) {
        var userId = authHelper.getUserId();
        log.debug("BEGIN Cancel receipt item by user: '{}', in receipt: '{}', receipt item: '{}'",
                userId, receiptId, receiptItemId);

        Receipt receipt = findExistingById(receiptId);

        validateShiftStatus(receipt);
        validateIsReceiptItemsModificationAllowed(receipt);

        ReceiptItem receiptItem = findExistingReceiptItemInReceipt(receipt, receiptItemId);

        // decrease receipt price total
        BigDecimal sumTotalDecrease = calcCostByPriceAndAmount(
                receiptItem.getPrice(), receiptItem.getAmount());
        receipt.setSumTotal(subtract(receipt.getSumTotal(), sumTotalDecrease));

        receiptItemRepository.delete(receiptItem);
        receipt = receiptRepository.save(receipt);

        log.info("DONE Cancel receipt item by user: '{}', in receipt: '{}', receipt item: '{}'",
                userId, receiptId, receiptItemId);
        return receipt;
    }

    @Override
    @Transactional
    public Receipt updateReceiptItemAmount(
            String receiptId, String receiptItemId, BigDecimal newAmount) {
        var userId = authHelper.getUserId();
        log.debug("BEGIN Update receipt item amount by user: '{}', "
                        + "in receipt: '{}', receipt item: '{}', new amount: '{}'",
                userId, receiptId, receiptItemId, newAmount);

        Receipt receipt = findExistingById(receiptId);

        validateShiftStatus(receipt);
        validateIsReceiptItemsModificationAllowed(receipt);

        ReceiptItem receiptItem = findExistingReceiptItemInReceipt(receipt, receiptItemId);

        // determine amount diff
        BigDecimal amountDiff = subtract(newAmount, receiptItem.getAmount());
        if (amountDiff.compareTo(BigDecimal.ZERO) == 0) {

            log.info("DONE Update receipt item amount (no amount change) by user: '{}', "
                            + "in receipt: '{}', receipt item: '{}', new amount: '{}'",
                    userId, receiptId, receiptItemId, newAmount);
            return receipt;
        }

        // modify receipt item then validate
        receiptItem.setAmount(newAmount);
        validateProductAmountNotExceeded(receiptItem);

        // change receipt price total
        BigDecimal priceTotalDiff = calcCostByPriceAndAmount(
                receiptItem.getPrice(), amountDiff);
        receipt.setSumTotal(add(receipt.getSumTotal(), priceTotalDiff));

        // save to receipt must propagate to receipt item via cascade
        receipt = receiptRepository.save(receipt);

        log.info("DONE Update receipt item amount by user: '{}', "
                        + "in receipt: '{}', receipt item: '{}', new amount: '{}'",
                userId, receiptId, receiptItemId, receiptItem.getAmount());
        return receipt;
    }

    private ReceiptItem findExistingReceiptItemInReceipt(Receipt receipt, String receiptItemId) {
        return receipt.getReceiptItems().stream()
                .filter(ri -> receiptItemId.equals(ri.getId()))
                .findFirst()
                .orElseThrow(() -> new CashRegisterReceiptItemNotFoundException(receiptItemId));
    }

    private static void validateShiftStatus(Receipt receipt) {

        // user shift must be active, receipt must belong to an active shift
        validateIsUserShiftActive(receipt.getUser());
        validateIsReceiptInActiveShift(receipt);
    }

    private static void validateIsUserShiftActive(User user) {
        if (ACTIVE != user.getCashbox().getShiftStatus()) {
            throw new CashRegisterException("User shift status must be active");
        }
    }

    private static void validateIsReceiptInActiveShift(Receipt receipt) {
        if (!isReceiptInActiveShift(receipt)) {
            throw new CashRegisterException("Receipt must belong to an active shift");
        }
    }

    private static void validateReceiptStatusTransitionToCompleted(Receipt receipt) {
        ReceiptStatus fromStatus = receipt.getStatus();
        if (CREATED != fromStatus) {
            throwOnIllegalReceiptStatusTransition(fromStatus, CREATED);
        }
        if (receipt.getReceiptItems().isEmpty()) {
            throw new CashRegisterException("Receipt without items cannot be completed");
        }
    }

    private static void validateReceiptStatusTransitionToCanceled(Receipt receipt) {
        ReceiptStatus fromStatus = receipt.getStatus();
        if (CANCELED == fromStatus) {
            throwOnIllegalReceiptStatusTransition(fromStatus, CANCELED);
        }
    }

    private static void throwOnIllegalReceiptStatusTransition(
            ReceiptStatus from, ReceiptStatus to) {
        throw new CashRegisterException(quote(
                "Illegal receipt status transition", from, to));
    }

    private static void validateProductAmountNotExceeded(ReceiptItem receiptItem) {
        BigDecimal receiptItemAmount = receiptItem.getAmount();
        BigDecimal productAmountAvailable = receiptItem.getProduct().getAmountAvailable();

        // validate that receipt item amount does not exceed the product amount available
        if (receiptItemAmount.compareTo(productAmountAvailable) > 0) {
            throw new CashRegisterException(quote(
                    "Receipt item amount exceeds product amount available",
                    receiptItemAmount,
                    productAmountAvailable));
        }
    }

    private static void validateIsReceiptItemsModificationAllowed(Receipt receipt) {
        if (CREATED != receipt.getStatus()) {
            throw new CashRegisterException(quote(
                    "Receipt status does not allow modification of receipt items",
                    receipt.getStatus()));
        }
    }
}
