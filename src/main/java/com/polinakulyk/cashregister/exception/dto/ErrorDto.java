package com.polinakulyk.cashregister.exception.dto;

import java.util.StringJoiner;

public class ErrorDto {

    private final String errorMessage;

    public ErrorDto(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ErrorDto that = (ErrorDto) o;

        return errorMessage.equals(that.errorMessage);
    }

    @Override
    public int hashCode() {
        return errorMessage.hashCode();
    }

    @Override
    public String toString() {
        return new StringJoiner(
                ", ", ErrorDto.class.getSimpleName() + "[", "]")
                .add("errorMessage='" + errorMessage + "'")
                .toString();
    }
}
