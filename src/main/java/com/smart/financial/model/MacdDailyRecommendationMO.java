package com.smart.financial.model;

import java.util.Date;

public class MacdDailyRecommendationMO {

    private String tsCode;
    private Date date;
    private Integer type;
    private Integer intersection;
    private Integer exponent;

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getIntersection() {
        return intersection;
    }

    public void setIntersection(Integer intersection) {
        this.intersection = intersection;
    }

    public Integer getExponent() {
        return exponent;
    }

    public void setExponent(Integer exponent) {
        this.exponent = exponent;
    }
}
