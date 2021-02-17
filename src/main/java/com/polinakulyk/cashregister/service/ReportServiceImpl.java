package com.polinakulyk.cashregister.service;

import com.polinakulyk.cashregister.service.api.ReportKind;
import com.polinakulyk.cashregister.service.api.dto.XZReportResponseDto;
import com.polinakulyk.cashregister.db.entity.Cashbox;
import com.polinakulyk.cashregister.db.entity.Receipt;
import com.polinakulyk.cashregister.db.entity.User;
import com.polinakulyk.cashregister.exception.CashRegisterException;
import com.polinakulyk.cashregister.security.api.AuthHelper;
import com.polinakulyk.cashregister.service.api.CashboxService;
import com.polinakulyk.cashregister.service.api.ReceiptService;
import com.polinakulyk.cashregister.service.api.ReportService;
import com.polinakulyk.cashregister.service.api.UserService;
import com.polinakulyk.cashregister.util.CashRegisterUtil;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import static java.util.stream.StreamSupport.stream;

/**
 * Report service.
 */
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
    public XZReportResponseDto createXZReport(ReportKind reportKind) {
        String userId = authHelper.getUserId();
        User user = userService.findExistingById(userId);

        Cashbox cashbox = user.getCashbox();
        LocalDateTime shiftStartTime = cashbox.getShiftStatusTime();
        LocalDateTime reportCreatedTime = now();
        List<Receipt> receiptsCompleted =
                stream(receiptService.findAll().spliterator(), false)
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

        return new XZReportResponseDto()
                .setReportId(calcXZReportId(reportCreatedTime))
                .setReportKind(reportKind)
                .setCashboxName(cashbox.getName())
                .setShiftStartTime(shiftStartTime)
                .setCreatedTime(reportCreatedTime)
                .setCreatedBy(user.getFullName())
                .setNumReceiptsCompleted(receiptsCompleted.size())
                .setSumTotal(sum);
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
