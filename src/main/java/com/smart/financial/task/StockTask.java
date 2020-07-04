package com.smart.financial.task;

import com.smart.financial.analyzer.MacdAnalyzer;
import com.smart.financial.analyzer.MacdWeekAnalyzer;
import com.smart.financial.calculation.MacdCalc;
import com.smart.financial.common.SmartException;
import com.smart.financial.model.MacdMO;
import com.smart.financial.model.StockBaseMO;
import com.smart.financial.model.StockListMO;
import com.smart.financial.model.StockWeekMO;
import com.smart.financial.model.TransactionCalendarMO;
import com.smart.financial.proxy.StockProxy;
import com.smart.financial.service.MacdDailyRecommendationService;
import com.smart.financial.service.MacdService;
import com.smart.financial.service.MacdWeekRecommendationService;
import com.smart.financial.service.MacdWeekService;
import com.smart.financial.service.StockBaseService;
import com.smart.financial.service.StockListService;
import com.smart.financial.service.StockWeekService;
import com.smart.financial.service.TransactionCalendarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class StockTask {

    @Autowired
    private MacdService macdService;
    @Autowired
    private StockListService stockListService;
    @Autowired
    private MacdDailyRecommendationService recommendationService;
    @Autowired
    private SmartTaskExecutor executor;
    @Autowired
    private StockBaseService stockBaseService;
    @Autowired
    private StockWeekService stockWeekService;
    @Autowired
    private StockProxy stockProxy;
    @Autowired
    private MacdWeekService macdWeekService;
    @Autowired
    private MacdWeekRecommendationService weekRecommendationService;
    @Autowired
    private TransactionCalendarService transactionCalendarService;
    private static final Logger LOGGER = LoggerFactory.getLogger(StockProxy.class);

    @Scheduled(cron = "0/5 * 22 * * ?")
    public void hello(){
        // System.out.println(new Date());
        LOGGER.info(new Date().toString());
    }

    private void calcDailyMacd2Db(StockBaseMO stockBaseMO){
        final List<MacdMO> macdMOList = macdService.getLastTen(stockBaseMO.getTsCode());
        if (CollectionUtils.isEmpty(macdMOList)) {
            return;
        }
        final MacdCalc macdCalc = new MacdCalc(macdMOList.get(0), BigDecimal.valueOf(stockBaseMO.getClose()));
        final MacdMO macdMO = macdCalc.calcMACD(12, 26, 9);
        List<MacdMO> insertMacdList = new ArrayList<>(1);
        macdMO.setTsCode(stockBaseMO.getTsCode());
        macdMO.setDate(stockBaseMO.getTradeDate());
        insertMacdList.add(macdMO);
        macdService.insert(insertMacdList);
    }

    // @Scheduled(cron = "0 30 22 * * ?")
    public void crawlDailyDataToDb(){

        LOGGER.info("开始爬取日线行情");

        final List<StockListMO> stockList = stockListService.getAvailableList();

        // 如果是节假日就跳过
        if (!transactionCalendarService.isTransactionDay()) {
            LOGGER.info("今天不是交易日");
            return;
        }

        for (StockListMO stockListMO : stockList) {
            try {
                StockBaseMO stockBaseMO = stockProxy.getStockBase(stockListMO.getTsCode());

                // 插入数据库
                List<StockBaseMO> stockBaseMOList = new ArrayList<>();
                stockBaseMOList.add(stockBaseMO);
                stockBaseService.insert(stockBaseMOList);

                // 计算macd
                calcDailyMacd2Db(stockBaseMO);
            } catch (Exception e) {
                LOGGER.error("爬取日线行情出错, tsCode:"+stockListMO.getTsCode(),e);
            }
        }
        LOGGER.info("结束爬取日线行情");
    }

    // @Scheduled(cron = "0 10 23 * * ?")
    public void analyzeMacd(){
        LOGGER.info("开始异步执行日推任务");
        final List<StockListMO> stockList = stockListService.getAvailableList();

        if (CollectionUtils.isEmpty(stockList)) {
            return;
        }

        // 如果是节假日就跳过
        if (!transactionCalendarService.isTransactionDay()) {
            LOGGER.info("今天不是交易日");
            return;
        }

        for (StockListMO stockListMO : stockList) {
            executor.execute(new AnalyzeMacdRunner(stockListMO,macdService,recommendationService,new MacdAnalyzer()));
        }
        LOGGER.info("结束异步执行日推任务");

    }


    // @Scheduled(cron = "0 0 02 * * ?")
    public void crawlWeekDataToDb(){
        LOGGER.info("开始爬取周线行情");

        // 非交易日的第一天才执行任务
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String format = df.format(new Date());
        final TransactionCalendarMO byDate = transactionCalendarService.getByDate(format);
        if (byDate.getIsOpen() == 1) {
            LOGGER.info("交易日");
            return;
        }

        final List<StockListMO> stockList = stockListService.getAvailableList();

        format = df.format(byDate.getPretradeDate());
        TransactionCalendarMO preDate = transactionCalendarService.getByDate(format);
        if (preDate.getIsOpen() == 1) {
            for (StockListMO stockListMO : stockList) {
                try {
                    StockBaseMO stockBaseMO = stockBaseService.getByTsCodeAndDate(stockListMO.getTsCode(),df.format(preDate.getCalDate()));

                    // 插入数据库
                    List<StockWeekMO> stockWeekMOS = new ArrayList<>();
                    StockWeekMO stockWeekMO = new StockWeekMO();
                    BeanUtils.copyProperties(stockBaseMO,stockWeekMO);
                    stockWeekMOS.add(stockWeekMO);
                    stockWeekService.insert(stockWeekMOS);

                    // 计算macd
                    calcWeekMacd2Db(stockWeekMO);
                } catch (Exception e) {
                    LOGGER.error("爬取周线行情出错, tsCode:"+stockListMO.getTsCode(),e);
                }
            }
            LOGGER.info("结束爬取周线行情");
        }
        LOGGER.info("非交易日");

    }


    // @Scheduled(cron = "0 0 04 * * ?")
    public void analyzeMacdWeek(){
        LOGGER.info("开始异步执行周线任务");

        // 非交易日的第一天才执行任务
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String format = df.format(new Date());
        final TransactionCalendarMO byDate = transactionCalendarService.getByDate(format);
        if (byDate.getIsOpen() == 1) {
            LOGGER.info("交易日");
            return;
        }

        final List<StockListMO> stockList = stockListService.getAvailableList();

        if (CollectionUtils.isEmpty(stockList)) {
            return;
        }

        format = df.format(byDate.getPretradeDate());
        TransactionCalendarMO preDate = transactionCalendarService.getByDate(format);
        if (preDate.getIsOpen() == 1) {
            for (StockListMO stockListMO : stockList) {
                executor.execute(new AnalyzeMacdWeekRunner(stockListMO,macdWeekService,weekRecommendationService,new MacdWeekAnalyzer()));
            }
            LOGGER.info("结束异步执行周线任务");
        }
        LOGGER.info("非交易日");
    }


    private void calcWeekMacd2Db(StockWeekMO stockWeekMO){
        final List<MacdMO> macdMOList = macdWeekService.getLastTen(stockWeekMO.getTsCode());
        if (CollectionUtils.isEmpty(macdMOList)) {
            return;
        }
        final MacdCalc macdCalc = new MacdCalc(macdMOList.get(0), BigDecimal.valueOf(stockWeekMO.getClose()));
        final MacdMO macdMO = macdCalc.calcMACD(12, 26, 9);
        List<MacdMO> insertMacdList = new ArrayList<>(1);
        macdMO.setTsCode(stockWeekMO.getTsCode());
        macdMO.setDate(stockWeekMO.getTradeDate());
        insertMacdList.add(macdMO);
        macdWeekService.insert(insertMacdList);
    }
}
