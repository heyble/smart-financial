package com.smart.financial.service;

import com.smart.financial.dao.MacdDailyRecommendationDao;
import com.smart.financial.model.MacdDailyRecommendationMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MacdDailyRecommendationService {

    @Autowired
    private MacdDailyRecommendationDao macdDailyRecommendationDao;

    public void insert(List<MacdDailyRecommendationMO> recommendationMOS){
        macdDailyRecommendationDao.insert(recommendationMOS);
    }

    public List<MacdDailyRecommendationMO> getByCondition(MacdDailyRecommendationMO recommendationMO){
        return macdDailyRecommendationDao.getByCondition(recommendationMO);
    }

    public List<MacdDailyRecommendationMO> getByDateDesc(Integer type, String dateStr) {
        return macdDailyRecommendationDao.getByDateDesc(type,dateStr);
    }
}
