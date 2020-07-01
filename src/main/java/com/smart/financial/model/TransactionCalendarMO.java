package com.smart.financial.model;

import java.util.Date;

public class TransactionCalendarMO {
    private String exchange;
    private Date calDate;
    private Integer isOpen;
    private Date pretradeDate;

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public Date getCalDate() {
        return calDate;
    }

    public void setCalDate(Date calDate) {
        this.calDate = calDate;
    }

    public Integer getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(Integer isOpen) {
        this.isOpen = isOpen;
    }

    public Date getPretradeDate() {
        return pretradeDate;
    }

    public void setPretradeDate(Date pretradeDate) {
        this.pretradeDate = pretradeDate;
    }
}
