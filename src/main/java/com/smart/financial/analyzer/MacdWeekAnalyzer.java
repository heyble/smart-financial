package com.smart.financial.analyzer;

import com.smart.financial.model.MacdDailyRecommendationMO;
import com.smart.financial.model.MacdMO;
import com.smart.financial.model.MacdWeekRecommendationMO;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class MacdWeekAnalyzer implements RecommendationAnalyzer<MacdWeekRecommendationMO> {

    public MacdWeekRecommendationMO analyzeRecommendation(List<MacdMO> macdList){

        if (CollectionUtils.isEmpty(macdList) || macdList.size() < 5) {
            return null;
        }

        final MacdMO currentMacd = macdList.get(0);
        final MacdMO previousMacd = macdList.get(1);

        // macd由负转正
        // macd前三天为负
        // macd前五天为负
        // dif dea 为负
        // dif dea 为正
        if (Double.parseDouble(currentMacd.getMacd()) >= 0D && Double.parseDouble(previousMacd.getMacd()) < 0D) {
            return goldenHookAnalyze(macdList);
        }



        // macd为负，但是今天比昨天 >=
        // macd前三天均为 +负
        // dif dea 为负
        if (Double.parseDouble(currentMacd.getMacd()) >=  Double.parseDouble(previousMacd.getMacd())) {
            return bottomCopyAnalyze(macdList);
        }

        return null;
    }

    private MacdWeekRecommendationMO bottomCopyAnalyze(List<MacdMO> macdList) {
        int macdScore = 0;

        Double previousMacdVal = 0D;
        Double currentMacdVal;
        for (int i = 0; i < macdList.size(); i++) {
            if (i == macdList.size() - 1) {
                break;
            }
            currentMacdVal = Double.parseDouble(macdList.get(i).getMacd());
            previousMacdVal = Double.parseDouble(macdList.get(i+1).getMacd());
            if (currentMacdVal <= previousMacdVal && i < 8) {
                macdScore++;
            }
        }

        final MacdMO currentMacd = macdList.get(0);
        MacdWeekRecommendationMO recommendationMO = new MacdWeekRecommendationMO();
        recommendationMO.setIntersection(0);

        int difAndDeaScore = 3;
        if (Double.parseDouble(currentMacd.getMacd()) > 0D) {
            difAndDeaScore = 5;
            recommendationMO.setIntersection(1);
        }

        int attachScore = 0;
        if (PreferredStockList.getPreferred().contains(currentMacd.getTsCode().split("\\.")[0])) {
            attachScore = 5;
        }

        int totalScore = macdScore+difAndDeaScore+attachScore;

        if (totalScore < 9){
            return null;
        }

        recommendationMO.setDate(currentMacd.getDate());
        recommendationMO.setType(2);
        recommendationMO.setExponent(totalScore);
        recommendationMO.setTsCode(currentMacd.getTsCode());
        return recommendationMO;
    }

    private MacdWeekRecommendationMO goldenHookAnalyze(List<MacdMO> macdList) {

        MacdWeekRecommendationMO recommendationMO = new MacdWeekRecommendationMO();

        if (Double.parseDouble(macdList.get(1).getMacd()) > Double.parseDouble(macdList.get(2).getMacd())
                || Double.parseDouble(macdList.get(2).getMacd()) > Double.parseDouble(macdList.get(3).getMacd())) {
            return null;
        }

        int macdScore = 0;

        // macd前三天为负
        // macd前五天为负
        for (int i = 1; i < macdList.size(); i++) {
            final MacdMO macdMO = macdList.get(i);
            if (Double.parseDouble(macdMO.getMacd()) < 0D && i <= 8) {
                macdScore++ ;
            }
        }

        int difAndDeaScore = 0;
        // dif dea 为负
        // dif dea 为正
        final MacdMO currentMacd = macdList.get(0);
        if (Double.parseDouble(currentMacd.getDif()) > 0D) {
            difAndDeaScore = 5;
            recommendationMO.setIntersection(1);
        }else {
            difAndDeaScore = 3;
            recommendationMO.setIntersection(0);
        }

        int attachScore = 0;
        if (PreferredStockList.getPreferred().contains(currentMacd.getTsCode().split("\\.")[0])) {
            attachScore = 5;
        }

        int totalScore = macdScore+difAndDeaScore+attachScore;

        if (totalScore < 9){
            return null;
        }

        recommendationMO.setDate(currentMacd.getDate());
        recommendationMO.setType(1);
        recommendationMO.setExponent(totalScore);
        recommendationMO.setTsCode(currentMacd.getTsCode());
        return recommendationMO;
    }
}
