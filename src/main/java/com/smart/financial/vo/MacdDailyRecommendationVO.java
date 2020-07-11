package com.smart.financial.vo;

import com.smart.financial.model.MacdWeekRecommendationMO;

public class MacdDailyRecommendationVO extends MacdWeekRecommendationMO {
    private String name;
    private String industry;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }
}
