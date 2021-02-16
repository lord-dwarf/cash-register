package com.polinakulyk.cashregister.controller.dto;

import java.util.StringJoiner;

public class FindProductsDto {

    private String filterValue;
    private ProductFilterKind filterKind;

    public String getFilterValue() {
        return filterValue;
    }

    public FindProductsDto setFilterValue(String filterValue) {
        this.filterValue = filterValue;
        return this;
    }

    public ProductFilterKind getFilterKind() {
        return filterKind;
    }

    public FindProductsDto setFilterKind(ProductFilterKind filterKind) {
        this.filterKind = filterKind;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FindProductsDto that = (FindProductsDto) o;

        if (!filterValue.equals(that.filterValue)) return false;
        return filterKind == that.filterKind;
    }

    @Override
    public int hashCode() {
        int result = filterValue.hashCode();
        result = 31 * result + filterKind.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner(
                ", ", FindProductsDto.class.getSimpleName() + "[", "]")
                .add("filterValue='" + filterValue + "'")
                .add("filterKind=" + filterKind)
                .toString();
    }
}
