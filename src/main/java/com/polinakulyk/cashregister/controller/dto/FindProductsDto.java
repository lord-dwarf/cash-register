package com.polinakulyk.cashregister.controller.dto;

import java.util.StringJoiner;

public class FindProductsDto {

    private String codeFilter;
    private String nameFilter;

    public String getCodeFilter() {
        return codeFilter;
    }

    public FindProductsDto setCodeFilter(String codeFilter) {
        this.codeFilter = codeFilter;
        return this;
    }

    public String getNameFilter() {
        return nameFilter;
    }

    public FindProductsDto setNameFilter(String nameFilter) {
        this.nameFilter = nameFilter;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FindProductsDto that = (FindProductsDto) o;

        if (codeFilter != null ? !codeFilter.equals(that.codeFilter) : that.codeFilter != null)
            return false;
        return nameFilter != null ? nameFilter.equals(that.nameFilter) : that.nameFilter == null;
    }

    @Override
    public int hashCode() {
        int result = codeFilter != null ? codeFilter.hashCode() : 0;
        result = 31 * result + (nameFilter != null ? nameFilter.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner(
                ", ", FindProductsDto.class.getSimpleName() + "[", "]")
                .add("codeFilter='" + codeFilter + "'")
                .add("nameFilter='" + nameFilter + "'")
                .toString();
    }
}
