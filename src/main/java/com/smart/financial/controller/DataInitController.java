package com.smart.financial.controller;

import com.smart.financial.common.SmartException;
import com.smart.financial.controller.vo.Response;
import com.smart.financial.service.DataInitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/init")
public class DataInitController {

    @Autowired
    private DataInitService dataInitService;

    private static final Logger LOGGER = LoggerFactory.getLogger(DataInitController.class);


    @RequestMapping("/stockList")
    public Response<?> initStockList() throws SmartException {
        // dataInitService.initStockList();
        return new Response<>(200, "OK");
    }

    @RequestMapping("/stockBase")
    public Response<?> initStockBase() throws SmartException {
        dataInitService.initStockBase();
        return new Response<>(200, "OK");
    }

    @RequestMapping("/macd")
    public Response<?> initMacd(){
        // new Thread(()->{
        //     dataInitService.initMacd();
        // }).start();
        return new Response<>(200,"OK");
    }

    @RequestMapping("/stockWeek")
    public Response<?> initStockWeek() throws SmartException {
        // dataInitService.initStockWeek();
        return new Response<>(200, "OK");
    }

    @RequestMapping("/macdWeek")
    public Response<?> initMacdWeek(){
        // dataInitService.initMacdWeek();
        return new Response<>(200,"OK");
    }


    @RequestMapping("/transactionCalendar")
    public Response<?> initTransactionCalendar() throws SmartException {
        // dataInitService.initTransactionCalendar();
        return new Response<>(200,"OK");
    }
}
