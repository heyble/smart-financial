package com.smart.financial.dao;

import com.smart.financial.model.MacdDailyRecommendationMO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MacdDailyRecommendationDao {

    void insert(@Param("recommendationMOS") List<MacdDailyRecommendationMO> recommendationMOS);

    List<MacdDailyRecommendationMO> getByCondition(@Param("recommendationMO") MacdDailyRecommendationMO recommendationMO);

    List<MacdDailyRecommendationMO> getByDateDesc(@Param("type") Integer type, @Param("dateStr") String dateStr);
}
