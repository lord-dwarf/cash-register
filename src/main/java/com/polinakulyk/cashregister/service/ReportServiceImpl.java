package com.polinakulyk.cashregister.service;

import com.polinakulyk.cashregister.controller.dto.XZReportDto;
import com.polinakulyk.cashregister.db.entity.Cashbox;
import com.polinakulyk.cashregister.db.entity.Product;
import com.polinakulyk.cashregister.db.entity.Receipt;
import com.polinakulyk.cashregister.db.entity.ReceiptItem;
import com.polinakulyk.cashregister.db.entity.User;
import com.polinakulyk.cashregister.db.repository.ProductRepository;
import com.polinakulyk.cashregister.db.repository.ReceiptRepository;
import com.polinakulyk.cashregister.exception.CashRegisterException;
import com.polinakulyk.cashregister.security.api.AuthHelper;
import com.polinakulyk.cashregister.service.api.CashboxService;
import com.polinakulyk.cashregister.service.api.ReportService;
import com.polinakulyk.cashregister.service.api.UserService;
import com.polinakulyk.cashregister.util.CashRegisterUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
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

    // we use repo, because we need an iterator that may potentially be lazy,
    // while services provide eager lists, instead of iterator
    private final ReceiptRepository receiptRepository;
    private final AuthHelper authHelper;
    private final UserService userService;
    private final CashboxService cashboxService;

    public ReportServiceImpl(
            ReceiptRepository receiptRepository,
            AuthHelper authHelper,
            UserService userService,
            CashboxService cashboxService
    ) {
        this.receiptRepository = receiptRepository;
        this.authHelper = authHelper;
        this.userService = userService;
        this.cashboxService = cashboxService;
    }

    @Override
    @Transactional
    public XZReportDto createXZReport(String reportKind) {

        String userId = authHelper.getUserId();
        User user = userService.findById(userId).orElseThrow(() -> new CashRegisterException(
                HttpStatus.FORBIDDEN, quote("User not found", userId)));

        Cashbox cashbox = user.getCashbox();
        LocalDateTime shiftStartTime = cashbox.getShiftStatusTime();
        LocalDateTime createdTime = now();
        List<Receipt> receiptsCompleted = new ArrayList<>();
        List<Receipt> receiptsCanceled = new ArrayList<>();
        for (Receipt receipt : receiptRepository.findAll()) {
            if ("ACTIVE".equals(receipt.getShiftStatus())) {
                String status = receipt.getStatus();
                if (!("COMPLETED".equals(status) || "CANCELED".equals(status))) {
                    throw new CashRegisterException(quote(
                            "Receipt must be either completed or canceled", receipt.getId()));
                }
                if (!(shiftStartTime.isBefore(receipt.getCreatedTime())
                                && shiftStartTime.isBefore(receipt.getCheckoutTime()))) {
                    throw new CashRegisterException(quote(
                            "Receipt must be modified after shift start",
                            receipt.getId()));
                }
                if (!(createdTime.isAfter(receipt.getCreatedTime())
                        && createdTime.isAfter(receipt.getCheckoutTime()))) {
                    throw new CashRegisterException(quote(
                            "Receipt must be modified before report create time",
                            receipt.getId()));
                }
                if ("COMPLETED".equals(status)) {
                    receiptsCompleted.add(receipt);
                } else if ("CANCELED".equals(status)) {
                    receiptsCanceled.add(receipt);
                } else {
                    throw new UnsupportedOperationException("Should never happen");
                }
            }
        }

        long minutesSince2021 = ChronoUnit.MINUTES.between(
                now().withYear(2021).withMonth(1).truncatedTo(ChronoUnit.DAYS),
                createdTime);

        int sum = 0;
        for (Receipt receipt : receiptsCompleted) {
            sum += receipt.getSumTotal();
        }

        if ("Z".equals(reportKind)) {
            for (Receipt receipt : receiptsCompleted) {
                receipt.setShiftStatus("INACTIVE");
                receiptRepository.save(receipt);
            }
            for (Receipt receipt : receiptsCanceled) {
                receipt.setShiftStatus("INACTIVE");
                receiptRepository.save(receipt);
            }
            cashboxService.deactivateShift();
        }

        XZReportDto report = new XZReportDto()
                .setReportId("" + minutesSince2021)
                .setReportKind(reportKind)
                .setCashboxName(cashbox.getName())
                .setShiftStartTime(shiftStartTime)
                .setCreatedTime(createdTime)
                .setCreatedBy(user.getFullName())
                .setNumReceiptsCompleted(receiptsCompleted.size())
                .setSumTotal(sum);

        return report;
    }
}
