package com.polinakulyk.cashregister.util;

import com.polinakulyk.cashregister.db.dto.ShiftStatus;
import com.polinakulyk.cashregister.db.entity.Cashbox;
import com.polinakulyk.cashregister.db.entity.Product;
import com.polinakulyk.cashregister.db.entity.Receipt;
import com.polinakulyk.cashregister.db.entity.ReceiptItem;
import com.polinakulyk.cashregister.db.entity.User;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import static com.polinakulyk.cashregister.db.dto.ShiftStatus.ACTIVE;

public class CashRegisterUtil {

    private static final String QUOTE = "'";

    private CashRegisterUtil() {
        throw new UnsupportedOperationException("Cannot instantiate");
    }

    public static String nonEmpty(String s) {
        return s != null && !s.isEmpty() ? s : null;
    }

    public static String quote(String message, Object value) {
        return "" + message + ": " + QUOTE + (value != null ? value.toString() : "null") + QUOTE;
    }

    public static String quote(String message, Object value1, Object value2) {
        return "" + message + ": " + QUOTE + (value1 != null ? value1.toString() : "null")
                + QUOTE + ", " + QUOTE + (value2 != null ? value2.toString() : "null") + QUOTE;
    }

    public static LocalDateTime now() {
        return LocalDateTime.now(Clock.systemUTC());
    }

    public static Date from(LocalDateTime localDateTime) {
        return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
    }

    public static LocalDateTime from(Date date) {
        return date.toInstant().atZone(ZoneId.of("Z")).toLocalDateTime();
    }

    public static String generateUuid() {
        return UUID.randomUUID().toString();
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
     * Provides relatively unique id for reports based on report created time.
     * <p>
     * The result is the number of minutes passed since 2021-01-01 00:00:00,
     * up to report created time. It is a positive ~6-7 digits number.
     *
     * @param reportCreatedTime
     * @return
     */
    public static String calcReportId(LocalDateTime reportCreatedTime) {
        return "" + ChronoUnit.MINUTES.between(
                now().withYear(2021).withMonth(1).truncatedTo(ChronoUnit.DAYS),
                reportCreatedTime);
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
