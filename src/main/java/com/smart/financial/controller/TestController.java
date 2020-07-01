package com.smart.financial.controller;

import com.smart.financial.configuration.AppProperties;
import com.smart.financial.dao.StockListDao;
import com.smart.financial.dao.TransactionCalendarDao;
import com.smart.financial.model.StockListMO;
import com.smart.financial.model.TransactionCalendarMO;
import com.smart.financial.proxy.StockProxy;
import com.smart.financial.task.StockTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
public class TestController {

    @Autowired
    private StockProxy stockProxy;
    @Autowired
    private AppProperties properties;
    @Autowired
    private StockListDao stockListDao;
    @Autowired
    private StockTask task;
    @Autowired
    private TransactionCalendarDao transactionCalendarDao;

    @RequestMapping("/test")
    public String hello(){
        task.analyzeMacdWeek();
        return "OK";
    }

    @RequestMapping("/test1")
    public String hello1(){
        task.crawlDailyDataToDb();
        return "OK";
    }

    @RequestMapping("/test2")
    public String hello2(){
        task.analyzeMacd();
        return "OK";
    }
}
