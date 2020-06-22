package com.smart.financial.controller;

import com.smart.financial.configuration.AppProperties;
import com.smart.financial.dao.StockListDao;
import com.smart.financial.model.StockListMO;
import com.smart.financial.proxy.StockProxy;
import com.smart.financial.task.StockTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping("/test")
    public String hello(){
        // final List<StockListMO> stockList = stockListDao.getAll();
        // stockListDao.insert(stockList);
        // return stockList.get(0).getSymbol();

        // return stockProxy.post(properties.getStockListUrl());
        // return stockProxy.get(properties.getStockDetailUrl());
        task.analyzeMacd();
        return "OK";
    }
}
