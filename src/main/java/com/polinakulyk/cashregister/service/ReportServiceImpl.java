package com.polinakulyk.cashregister.service;

import com.polinakulyk.cashregister.controller.dto.ReportKind;
import com.polinakulyk.cashregister.controller.dto.XZReportDto;
import com.polinakulyk.cashregister.db.dto.ReceiptStatus;
import com.polinakulyk.cashregister.db.entity.Cashbox;
import com.polinakulyk.cashregister.db.entity.Receipt;
import com.polinakulyk.cashregister.db.entity.User;
import com.polinakulyk.cashregister.db.repository.ReceiptRepository;
import com.polinakulyk.cashregister.exception.CashRegisterException;
import com.polinakulyk.cashregister.security.api.AuthHelper;
import com.polinakulyk.cashregister.service.api.CashboxService;
import com.polinakulyk.cashregister.service.api.ReceiptService;
import com.polinakulyk.cashregister.service.api.ReportService;
import com.polinakulyk.cashregister.service.api.UserService;
import com.polinakulyk.cashregister.util.CashRegisterUtil;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.polinakulyk.cashregister.db.dto.ReceiptStatus.CANCELED;
import static com.polinakulyk.cashregister.db.dto.ReceiptStatus.COMPLETED;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.calcReceiptCode;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.calcReportId;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.now;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.quote;

@Service
public class ReportServiceImpl implements ReportService {
    private static final Logger LOG = LoggerFactory.getLogger(ReportServiceImpl.class);

    private final ReceiptService receiptService;
    private final AuthHelper authHelper;
    private final UserService userService;
    private final CashboxService cashboxService;

    public ReportServiceImpl(
            ReceiptService receiptService,
            AuthHelper authHelper,
            UserService userService,
            CashboxService cashboxService
    ) {
        this.receiptService = receiptService;
        this.authHelper = authHelper;
        this.userService = userService;
        this.cashboxService = cashboxService;
    }

    @Override
    @Transactional
    public XZReportDto createXZReport(ReportKind reportKind) {

        String userId = authHelper.getUserId();
        User user = userService.findById(userId).orElseThrow(() -> new CashRegisterException(
                HttpStatus.FORBIDDEN, quote("User not found", userId)));

        Cashbox cashbox = user.getCashbox();
        LocalDateTime shiftStartTime = cashbox.getShiftStatusTime();
        LocalDateTime reportCreatedTime = now();
        List<Receipt> receiptsCompleted = new ArrayList<>();
        List<Receipt> receiptsCanceled = new ArrayList<>();
        for (Receipt receipt : receiptService.findAll()) {
            if (CashRegisterUtil.isReceiptInActiveShift(receipt)) {
                ReceiptStatus status = receipt.getStatus();
                if (!(COMPLETED == status || CANCELED == status)) {
                    throw new CashRegisterException(quote(
                            "Receipt must be either completed or canceled",
                            calcReceiptCode(receipt.getId())));
                }
                if (COMPLETED == status) {
                    receiptsCompleted.add(receipt);
                } else if (CANCELED == status) {
                    receiptsCanceled.add(receipt);
                } else {
                    throw new UnsupportedOperationException("Should never happen");
                }
            }
        }

        int sum = 0;
        for (Receipt receipt : receiptsCompleted) {
            sum += receipt.getSumTotal();
        }

        if (ReportKind.Z == reportKind) {
            cashboxService.deactivateShift();
        }

        XZReportDto report = new XZReportDto()
                .setReportId(calcReportId(reportCreatedTime))
                .setReportKind(reportKind)
                .setCashboxName(cashbox.getName())
                .setShiftStartTime(shiftStartTime)
                .setCreatedTime(reportCreatedTime)
                .setCreatedBy(user.getFullName())
                .setNumReceiptsCompleted(receiptsCompleted.size())
                .setSumTotal(sum);

        return report;
    }


}
