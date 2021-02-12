package com.polinakulyk.cashregister.exception.dto;

import java.util.StringJoiner;

public class ExceptionDto {

    private final String errorMessage;

    public ExceptionDto(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExceptionDto that = (ExceptionDto) o;

        return errorMessage.equals(that.errorMessage);
    }

    @Override
    public int hashCode() {
        return errorMessage.hashCode();
    }

    @Override
    public String toString() {
        return new StringJoiner(
                ", ", ExceptionDto.class.getSimpleName() + "[", "]")
                .add("errorMessage='" + errorMessage + "'")
                .toString();
    }
}
