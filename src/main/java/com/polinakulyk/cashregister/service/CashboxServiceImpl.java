package com.polinakulyk.cashregister.service;

import com.polinakulyk.cashregister.controller.dto.CashboxShiftStatusDto;
import com.polinakulyk.cashregister.db.entity.Cashbox;
import com.polinakulyk.cashregister.db.entity.User;
import com.polinakulyk.cashregister.db.repository.CashboxRepository;
import com.polinakulyk.cashregister.exception.CashRegisterException;
import com.polinakulyk.cashregister.security.api.AuthHelper;
import com.polinakulyk.cashregister.service.api.CashboxService;
import com.polinakulyk.cashregister.service.api.UserService;
import com.polinakulyk.cashregister.util.CashRegisterUtil;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.polinakulyk.cashregister.util.CashRegisterUtil.generateUuid;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.now;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.quote;

@Service
public class CashboxServiceImpl implements CashboxService {
    private static final Logger LOG = LoggerFactory.getLogger(CashboxServiceImpl.class);

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

    @Override
    @Transactional
    public Cashbox createWithId(String cashboxId, String name, String shiftStatus) {

        // here we assume that auto-generated cashbox UUID is unique
        return cashboxRepository.save(new Cashbox()
                .setId(cashboxId)
                .setName(name)
                .setShiftStatus(shiftStatus)
                .setShiftStatusTime(now()));
    }

    @Override
    @Transactional
    public CashboxShiftStatusDto activateShift() {
        String userId = authHelper.getUserId();
        User user = userService.findById(userId).orElseThrow(() ->
                new CashRegisterException(quote("User not found", userId)));
        Cashbox cashbox = user.getCashbox();
        String shiftStatus = cashbox.getShiftStatus();
        if ("INACTIVE".equals(shiftStatus)) {
            cashbox.setShiftStatus("ACTIVE");
            cashbox.setShiftStatusTime(now());
        }
        cashbox = cashboxRepository.save(cashbox);
        shiftStatus = cashbox.getShiftStatus();
        LocalDateTime shiftStatusTime = cashbox.getShiftStatusTime();
        return new CashboxShiftStatusDto()
                .setShiftStatus(shiftStatus)
                .setShiftStatusElapsedTime(calcShiftElapsedTime(shiftStatusTime));
    }

    @Override
    @Transactional
    public CashboxShiftStatusDto deactivateShift() {
        String userId = authHelper.getUserId();
        User user = userService.findById(userId).orElseThrow(() ->
                new CashRegisterException(quote("User not found", userId)));
        Cashbox cashbox = user.getCashbox();
        String shiftStatus = cashbox.getShiftStatus();
        if ("ACTIVE".equals(shiftStatus)) {
            cashbox.setShiftStatus("INACTIVE");
            cashbox.setShiftStatusTime(now());
        }
        cashbox = cashboxRepository.save(cashbox);
        shiftStatus = cashbox.getShiftStatus();
        LocalDateTime shiftStatusTime = cashbox.getShiftStatusTime();
        return new CashboxShiftStatusDto()
                .setShiftStatus(shiftStatus)
                .setShiftStatusElapsedTime(calcShiftElapsedTime(shiftStatusTime));
    }

    @Override
    @Transactional
    public CashboxShiftStatusDto getShiftStatusAndElapsedTime() {
        String userId = authHelper.getUserId();
        User user = userService.findById(userId).orElseThrow(() ->
                new CashRegisterException(quote("User not found", userId)));
        Cashbox cashbox = user.getCashbox();
        String shiftStatus = cashbox.getShiftStatus();
        LocalDateTime shiftStatusTime = cashbox.getShiftStatusTime();
        return new CashboxShiftStatusDto()
                .setShiftStatus(shiftStatus)
                .setShiftStatusElapsedTime(calcShiftElapsedTime(shiftStatusTime));
    }

    private String calcShiftElapsedTime(LocalDateTime shiftStatusTime) {
        long elapsedSeconds = ChronoUnit.SECONDS.between(shiftStatusTime, now());
        long days = elapsedSeconds / (3600 * 24);
        elapsedSeconds %= 3600 * 24;
        long hours = elapsedSeconds / 3600;
        elapsedSeconds %= 3600;
        long minutes = elapsedSeconds / 60;
        elapsedSeconds %= 60;
        String elapsedTime = ""
                + (days != 0 ? days + ":" : "")
                + (hours != 0 ? String.format("%02d", hours) : "00") + ":"
                + (minutes != 0 ? String.format("%02d", minutes) : "00") + ":"
                + String.format("%02d", elapsedSeconds);
        return elapsedTime;
    }
}
