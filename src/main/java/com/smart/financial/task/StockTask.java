package com.smart.financial.task;

import com.smart.financial.model.StockListMO;
import com.smart.financial.service.MacdDailyRecommendationService;
import com.smart.financial.service.MacdService;
import com.smart.financial.service.StockListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

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

    // @Scheduled(cron = "0/5 * * * * ?")
    public void hello(){
        System.out.println(new Date());
    }

    // @Scheduled(cron = "0/8 * * * * ?")
    public void analyzeMacd(){
        // System.out.println("macd analyze");
        final List<StockListMO> stockList = stockListService.getAvailableList();

        if (CollectionUtils.isEmpty(stockList)) {
            return;
        }

        for (StockListMO stockListMO : stockList) {
            executor.execute(new AnalyzeMacdRunner(stockListMO,macdService,recommendationService));
        }

    }
}
