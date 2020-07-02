package com.smart.financial.model;

import java.util.Date;
import java.util.Objects;

public class MacdMO {
    private String tsCode;
    private Date date;
    private String dif;
    private String dea;
    private String macd;
    private String shortMea;
    private String longMea;

    public String getTsCode() {
        return tsCode;
    }

    public void setTsCode(String tsCode) {
        this.tsCode = tsCode;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDif() {
        return dif;
    }

    public void setDif(String dif) {
        this.dif = dif;
    }

    public String getDea() {
        return dea;
    }

    public void setDea(String dea) {
        this.dea = dea;
    }

    public String getMacd() {
        return macd;
    }

    public void setMacd(String macd) {
        this.macd = macd;
    }

    public String getShortMea() {
        return shortMea;
    }

    public void setShortMea(String shortMea) {
        this.shortMea = shortMea;
    }

    public String getLongMea() {
        return longMea;
    }

    public void setLongMea(String longMea) {
        this.longMea = longMea;
    }

    @Override
    public String toString() {
        return "MacdMO{" +
                "tsCode='" + tsCode + '\'' +
                ", date=" + date +
                ", dif='" + dif + '\'' +
                ", dea='" + dea + '\'' +
                ", macd='" + macd + '\'' +
                ", shortMea='" + shortMea + '\'' +
                ", longMea='" + longMea + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MacdMO macdMO = (MacdMO) o;
        return Objects.equals(tsCode, macdMO.tsCode) &&
                Objects.equals(date, macdMO.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tsCode, date);
    }
}
