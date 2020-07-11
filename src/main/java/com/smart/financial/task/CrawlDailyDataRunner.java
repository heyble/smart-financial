package com.smart.financial.task;

import com.smart.financial.analyzer.RecommendationAnalyzer;
import com.smart.financial.calculation.MacdCalc;
import com.smart.financial.common.DateUtil;
import com.smart.financial.model.MacdDailyRecommendationMO;
import com.smart.financial.model.MacdMO;
import com.smart.financial.model.StockBaseMO;
import com.smart.financial.model.StockListMO;
import com.smart.financial.model.TransactionCalendarMO;
import com.smart.financial.proxy.StockProxy;
import com.smart.financial.service.MacdDailyRecommendationService;
import com.smart.financial.service.MacdService;
import com.smart.financial.service.StockBaseService;
import com.smart.financial.service.TransactionCalendarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CrawlDailyDataRunner implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlDailyDataRunner.class);
    private StockListMO stockListMO;
    private StockProxy stockProxy;
    private StockBaseService stockBaseService;
    private MacdService macdService;
    private TransactionCalendarService transactionCalendarService;
    private MacdDailyRecommendationService recommendationService;
    private RecommendationAnalyzer<MacdDailyRecommendationMO> recommendationAnalyzer;
    private String transactionDateStr;

    public CrawlDailyDataRunner(StockListMO stockListMO,
                                     StockProxy stockProxy,
                                     StockBaseService stockBaseService,
                                     MacdService macdService,
                                     TransactionCalendarService transactionCalendarService,
                                     MacdDailyRecommendationService recommendationService,
                                     RecommendationAnalyzer<MacdDailyRecommendationMO> recommendationAnalyzer) {
        this.stockListMO = stockListMO;
        this.stockProxy = stockProxy;
        this.stockBaseService = stockBaseService;
        this.macdService = macdService;
        this.transactionCalendarService = transactionCalendarService;
        this.recommendationService = recommendationService;
        this.recommendationAnalyzer =  recommendationAnalyzer;
    }

    public CrawlDailyDataRunner(StockListMO stockListMO, StockProxy stockProxy, StockBaseService stockBaseService, MacdService macdService, TransactionCalendarService transactionCalendarService, MacdDailyRecommendationService recommendationService, RecommendationAnalyzer<MacdDailyRecommendationMO> recommendationAnalyzer, String transactionDateStr) {
        this.stockListMO = stockListMO;
        this.stockProxy = stockProxy;
        this.stockBaseService = stockBaseService;
        this.macdService = macdService;
        this.transactionCalendarService = transactionCalendarService;
        this.recommendationService = recommendationService;
        this.recommendationAnalyzer = recommendationAnalyzer;
        this.transactionDateStr = transactionDateStr;
    }

    @Override
    public void run() {
        try {
            StockBaseMO stockBaseMO = StringUtils.isEmpty(transactionDateStr)
                    ? stockProxy.getStockBase(stockListMO.getTsCode())
                    : stockProxy.getStockBaseByDate(stockListMO.getTsCode(), transactionDateStr);

            if (stockBaseMO == null) {
                return;
            }

            // 插入数据库
            List<StockBaseMO> stockBaseMOList = new ArrayList<>();
            stockBaseMOList.add(stockBaseMO);
            stockBaseService.insert(stockBaseMOList);

            // 计算macdX
            calcDailyMacd2Db(stockBaseMO);

            // macd分析
            analyzeMacd();
        } catch (Exception e) {
            LOGGER.error("爬取日线行情出错, tsCode:"+stockListMO.getTsCode(),e);
        }
    }

    public void analyzeMacd(){
        List<MacdMO> macdMOList = macdService.getLastTen(stockListMO.getTsCode());

        // 分析
        final MacdDailyRecommendationMO recommendationMO = recommendationAnalyzer.analyzeRecommendation(macdMOList);

        // 写入
        if (recommendationMO != null) {
            List<MacdDailyRecommendationMO> recommendationMOList = new ArrayList<MacdDailyRecommendationMO>();
            recommendationMOList.add(recommendationMO);
            recommendationService.insert(recommendationMOList);
        }
    }

    private void calcDailyMacd2Db(StockBaseMO stockBaseMO){
        final TransactionCalendarMO transactionDate = transactionCalendarService.getByDate(DateUtil.formatDateLine(stockBaseMO.getTradeDate()));
        final MacdMO pretradeMacd = macdService.getOne(stockBaseMO.getTsCode(), DateUtil.formatDateLine(transactionDate.getPretradeDate()));
        final MacdCalc macdCalc = new MacdCalc(pretradeMacd, BigDecimal.valueOf(stockBaseMO.getClose()));
        final MacdMO macdMO = macdCalc.calcMACD(12, 26, 9);
        List<MacdMO> insertMacdList = new ArrayList<>(1);
        macdMO.setTsCode(stockBaseMO.getTsCode());
        macdMO.setDate(stockBaseMO.getTradeDate());
        insertMacdList.add(macdMO);
        macdService.insert(insertMacdList);
    }
}
