package com.smart.financial.task;

import com.smart.financial.analyzer.MacdAnalyzer;
import com.smart.financial.analyzer.RecommendationAnalyzer;
import com.smart.financial.dao.MacdWeekDao;
import com.smart.financial.model.MacdDailyRecommendationMO;
import com.smart.financial.model.MacdMO;
import com.smart.financial.model.MacdWeekRecommendationMO;
import com.smart.financial.model.StockListMO;
import com.smart.financial.service.MacdDailyRecommendationService;
import com.smart.financial.service.MacdService;
import com.smart.financial.service.MacdWeekRecommendationService;
import com.smart.financial.service.MacdWeekService;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class AnalyzeMacdWeekRunner implements Runnable {

    private StockListMO stockListMO;
    private MacdWeekService macdService;
    private MacdWeekRecommendationService recommendationService;
    private RecommendationAnalyzer<MacdWeekRecommendationMO> recommendationAnalyzer;

    public AnalyzeMacdWeekRunner(StockListMO stockListMO, MacdWeekService macdService, MacdWeekRecommendationService recommendationService, RecommendationAnalyzer<MacdWeekRecommendationMO> recommendationAnalyzer) {
        this.stockListMO = stockListMO;
        this.macdService = macdService;
        this.recommendationService = recommendationService;
        this.recommendationAnalyzer = recommendationAnalyzer;
    }

    @Override
    public void run() {
        List<MacdMO> macdMOList = macdService.getLastTen(stockListMO.getTsCode());

        if (CollectionUtils.isEmpty(macdMOList)) {
            return;
        }

        // 分析
        final MacdWeekRecommendationMO recommendation = recommendationAnalyzer.analyzeRecommendation(macdMOList);

        // 写入
        if (recommendation != null) {
            List<MacdWeekRecommendationMO> recommendationMOList = new ArrayList<MacdWeekRecommendationMO>();
            recommendationMOList.add(recommendation);
            recommendationService.insert(recommendationMOList);
        }
    }
}
