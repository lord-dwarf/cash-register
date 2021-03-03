package com.polinakulyk.cashregister.service;

import com.polinakulyk.cashregister.db.entity.Cashbox;
import com.polinakulyk.cashregister.db.entity.Receipt;
import com.polinakulyk.cashregister.db.entity.User;
import com.polinakulyk.cashregister.exception.CashRegisterException;
import com.polinakulyk.cashregister.security.api.AuthHelper;
import com.polinakulyk.cashregister.service.api.CashboxService;
import com.polinakulyk.cashregister.service.api.ReceiptService;
import com.polinakulyk.cashregister.service.api.ReportService;
import com.polinakulyk.cashregister.service.api.UserService;
import com.polinakulyk.cashregister.service.api.dto.ReportKind;
import com.polinakulyk.cashregister.service.api.dto.XZReportResponseDto;
import com.polinakulyk.cashregister.util.CashRegisterUtil;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.polinakulyk.cashregister.db.dto.ReceiptStatus.CANCELED;
import static com.polinakulyk.cashregister.db.dto.ReceiptStatus.COMPLETED;
import static com.polinakulyk.cashregister.service.ServiceHelper.calcReceiptCode;
import static com.polinakulyk.cashregister.service.ServiceHelper.calcXZReportId;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.add;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.now;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.quote;
import static java.util.stream.Collectors.toList;

/**
 * Report service that generates {@link ReportKind#X} and {@link ReportKind#Z} reports
 * for a {@link Cashbox}.
 */
@Slf4j
@Service
public class ReportServiceImpl implements ReportService {
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

    /**
     * Creates a specific report depending on a given {@link ReportKind}.
     *
     * @param reportKind
     * @return
     */
    @Override
    @Transactional
    public XZReportResponseDto createXZReport(ReportKind reportKind) {
        String userId = authHelper.getUserId();
        log.debug("BEGIN Create XZ report by user: '{}', of report kind: '{}'", userId, reportKind);

        User user = userService.findExistingById(userId);
        Cashbox cashbox = user.getCashbox();
        LocalDateTime shiftStartTime = cashbox.getShiftStatusTime();
        LocalDateTime reportCreatedTime = now();
        List<Receipt> receiptsCompleted =
                receiptService.findAll()
                        .stream()
                        .filter(ServiceHelper::isReceiptInActiveShift)
                        .filter(ReportServiceImpl::validateReceiptStatusForXZReport)
                        .filter(r -> COMPLETED == r.getStatus())
                        .collect(toList());

        BigDecimal sum = CashRegisterUtil.ZERO_MONEY;
        for (Receipt receipt : receiptsCompleted) {
            sum = add(sum, receipt.getSumTotal());
        }

        if (ReportKind.Z == reportKind) {
            cashboxService.deactivateShift();
        }

        var xzReportResponseDto = new XZReportResponseDto()
                .setReportId(calcXZReportId(reportCreatedTime))
                .setReportKind(reportKind)
                .setCashboxName(cashbox.getName())
                .setShiftStartTime(shiftStartTime)
                .setCreatedTime(reportCreatedTime)
                .setCreatedBy(user.getFullName())
                .setNumReceiptsCompleted(receiptsCompleted.size())
                .setSumTotal(sum);

        log.info("DONE Create XZ report by user: '{}', of report kind: '{}', sum total: '{}'",
                userId, reportKind, sum);
        return xzReportResponseDto;
    }

    private static boolean validateReceiptStatusForXZReport(Receipt receipt) {
        if (!List.of(COMPLETED, CANCELED).contains(receipt.getStatus())) {
            throw new CashRegisterException(quote(
                    "Receipt must be either completed or canceled",
                    calcReceiptCode(receipt.getId())));
        }
        return true;
    }
}
