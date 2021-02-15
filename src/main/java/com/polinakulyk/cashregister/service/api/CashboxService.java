package com.polinakulyk.cashregister.service.api;

import com.polinakulyk.cashregister.controller.dto.CashboxShiftStatusDto;
import com.polinakulyk.cashregister.db.entity.Cashbox;

public interface CashboxService {
    Cashbox createWithId(String cashboxId, String name, String shiftStatus);
    CashboxShiftStatusDto activateShift();
    CashboxShiftStatusDto deactivateShift();
    CashboxShiftStatusDto getShiftStatusAndElapsedTime();
}
