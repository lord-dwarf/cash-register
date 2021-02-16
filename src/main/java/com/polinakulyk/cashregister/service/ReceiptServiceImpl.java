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
import com.polinakulyk.cashregister.service.api.ProductService;
import com.polinakulyk.cashregister.service.api.ReceiptService;
import com.polinakulyk.cashregister.service.api.UserService;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.polinakulyk.cashregister.db.dto.ReceiptStatus.CANCELED;
import static com.polinakulyk.cashregister.db.dto.ReceiptStatus.COMPLETED;
import static com.polinakulyk.cashregister.db.dto.ReceiptStatus.CREATED;
import static com.polinakulyk.cashregister.db.dto.ShiftStatus.ACTIVE;
import static com.polinakulyk.cashregister.service.ServiceHelper.calcCostByPriceAndUnit;
import static com.polinakulyk.cashregister.service.ServiceHelper.isReceiptInActiveShift;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.now;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.quote;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

/**
 * Receipt service, which also includes receipt items logic.
 */
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
    @Transactional
    public List<Receipt> findAllByTellerId(String tellerId) {

        // validate that user exists
        userService.findExistingById(tellerId);

        // filter teller's receipts that belong to the active shift
        return stream(receiptRepository.findAll().spliterator(), false)
                .filter(r -> tellerId.equals(r.getUser().getId()) && isReceiptInActiveShift(r)).
                        collect(toList());
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
        return receiptRepository.findById(receiptId).orElseThrow(() ->
                new CashRegisterReceiptNotFoundException(receiptId));
    }

    @Override
    @Transactional
    public Receipt createReceipt(String userId) {
        User user = userService.findExistingById(userId);

        validateIsUserShiftActive(user);

        return receiptRepository.save(new Receipt()
                .setStatus(CREATED)
                .setCreatedTime(now())
                .setUser(user));
    }

    @Override
    @Transactional
    public Receipt completeReceipt(String receiptId) {
        Receipt receipt = findExistingById(receiptId);

        validateShiftStatus(receipt);
        validateReceiptStatusTransitionToCompleted(receipt);

        // decrease amount available for products in receipt items
        for (ReceiptItem receiptItem : receipt.getReceiptItems()) {

            validateProductAmountNotExceeded(receiptItem);

            // decrease amount available for product
            Product receiptItemProduct = receiptItem.getProduct();
            receiptItemProduct.setAmountAvailable(
                    receiptItemProduct.getAmountAvailable() - receiptItem.getAmount());
        }

        // set status
        receipt.setStatus(COMPLETED);
        receipt.setCheckoutTime(now());

        return receiptRepository.save(receipt);
    }

    @Override
    @Transactional
    public Receipt cancelReceipt(String receiptId) {
        Receipt receipt = findExistingById(receiptId);

        validateShiftStatus(receipt);
        validateReceiptStatusTransitionToCanceled(receipt);

        if (COMPLETED == receipt.getStatus()) {

            // increase amount available for products in receipt items
            for (ReceiptItem receiptItem : receipt.getReceiptItems()) {

                // increase amount available for product
                Product receiptItemProduct = receiptItem.getProduct();
                receiptItemProduct.setAmountAvailable(
                        receiptItemProduct.getAmountAvailable() + receiptItem.getAmount());
            }
        }

        // set status
        receipt.setStatus(CANCELED);
        receipt.setCheckoutTime(now());

        return receiptRepository.save(receipt);
    }

    @Override
    @Transactional
    public Receipt addReceiptItem(
            String receiptId, String receiptItemProductId, Integer receiptItemAmount) {
        Receipt receipt = findExistingById(receiptId);

        validateShiftStatus(receipt);
        validateIsReceiptItemsModificationAllowed(receipt);

        Product product = productService.findExistingById(receiptItemProductId);

        // find an already existing receipt item with the same product,
        // to prevent adding duplicate receipt items
        Optional<ReceiptItem> existingReceiptItemOpt = receipt.getReceiptItems().stream()
                .filter(ri -> product.getId().equals(ri.getProduct().getId()))
                .findFirst();

        ReceiptItem receiptItem = null;
        if (existingReceiptItemOpt.isPresent()) {

            // update existing receipt item
            receiptItem = existingReceiptItemOpt.get();
            receiptItem.setAmount(receiptItem.getAmount() + receiptItemAmount);
        } else {

            // create receipt item by copying some fields from product, then add to receipt
            receiptItem = new ReceiptItem()
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
        int sumTotalIncrease = calcCostByPriceAndUnit(
                receiptItem.getPrice(), receiptItemAmount, receiptItem.getAmountUnit());
        receipt.setSumTotal(receipt.getSumTotal() + sumTotalIncrease);

        // update receipt and associated receipt item
        return receiptRepository.save(receipt);
    }

    @Override
    @Transactional
    public Receipt cancelReceiptItem(String receiptId, String receiptItemId) {
        Receipt receipt = findExistingById(receiptId);

        validateShiftStatus(receipt);
        validateIsReceiptItemsModificationAllowed(receipt);

        ReceiptItem receiptItem = findExistingReceiptItemInReceipt(receipt, receiptItemId);

        // decrease receipt price total
        int sumTotalDecrease = calcCostByPriceAndUnit(
                receiptItem.getPrice(), receiptItem.getAmount(), receiptItem.getAmountUnit());
        receipt.setSumTotal(receipt.getSumTotal() - sumTotalDecrease);

        receiptItemRepository.delete(receiptItem);
        return receiptRepository.save(receipt);
    }

    @Override
    @Transactional
    public Receipt updateReceiptItemAmount(
            String receiptId, String receiptItemId, Integer newAmount) {
        Receipt receipt = findExistingById(receiptId);

        validateShiftStatus(receipt);
        validateIsReceiptItemsModificationAllowed(receipt);

        ReceiptItem receiptItem = findExistingReceiptItemInReceipt(receipt, receiptItemId);

        // determine amount diff
        int amountDiff = newAmount - receiptItem.getAmount();
        if (amountDiff == 0) {
            return receipt;
        }

        // modify receipt item then validate
        receiptItem.setAmount(newAmount);
        validateProductAmountNotExceeded(receiptItem);

        // change receipt price total
        int priceTotalDiff = calcCostByPriceAndUnit(
                receiptItem.getPrice(), amountDiff, receiptItem.getAmountUnit());
        receipt.setSumTotal(receipt.getSumTotal() + priceTotalDiff);

        // save to receipt must propagate to receipt item via cascade
        return receiptRepository.save(receipt);
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
        int receiptItemAmount = receiptItem.getAmount();
        int productAmountAvailable = receiptItem.getProduct().getAmountAvailable();

        // validate that receipt item amount does not exceed the product amount available
        if (receiptItemAmount > productAmountAvailable) {
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
