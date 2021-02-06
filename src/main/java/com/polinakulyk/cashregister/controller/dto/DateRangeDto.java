package com.polinakulyk.cashregister.controller.dto;

import java.time.LocalDate;
import java.util.StringJoiner;

public class DateRangeDto {

    private LocalDate start;
    private LocalDate end;

    public LocalDate getStart() {
        return start;
    }

    public DateRangeDto setStart(LocalDate start) {
        this.start = start;
        return this;
    }

    public LocalDate getEnd() {
        return end;
    }

    public DateRangeDto setEnd(LocalDate end) {
        this.end = end;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DateRangeDto that = (DateRangeDto) o;

        if (start != null ? !start.equals(that.start) : that.start != null) return false;
        return end != null ? end.equals(that.end) : that.end == null;
    }

    @Override
    public int hashCode() {
        int result = start != null ? start.hashCode() : 0;
        result = 31 * result + (end != null ? end.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner(
                ", ", DateRangeDto.class.getSimpleName() + "[", "]")
                .add("start=" + start)
                .add("end=" + end)
                .toString();
    }
}
