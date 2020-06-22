package com.smart.financial.controller;

import com.smart.financial.common.SmartException;
import com.smart.financial.controller.vo.Response;
import com.smart.financial.service.DataInitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/init")
public class DataInitController {

    @Autowired
    private DataInitService dataInitService;


    @RequestMapping("/stockList")
    public Response<?> initStockList() throws SmartException {
        dataInitService.initStockList();
        return new Response<>(200, "OK");
    }

    @RequestMapping("/stockBase")
    public Response<?> initStockBase() throws SmartException {
        dataInitService.initStockBase();
        return new Response<>(200, "OK");
    }

    @RequestMapping("/macd")
    public Response<?> initMacd(){
        dataInitService.initMacd();
        return new Response<>(200,"OK");
    }
}
