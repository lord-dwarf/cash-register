package com.polinakulyk.cashregister.controller.dto;

import com.polinakulyk.cashregister.db.dto.ProductAmountUnit;
import java.time.LocalDateTime;
import java.util.StringJoiner;

public class XZReportDto {
    private String reportId;
    private String reportKind;
    private String cashboxName;
    private String companyName = "Creamery";
    private LocalDateTime shiftStartTime;
    private LocalDateTime createdTime;
    private String createdBy;
    private Integer numReceiptsCompleted;
    private Integer sumTotal;

    public String getReportId() {
        return reportId;
    }

    public XZReportDto setReportId(String reportId) {
        this.reportId = reportId;
        return this;
    }

    public String getReportKind() {
        return reportKind;
    }

    public XZReportDto setReportKind(String reportKind) {
        this.reportKind = reportKind;
        return this;
    }

    public String getCashboxName() {
        return cashboxName;
    }

    public XZReportDto setCashboxName(String cashboxName) {
        this.cashboxName = cashboxName;
        return this;
    }

    public String getCompanyName() {
        return companyName;
    }

    public XZReportDto setCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public LocalDateTime getShiftStartTime() {
        return shiftStartTime;
    }

    public XZReportDto setShiftStartTime(LocalDateTime shiftStartTime) {
        this.shiftStartTime = shiftStartTime;
        return this;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public XZReportDto setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
        return this;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public XZReportDto setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public Integer getNumReceiptsCompleted() {
        return numReceiptsCompleted;
    }

    public XZReportDto setNumReceiptsCompleted(Integer numReceiptsCompleted) {
        this.numReceiptsCompleted = numReceiptsCompleted;
        return this;
    }

    public Integer getSumTotal() {
        return sumTotal;
    }

    public XZReportDto setSumTotal(Integer sumTotal) {
        this.sumTotal = sumTotal;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        XZReportDto that = (XZReportDto) o;

        if (!reportId.equals(that.reportId)) return false;
        if (!reportKind.equals(that.reportKind)) return false;
        if (!cashboxName.equals(that.cashboxName)) return false;
        if (!companyName.equals(that.companyName)) return false;
        if (!shiftStartTime.equals(that.shiftStartTime)) return false;
        if (!createdTime.equals(that.createdTime)) return false;
        if (!createdBy.equals(that.createdBy)) return false;
        if (!numReceiptsCompleted.equals(that.numReceiptsCompleted)) return false;
        return sumTotal.equals(that.sumTotal);
    }

    @Override
    public int hashCode() {
        int result = reportId.hashCode();
        result = 31 * result + reportKind.hashCode();
        result = 31 * result + cashboxName.hashCode();
        result = 31 * result + companyName.hashCode();
        result = 31 * result + shiftStartTime.hashCode();
        result = 31 * result + createdTime.hashCode();
        result = 31 * result + createdBy.hashCode();
        result = 31 * result + numReceiptsCompleted.hashCode();
        result = 31 * result + sumTotal.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner(
                ", ", XZReportDto.class.getSimpleName() + "[", "]")
                .add("reportId='" + reportId + "'")
                .add("reportKind='" + reportKind + "'")
                .add("cashboxName='" + cashboxName + "'")
                .add("companyName='" + companyName + "'")
                .add("shiftStartTime=" + shiftStartTime)
                .add("createdTime=" + createdTime)
                .add("createdBy='" + createdBy + "'")
                .add("numReceiptsCompleted=" + numReceiptsCompleted)
                .add("sumTotal=" + sumTotal)
                .toString();
    }
}
