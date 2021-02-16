package com.polinakulyk.cashregister.controller.dto;

import com.polinakulyk.cashregister.db.dto.ShiftStatus;
import java.util.StringJoiner;

public class ShiftStatusSummaryDto {

    private ShiftStatus shiftStatus;
    private String shiftStatusElapsedTime;

    public ShiftStatus getShiftStatus() {
        return shiftStatus;
    }

    public ShiftStatusSummaryDto setShiftStatus(ShiftStatus shiftStatus) {
        this.shiftStatus = shiftStatus;
        return this;
    }

    public String getShiftStatusElapsedTime() {
        return shiftStatusElapsedTime;
    }

    public ShiftStatusSummaryDto setShiftStatusElapsedTime(String shiftStatusElapsedTime) {
        this.shiftStatusElapsedTime = shiftStatusElapsedTime;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShiftStatusSummaryDto that = (ShiftStatusSummaryDto) o;

        if (!shiftStatus.equals(that.shiftStatus)) return false;
        return shiftStatusElapsedTime.equals(that.shiftStatusElapsedTime);
    }

    @Override
    public int hashCode() {
        int result = shiftStatus.hashCode();
        result = 31 * result + shiftStatusElapsedTime.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner(
                ", ", ShiftStatusSummaryDto.class.getSimpleName() + "[", "]")
                .add("shiftStatus='" + shiftStatus + "'")
                .add("shiftStatusElapsedTime='" + shiftStatusElapsedTime + "'")
                .toString();
    }
}
