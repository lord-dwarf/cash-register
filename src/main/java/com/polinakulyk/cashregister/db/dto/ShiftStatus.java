package com.polinakulyk.cashregister.db.dto;

import com.polinakulyk.cashregister.security.dto.UserRole;
import java.util.Optional;

public enum ShiftStatus {
    ACTIVE,
    INACTIVE;

    /**
     * Finds the existing enum ShiftStatus value by a given string.
     *
     * @param shiftStatusStr
     * @return
     */
    public static Optional<ShiftStatus> fromString(String shiftStatusStr) {
        for (ShiftStatus shiftStatus : ShiftStatus.values()) {
            if (shiftStatus.toString().equals(shiftStatusStr)) {
                return Optional.of(shiftStatus);
            }
        }
        return Optional.empty();
    }
}
