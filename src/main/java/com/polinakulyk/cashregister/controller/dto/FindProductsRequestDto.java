package com.polinakulyk.cashregister.controller.dto;

import com.polinakulyk.cashregister.service.api.ProductFilterKind;
import java.util.StringJoiner;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class FindProductsRequestDto {

    @NotBlank(message = "Filter value cannot be blank")
    private String filterValue;

    @NotNull(message = "Filter kind cannot be null")
    private ProductFilterKind filterKind;

    public String getFilterValue() {
        return filterValue;
    }

    public FindProductsRequestDto setFilterValue(String filterValue) {
        this.filterValue = filterValue;
        return this;
    }

    public ProductFilterKind getFilterKind() {
        return filterKind;
    }

    public FindProductsRequestDto setFilterKind(ProductFilterKind filterKind) {
        this.filterKind = filterKind;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FindProductsRequestDto that = (FindProductsRequestDto) o;

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
                ", ", FindProductsRequestDto.class.getSimpleName() + "[", "]")
                .add("filterValue='" + filterValue + "'")
                .add("filterKind=" + filterKind)
                .toString();
    }
}
