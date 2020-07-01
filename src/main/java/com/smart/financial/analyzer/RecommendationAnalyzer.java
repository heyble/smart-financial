package com.smart.financial.analyzer;

import com.smart.financial.model.MacdMO;

import java.util.List;

public interface RecommendationAnalyzer<T> {

    T analyzeRecommendation(List<MacdMO> macdList);
}
