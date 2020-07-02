package com.smart.financial.controller;

import com.smart.financial.controller.vo.Response;
import com.smart.financial.model.MacdWeekRecommendationMO;
import com.smart.financial.service.MacdWeekRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/week")
public class MacdWeekRecommendationController {

    @Autowired
    private MacdWeekRecommendationService recommendationService;

    @RequestMapping("/recommendation")
    public Response<?> getRecommendation(@RequestParam String dateStr,@RequestParam(required = false) Integer type){
        final List<MacdWeekRecommendationMO> recommendationMOList = recommendationService.getByDateDesc(1, "");
        return new Response<>(200, "OK", recommendationMOList);
    }
}
