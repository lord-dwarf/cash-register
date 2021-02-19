package com.polinakulyk.cashregister.service;

import com.polinakulyk.cashregister.db.dto.ShiftStatus;
import com.polinakulyk.cashregister.db.entity.Cashbox;
import com.polinakulyk.cashregister.db.entity.User;
import com.polinakulyk.cashregister.db.repository.CashboxRepository;
import com.polinakulyk.cashregister.exception.CashRegisterException;
import com.polinakulyk.cashregister.security.api.AuthHelper;
import com.polinakulyk.cashregister.service.api.CashboxService;
import com.polinakulyk.cashregister.service.api.UserService;
import com.polinakulyk.cashregister.service.api.dto.ShiftStatusSummaryResponseDto;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.polinakulyk.cashregister.db.dto.ShiftStatus.ACTIVE;
import static com.polinakulyk.cashregister.db.dto.ShiftStatus.INACTIVE;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.now;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.quote;
import static java.lang.String.format;

/**
 * Cashbox service.
 * <p>
 * The existence of {@link Cashbox} entity allows us to keep track of a current shift status.
 * Currently, application uses a single cash box, but can be extended to use multiple cash boxes.
 */
@Slf4j
@Service
public class CashboxServiceImpl implements CashboxService {
    private final CashboxRepository cashboxRepository;
    private final UserService userService;
    private final AuthHelper authHelper;

    public CashboxServiceImpl(
            CashboxRepository cashboxRepository,
            UserService userService,
            AuthHelper authHelper) {
        this.cashboxRepository = cashboxRepository;
        this.userService = userService;
        this.authHelper = authHelper;
    }

    /**
     * Creates cash box entity with the given id.
     * <p>
     * Currently, it is intended to be used only by
     * {@link com.polinakulyk.cashregister.config.autorun.InitDbAutorun}.
     *
     * @param cashboxId
     * @param name
     * @param shiftStatus
     * @return
     */
    @Override
    @Transactional
    public Cashbox createWithId(String cashboxId, String name, ShiftStatus shiftStatus) {

        // IMPORTANT here we assume that the provided cash box id is unique.
        return cashboxRepository.save(new Cashbox()
                .setId(cashboxId)
                .setName(name)
                .setShiftStatus(shiftStatus)
                .setShiftStatusTime(now()));
    }

    @Override
    @Transactional
    public ShiftStatusSummaryResponseDto activateShift() {
        String userId = authHelper.getUserId();
        log.debug("BEGIN Activate shift by user: '{}'", userId);

        User user = userService.findExistingById(userId);

        Cashbox cashbox = user.getCashbox();
        validateShiftStatusTransitionToActive(cashbox);

        var shiftStatusSummaryResponseDto =
                updateShiftStatus(cashbox, ACTIVE);

        log.info("DONE Activate shift by user: '{}', for cash box: '{}'",
                userId, cashbox.getId());
        return shiftStatusSummaryResponseDto;
    }

    @Override
    @Transactional
    public ShiftStatusSummaryResponseDto deactivateShift() {
        String userId = authHelper.getUserId();
        log.debug("BEGIN Deactivate shift by user: '{}'", userId);

        User user = userService.findExistingById(userId);

        Cashbox cashbox = user.getCashbox();
        validateShiftStatusTransitionToInactive(cashbox);

        var shiftStatusSummaryResponseDto =
                updateShiftStatus(cashbox, INACTIVE);

        log.info("DONE Deactivate shift by user: '{}', for cash box: '{}'",
                userId, cashbox.getId());
        return shiftStatusSummaryResponseDto;
    }

    /**
     * Provides shift status and elapsed time since last shift status change.
     *
     * @return
     */
    @Override
    @Transactional
    public ShiftStatusSummaryResponseDto getShiftStatusSummary() {
        String userId = authHelper.getUserId();
        User user = userService.findExistingById(userId);

        Cashbox cashbox = user.getCashbox();
        LocalDateTime shiftStatusTime = cashbox.getShiftStatusTime();
        var shiftStatusSummaryResponseDto =
                new ShiftStatusSummaryResponseDto()
                        .setShiftStatus(cashbox.getShiftStatus())
                        .setShiftStatusElapsedTime(calcElapsedTime(shiftStatusTime));

        log.debug("DONE Poll shift status for cash box: '{}', shift status: '{}'",
                cashbox.getId(), shiftStatusSummaryResponseDto.toString());
        return shiftStatusSummaryResponseDto;
    }

    /*
     * Calculates elapsed time since the given time.
     */
    private String calcElapsedTime(LocalDateTime from) {
        long seconds = ChronoUnit.SECONDS.between(from, now());
        long days = seconds / (3600 * 24);
        seconds %= 3600 * 24;
        long hours = seconds / 3600;
        seconds %= 3600;
        long minutes = seconds / 60;
        seconds %= 60;
        return format(
                "%s%02d:%02d:%02d",
                days != 0 ? days + ":" : "",
                hours,
                minutes,
                seconds
        );
    }

    private ShiftStatusSummaryResponseDto updateShiftStatus(Cashbox cashbox, ShiftStatus shiftStatus) {
        cashbox.setShiftStatus(shiftStatus);
        cashbox.setShiftStatusTime(now());
        cashbox = cashboxRepository.save(cashbox);

        LocalDateTime shiftStatusTime = cashbox.getShiftStatusTime();
        return new ShiftStatusSummaryResponseDto()
                .setShiftStatus(cashbox.getShiftStatus())
                .setShiftStatusElapsedTime(calcElapsedTime(shiftStatusTime));
    }

    private static void validateShiftStatusTransitionToActive(Cashbox cashbox) {
        ShiftStatus fromStatus = cashbox.getShiftStatus();
        if (INACTIVE != fromStatus) {
            throwOnIllegalShiftStatusTransition(fromStatus, ACTIVE);
        }
    }

    private static void validateShiftStatusTransitionToInactive(Cashbox cashbox) {
        ShiftStatus fromStatus = cashbox.getShiftStatus();
        if (ACTIVE != fromStatus) {
            throwOnIllegalShiftStatusTransition(fromStatus, INACTIVE);
        }
    }

    private static void throwOnIllegalShiftStatusTransition(
            ShiftStatus from, ShiftStatus to) {
        throw new CashRegisterException(quote(
                "Illegal shift status transition", from, to));
    }
}
