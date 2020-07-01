package com.smart.financial.service;

import com.smart.financial.dao.MacdWeekRecommendationDao;
import com.smart.financial.model.MacdDailyRecommendationMO;
import com.smart.financial.model.MacdWeekRecommendationMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MacdWeekRecommendationService {

    @Autowired
    private MacdWeekRecommendationDao macdWeekRecommendationDao;

    public void insert(List<MacdWeekRecommendationMO> recommendationMOS){
        macdWeekRecommendationDao.insert(recommendationMOS);
    }

    public List<MacdWeekRecommendationMO> getByCondition(MacdWeekRecommendationMO recommendationMO){
        return macdWeekRecommendationDao.getByCondition(recommendationMO);
    }

    public List<MacdWeekRecommendationMO> getByDateDesc(Integer type, String dateStr) {
        return macdWeekRecommendationDao.getByDateDesc(type,dateStr);
    }
}
