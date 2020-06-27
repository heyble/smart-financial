package com.smart.financial.task;

import com.smart.financial.analyzer.MacdAnalyzer;
import com.smart.financial.model.MacdDailyRecommendationMO;
import com.smart.financial.model.MacdMO;
import com.smart.financial.model.StockListMO;
import com.smart.financial.service.MacdDailyRecommendationService;
import com.smart.financial.service.MacdService;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class AnalyzeMacdRunner implements Runnable {

    private StockListMO stockListMO;
    private MacdService macdService;
    private MacdDailyRecommendationService recommendationService;

    public AnalyzeMacdRunner(StockListMO stockListMO, MacdService macdService, MacdDailyRecommendationService recommendationService) {
        this.stockListMO = stockListMO;
        this.macdService = macdService;
        this.recommendationService = recommendationService;
    }

    @Override
    public void run() {
        List<MacdMO> macdMOList = macdService.getLastTen(stockListMO.getTsCode());

        if (CollectionUtils.isEmpty(macdMOList)) {
            return;
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
