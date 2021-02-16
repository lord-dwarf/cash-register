package com.polinakulyk.cashregister.service.api;

import com.polinakulyk.cashregister.controller.dto.ShiftStatusSummaryDto;
import com.polinakulyk.cashregister.db.dto.ShiftStatus;
import com.polinakulyk.cashregister.db.entity.Cashbox;

public interface CashboxService {
    Cashbox createWithId(String cashboxId, String name, ShiftStatus shiftStatus);
    ShiftStatusSummaryDto activateShift();
    ShiftStatusSummaryDto deactivateShift();
    ShiftStatusSummaryDto getShiftStatusSummary();
}
