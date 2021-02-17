package com.polinakulyk.cashregister.service.api;

import com.polinakulyk.cashregister.service.api.dto.ShiftStatusSummaryResponseDto;
import com.polinakulyk.cashregister.db.dto.ShiftStatus;
import com.polinakulyk.cashregister.db.entity.Cashbox;

public interface CashboxService {
    Cashbox createWithId(String cashboxId, String name, ShiftStatus shiftStatus);
    ShiftStatusSummaryResponseDto activateShift();
    ShiftStatusSummaryResponseDto deactivateShift();
    ShiftStatusSummaryResponseDto getShiftStatusSummary();
}
