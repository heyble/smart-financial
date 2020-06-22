package com.smart.financial.calculation;

import com.smart.financial.model.MacdMO;

import java.math.BigDecimal;

public class MacdCalc {
    private MacdMO pastDay;
    private BigDecimal price;
    private MacdMO macd;

    public MacdCalc(MacdMO pastDay, BigDecimal price) {
        this.pastDay = pastDay;
        this.price = price;
        this.macd = new MacdMO();
    }

    // MACD=（DIF-DEA）*2;
    public MacdMO calcMACD(int shortI,int longI,int mid){
        BigDecimal dif = calcDif(shortI,longI);
        BigDecimal dea = calcDea(mid,dif);
        // double macd = (dif-dea) * 2;
        macd.setDif(dif.toString());
        macd.setDea(dea.toString());
        macd.setMacd(dif.subtract(dea).multiply(new BigDecimal("2")).toString());
        return macd;
    }

    //（前一日DEA X 8/10+今日DIF X 2/10）
    private BigDecimal calcDea(int mid, BigDecimal dif) {
        // return pastDay.getDea() * (mid-1) / (mid+1) + dif * 2 / (mid+1);
        return new BigDecimal(pastDay.getDea()).multiply(new BigDecimal(mid-1+"")).divide(new BigDecimal(mid+1+""),BigDecimal.ROUND_HALF_UP)
                .add(dif.multiply(new BigDecimal("2")).divide(new BigDecimal(mid+1+""),BigDecimal.ROUND_HALF_UP));
    }

    // EMA（12）-EMA（26）
    private BigDecimal calcDif(int shortI, int longI) {
        final BigDecimal shortEma = calcEma(pastDay.getShortMea(), shortI);
        final BigDecimal longEma = calcEma(pastDay.getLongMea(), longI);
        macd.setShortMea(shortEma.toString());
        macd.setLongMea(longEma.toString());
        return shortEma.subtract(longEma);
    }

    // 11/(12+1) * 昨日EMA(12) + 2/(12+1) * 今日收盘价(12)
    // 前一日EMA（12）X 11/13+今日收盘价X2/13;
    private BigDecimal calcEma(String pastEma, int n) {
        final BigDecimal pastEmaD = new BigDecimal(pastEma);
        pastEmaD.setScale(2, BigDecimal.ROUND_HALF_UP);
        price.setScale(2, BigDecimal.ROUND_HALF_UP);
        return pastEmaD.multiply(new BigDecimal(n-1+"")).divide(new BigDecimal(n+1+""),BigDecimal.ROUND_HALF_UP).add(price.multiply(new BigDecimal("2")).divide(new BigDecimal(n+1+""),BigDecimal.ROUND_HALF_UP)) ;
    }
}