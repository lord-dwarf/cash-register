package com.polinakulyk.cashregister.controller.dto;

import java.util.StringJoiner;

public class CashboxShiftStatusDto {

    private String shiftStatus;
    private String shiftStatusElapsedTime;

    public String getShiftStatus() {
        return shiftStatus;
    }

    public CashboxShiftStatusDto setShiftStatus(String shiftStatus) {
        this.shiftStatus = shiftStatus;
        return this;
    }

    public String getShiftStatusElapsedTime() {
        return shiftStatusElapsedTime;
    }

    public CashboxShiftStatusDto setShiftStatusElapsedTime(String shiftStatusElapsedTime) {
        this.shiftStatusElapsedTime = shiftStatusElapsedTime;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CashboxShiftStatusDto that = (CashboxShiftStatusDto) o;

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
                ", ", CashboxShiftStatusDto.class.getSimpleName() + "[", "]")
                .add("shiftStatus='" + shiftStatus + "'")
                .add("shiftStatusElapsedTime='" + shiftStatusElapsedTime + "'")
                .toString();
    }
}
