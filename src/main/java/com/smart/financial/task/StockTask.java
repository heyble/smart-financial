package com.smart.financial.task;

import com.smart.financial.analyzer.MacdAnalyzer;
import com.smart.financial.model.MacdDailyRecommendationMO;
import com.smart.financial.model.MacdMO;
import com.smart.financial.model.StockListMO;
import com.smart.financial.service.MacdDailyRecommendationService;
import com.smart.financial.service.MacdService;
import com.smart.financial.service.StockListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

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
            List<MacdMO> macdMOList = macdService.getLastTen(stockListMO.getTsCode());

            if (CollectionUtils.isEmpty(macdMOList)) {
                continue;
            }

            // 分析
            final MacdAnalyzer macdAnalyzer = new MacdAnalyzer();
            final MacdDailyRecommendationMO recommendationMO = macdAnalyzer.analyzeRecommendation(macdMOList);

            // 写入
            if (recommendationMO != null) {
                List<MacdDailyRecommendationMO> recommendationMOList = new ArrayList<MacdDailyRecommendationMO>();
                recommendationMOList.add(recommendationMO);
                recommendationService.insert(recommendationMOList);
            }
        }

    }
}
