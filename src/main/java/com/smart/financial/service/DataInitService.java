package com.smart.financial.service;

import com.smart.financial.calculation.MacdCalc;
import com.smart.financial.common.SmartException;
import com.smart.financial.dao.MacdDao;
import com.smart.financial.dao.MacdWeekDao;
import com.smart.financial.dao.StockBaseDao;
import com.smart.financial.dao.StockListDao;
import com.smart.financial.dao.StockWeekDao;
import com.smart.financial.dao.TransactionCalendarDao;
import com.smart.financial.model.MacdMO;
import com.smart.financial.model.StockBaseMO;
import com.smart.financial.model.StockListMO;
import com.smart.financial.model.StockWeekMO;
import com.smart.financial.model.TransactionCalendarMO;
import com.smart.financial.proxy.StockProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataInitService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataInitService.class);

    @Autowired
    private StockProxy stockProxy;

    @Autowired
    private StockListDao stockListDao;

    @Autowired
    private StockBaseDao stockBaseDao;

    @Autowired
    private StockWeekDao stockWeekDao;

    @Autowired
    private MacdDao macdDao;

    @Autowired
    private MacdWeekDao macdWeekDao;

    @Autowired
    private TransactionCalendarDao transactionCalendarDao;

    @Transactional
    public void initStockList() throws SmartException {
        final List<StockListMO> stockList = stockProxy.getStockList();
        stockListDao.delete();
        stockListDao.insert(stockList);
    }

    public void initTransactionCalendar() throws SmartException{
        List<TransactionCalendarMO> transactionCalendarMOS = stockProxy.getTransactionCalendar();
        transactionCalendarDao.insert(transactionCalendarMOS);
    }

    public void initStockBase() throws SmartException {
        final StockListMO condition = new StockListMO();
        condition.setListStatus("L");
        // condition.setTsCode("000001.SZ");
        List<StockListMO> stockList = stockListDao.getByCondition(condition);
        // 接口每分钟只能调500次
        long startTime = System.currentTimeMillis();
        long endTime = 0L;
        int n = 0;
        try {
            for (StockListMO stockListMO : stockList) {
                if (n == 500){
                    endTime = System.currentTimeMillis();
                    if (endTime-startTime<=60000){
                        Thread.sleep(endTime-startTime);
                        startTime = System.currentTimeMillis();
                        n = 0;
                    }
                }
                initStockBase(stockListMO);
                n++;
            }
        } catch (InterruptedException e) {
            throw new SmartException("中断异常",e);
        }
    }

    private void initStockBase(StockListMO stockListMO) {
        List<StockBaseMO> stockBaseList = null;
        try {
            stockBaseList = stockProxy.getStockBaseList(stockListMO.getTsCode());
        } catch (Exception e) {
            LOGGER.error("初始化initStockBase失败，stockProxy查询stockBaseList出错，tsCode:{}, message: {}", stockListMO.getTsCode(),e.getMessage());
        }
        if (stockBaseList == null || stockBaseList.isEmpty()) {
            LOGGER.warn("initStockBase中stockBaseList是空的");
            return;
        }
        stockBaseList.forEach(stock -> {
            stock.setTsCode(stockListMO.getTsCode());
            stock.setOpen(Double.valueOf(String.format("%.2f", stock.getOpen())));
            stock.setClose(Double.valueOf(String.format("%.2f", stock.getClose())));
            stock.setHigh(Double.valueOf(String.format("%.2f", stock.getHigh())));
            stock.setLow(Double.valueOf(String.format("%.2f", stock.getLow())));
        });
        stockBaseDao.insert(stockBaseList);
    }

    public void initMacd() {
        final StockListMO condition = new StockListMO();
        condition.setListStatus("L");
        // condition.setTsCode("000001.SZ");
        List<StockListMO> stockList = stockListDao.getByCondition(condition);
        for (StockListMO stockListMO : stockList) {
            initMacd(stockListMO);
        }
    }

    private void initMacd(StockListMO stockListMO) {
        List<StockBaseMO> stocks = stockBaseDao.getByTsCode(stockListMO.getTsCode());

        final List<MacdMO> macdMOS = calculationMacd(stockListMO, stocks);

        if (!CollectionUtils.isEmpty(macdMOS)) {
            macdDao.insert(macdMOS);
        }
    }

    private List<MacdMO> calculationMacd(StockListMO stockListMO, List<StockBaseMO> stocks) {
        if (CollectionUtils.isEmpty(stocks)) {
            LOGGER.warn("查询{}的stockBase为空",stockListMO.getTsCode());
            return new ArrayList<>();
        }
        List<MacdMO> macds = new ArrayList<>(stocks.size());
        MacdCalc macdCalc = new MacdCalc(buildInitMacdMO(), new BigDecimal(String.format("%.3f", stocks.get(stocks.size() - 1).getClose())));
        for (int i = stocks.size() - 1; i >= 0; i--) {
            final MacdMO macdMO = macdCalc.calcMACD(12, 26, 9);
            macdMO.setTsCode(stocks.get(i).getTsCode());
            macdMO.setDate(stocks.get(i).getTradeDate());
            macds.add(macdMO);
            if (i == 0) {
                break;
            }
            // 这里有个坑，一定要保证小数有三位，不然计算结果不够精确
            macdCalc = new MacdCalc(macdMO, new BigDecimal(String.format("%.3f", stocks.get(i-1).getClose())));
        }
        return macds;
    }

    private MacdMO buildInitMacdMO() {
        final MacdMO macd = new MacdMO();
        macd.setShortMea("1");
        macd.setLongMea("1");
        macd.setMacd("1");
        macd.setDea("1");
        macd.setDea("1");
        return macd;
    }

    public void initStockWeek() throws SmartException {
        final StockListMO condition = new StockListMO();
        condition.setListStatus("L");
        // condition.setTsCode("000001.SZ");
        List<StockListMO> stockList = stockListDao.getByCondition(condition);
        // 接口每分钟只能调500次
        long startTime = System.currentTimeMillis();
        long endTime = 0L;
        int n = 0;
        try {
            for (StockListMO stockListMO : stockList) {
                if (n == 500){
                    endTime = System.currentTimeMillis();
                    if (endTime-startTime<=60000){
                        Thread.sleep(endTime-startTime);
                        startTime = System.currentTimeMillis();
                        n = 0;
                    }
                }
                initStockWeek(stockListMO);
                n++;
            }
        } catch (InterruptedException e) {
            throw new SmartException("中断异常",e);
        }
    }

    private void initStockWeek(StockListMO stockListMO) {
        List<StockWeekMO> stockWeekMOList = null;
        try {
            stockWeekMOList = stockProxy.getStockWeekList(stockListMO.getTsCode());
        } catch (Exception e) {
            LOGGER.error("初始化initStockWeek失败，stockProxy查询stockWeekMOList出错，tsCode:{}, message: {}", stockListMO.getTsCode(),e.getMessage());
        }
        if (stockWeekMOList == null || stockWeekMOList.isEmpty()) {
            LOGGER.warn("initStockWeek中stockWeekMOList是空的");
            return;
        }
        stockWeekMOList.forEach(stock -> {
            stock.setTsCode(stockListMO.getTsCode());
            stock.setOpen(Double.valueOf(String.format("%.2f", stock.getOpen())));
            stock.setClose(Double.valueOf(String.format("%.2f", stock.getClose())));
            stock.setHigh(Double.valueOf(String.format("%.2f", stock.getHigh())));
            stock.setLow(Double.valueOf(String.format("%.2f", stock.getLow())));
        });
        stockWeekDao.insert(stockWeekMOList);
    }

    public void initMacdWeek() {
        final StockListMO condition = new StockListMO();
        condition.setListStatus("L");
        // condition.setTsCode("000001.SZ");
        List<StockListMO> stockList = stockListDao.getByCondition(condition);
        for (StockListMO stockListMO : stockList) {
            initMacdWeek(stockListMO);
        }
    }

    private void initMacdWeek(StockListMO stockListMO) {
        List<StockWeekMO> stocks = stockWeekDao.getByTsCode(stockListMO.getTsCode());

        final List<MacdMO> macdMOS = calculationMacdWeek(stockListMO, stocks);

        if (!CollectionUtils.isEmpty(macdMOS)) {
            macdWeekDao.insert(macdMOS);
        }
    }

    private List<MacdMO> calculationMacdWeek(StockListMO stockListMO, List<StockWeekMO> stocks) {
        if (CollectionUtils.isEmpty(stocks)) {
            LOGGER.warn("查询{}的stockBase为空",stockListMO.getTsCode());
            return new ArrayList<>();
        }
        List<MacdMO> macds = new ArrayList<>(stocks.size());
        MacdCalc macdCalc = new MacdCalc(buildInitMacdMO(), new BigDecimal(String.format("%.3f", stocks.get(stocks.size() - 1).getClose())));
        for (int i = stocks.size() - 1; i >= 0; i--) {
            final MacdMO macdMO = macdCalc.calcMACD(12, 26, 9);
            macdMO.setTsCode(stocks.get(i).getTsCode());
            macdMO.setDate(stocks.get(i).getTradeDate());
            macds.add(macdMO);
            if (i == 0) {
                break;
            }
            // 这里有个坑，一定要保证小数有三位，不然计算结果不够精确
            macdCalc = new MacdCalc(macdMO, new BigDecimal(String.format("%.3f", stocks.get(i-1).getClose())));
        }
        return macds;
    }
}
