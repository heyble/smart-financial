package com.smart.financial.task;

import com.smart.financial.calculation.MacdCalc;
import com.smart.financial.model.MacdMO;
import com.smart.financial.model.StockBaseMO;
import com.smart.financial.model.StockListMO;
import com.smart.financial.proxy.StockProxy;
import com.smart.financial.service.MacdDailyRecommendationService;
import com.smart.financial.service.MacdService;
import com.smart.financial.service.StockBaseService;
import com.smart.financial.service.StockListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
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
    private StockProxy stockProxy;
    private static final Logger LOGGER = LoggerFactory.getLogger(StockProxy.class);

    // @Scheduled(cron = "0/5 * * * * ?")
    public void hello(){
        System.out.println(new Date());
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

    @Scheduled(cron = "0 0 16 * * ?")
    public void crawlDailyDataToDb(){

        final List<StockListMO> stockList = stockListService.getAvailableList();

        int n = 0;

        for (StockListMO stockListMO : stockList) {
            try {
                // 如果是节假日就跳过,查询3次都是null判断为节假日
                StockBaseMO stockBaseMO = stockProxy.getStockBase(stockListMO.getTsCode());

                if (null == stockBaseMO) {
                     n++;
                     if (n>2){
                         LOGGER.info("今天是节假日");
                         break;
                     }
                     continue;
                }

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
    }

    @Scheduled(cron = "0 0 17 * * ?")
    public void analyzeMacd(){

        // System.out.println("macd analyze");
        final List<StockListMO> stockList = stockListService.getAvailableList();

        if (CollectionUtils.isEmpty(stockList)) {
            return;
        }

        try {
            // 如果是节假日就跳过
            for (int i = 0; i < 3; i++) {
                StockBaseMO stockBaseMO = stockProxy.getStockBase(stockList.get(i).getTsCode());
                if (null != stockBaseMO) {
                    break;
                }
                if (i == 2){
                    LOGGER.info("今天是节假日");
                    return;
                }
            }

            for (StockListMO stockListMO : stockList) {
                executor.execute(new AnalyzeMacdRunner(stockListMO,macdService,recommendationService));
            }
        } catch (Exception e) {
            LOGGER.error("每日推荐计算出错", e);
        }

    }
}
