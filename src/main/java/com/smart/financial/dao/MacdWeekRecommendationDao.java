package com.smart.financial.dao;

import com.smart.financial.model.MacdDailyRecommendationMO;
import com.smart.financial.model.MacdWeekRecommendationMO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MacdWeekRecommendationDao {

    void insert(@Param("recommendationMOS") List<MacdWeekRecommendationMO> recommendationMOS);

    List<MacdWeekRecommendationMO> getByCondition(@Param("recommendationMO") MacdWeekRecommendationMO recommendationMO);

    List<MacdWeekRecommendationMO> getByDateDesc(@Param("type") Integer type, @Param("dateStr") String dateStr);
}
