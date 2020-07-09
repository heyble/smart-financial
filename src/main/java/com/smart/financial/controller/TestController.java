package com.smart.financial.controller;

import com.smart.financial.common.SmartException;
import com.smart.financial.configuration.AppProperties;
import com.smart.financial.dao.MacdDao;
import com.smart.financial.dao.StockListDao;
import com.smart.financial.dao.TransactionCalendarDao;
import com.smart.financial.proxy.StockProxy;
import com.smart.financial.task.SmartTaskExecutor;
import com.smart.financial.task.StockTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @Autowired
    private MacdDao macdDao;
    @Autowired
    private SmartTaskExecutor executor;
    private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);

    @RequestMapping("/test1")
    public String hello(){
        return "OK";
    }

    @RequestMapping("/test2")
    public String hello3(){
        return "OK";
    }
}
