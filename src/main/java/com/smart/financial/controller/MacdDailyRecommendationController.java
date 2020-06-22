package com.smart.financial.controller;

import com.smart.financial.common.SmartException;
import com.smart.financial.controller.vo.Response;
import com.smart.financial.model.MacdDailyRecommendationMO;
import com.smart.financial.service.MacdDailyRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/daily")
public class MacdDailyRecommendationController {

    @Autowired
    private MacdDailyRecommendationService recommendationService;

    @RequestMapping("/recommendation")
    public Response<?> getRecommendation(@RequestParam(required = false) Integer type,@RequestParam String dateStr) throws SmartException {

        List<MacdDailyRecommendationMO> recommendationMOList = null;
        try {
            DateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");
            Date date = dateFormat.parse(dateStr);
            recommendationMOList = recommendationService.getByDateDesc(type,dateStr);
        } catch (ParseException e) {
            throw new SmartException("时间格式不正确");
        }
        return new Response<>(200, "ok", recommendationMOList);
    }
}
