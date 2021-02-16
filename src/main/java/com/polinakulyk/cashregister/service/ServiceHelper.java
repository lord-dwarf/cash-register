package com.polinakulyk.cashregister.service;

import com.polinakulyk.cashregister.db.dto.ProductAmountUnit;
import com.polinakulyk.cashregister.db.entity.Cashbox;
import com.polinakulyk.cashregister.db.entity.Product;
import com.polinakulyk.cashregister.db.entity.Receipt;
import com.polinakulyk.cashregister.db.entity.ReceiptItem;
import com.polinakulyk.cashregister.db.entity.User;
import com.polinakulyk.cashregister.exception.CashRegisterException;
import com.polinakulyk.cashregister.util.CashRegisterUtil;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static com.polinakulyk.cashregister.db.dto.ShiftStatus.ACTIVE;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.now;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.quote;

/**
 * Helper for service layer logic.
 */
public class ServiceHelper {

    private ServiceHelper() {
        throw new UnsupportedOperationException("Cannot instantiate");
    }

    public static int calcCostByPriceAndUnit(int price, int amount, ProductAmountUnit amountUnit) {
        switch (amountUnit) {
            case GRAM: return amount * price / 1000;
            case UNIT: return amount * price;
            default: throw new CashRegisterException(quote(
                    "Amount unit not supported", amountUnit));
        }
    }

    /**
     * Create user-friendly (4 chars) but still relatively unique id for receipt,
     * based on UUID id of receipt
     *
     * @param receiptIdUuid
     * @return
     */
    public static String calcReceiptCode(String receiptIdUuid) {
        return receiptIdUuid.substring(32);
    }

    /**
     * Provides relatively unique id for X and Z reports based on report created time.
     * <p>
     * The result is the number of minutes passed since 2021-01-01 00:00:00,
     * up to report created time. The result is a positive ~6-7 digits string.
     * Newly generated ids are guaranteed to be no less than previously generated ones.
     *
     * @param reportCreatedTime
     * @return
     */
    public static String calcXZReportId(LocalDateTime reportCreatedTime) {
        LocalDateTime beginOf2021 = now().withYear(2021).withMonth(1).truncatedTo(ChronoUnit.DAYS);
        return "" + ChronoUnit.MINUTES.between(beginOf2021, reportCreatedTime);
    }

    /**
     * Determines whether the given receipt belongs to a currently active shift of a
     * receipt user's cash box.
     *
     * @param receipt
     * @return
     */
    public static boolean isReceiptInActiveShift(Receipt receipt) {
        Cashbox cashbox = receipt.getUser().getCashbox();
        LocalDateTime receiptCreatedTime = receipt.getCreatedTime();
        LocalDateTime shiftStartTime = cashbox.getShiftStatusTime();

        return ACTIVE == cashbox.getShiftStatus() && (
                receiptCreatedTime.isAfter(shiftStartTime)
                        || receiptCreatedTime.isEqual(shiftStartTime));
    }

    // TODO consider replacing all strip() methods usage, with explicit loading of related entities
    public static Product strip(Product product) {
        return product.setReceiptItems(null);
    }

    public static Receipt strip(Receipt receipt) {
        for (ReceiptItem receiptItem : receipt.getReceiptItems()) {
            strip(receiptItem);
        }
        strip(receipt.getUser());
        return receipt;
    }

    public static ReceiptItem strip(ReceiptItem receiptItem) {
        receiptItem.setReceipt(null);
        strip(receiptItem.getProduct());
        return receiptItem;
    }

    public static User strip(User user) {
        return user
                .setReceipts(null)
                .setCashbox(strip(user.getCashbox()));
    }

    public static Cashbox strip(Cashbox cashbox) {
        return cashbox.setUsers(null);
    }
}
