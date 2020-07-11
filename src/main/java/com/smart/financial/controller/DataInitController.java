package com.smart.financial.controller;

import com.smart.financial.common.DateUtil;
import com.smart.financial.common.SmartException;
import com.smart.financial.controller.vo.Response;
import com.smart.financial.service.DataInitService;
import com.smart.financial.task.StockTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/init")
public class DataInitController {

    @Autowired
    private DataInitService dataInitService;
    @Autowired
    private StockTask stockTask;
    private boolean isInitBase = false;


    @RequestMapping("/stockList")
    public Response<?> initStockList() throws SmartException {
        // dataInitService.initStockList();
        return new Response<>(200, "OK");
    }

    @RequestMapping("/stockBase")
    public Response<?> initStockBase() throws SmartException {
        if (isInitBase) {
            throw new SmartException("已初始化，请勿重复操作");
        }
        dataInitService.initStockBase();
        isInitBase = true;
        return new Response<>(200, "OK");
    }

    @RequestMapping("/reStockBase")
    public Response<?> initReStockBase(@RequestParam String dateStr) throws SmartException {
        DateUtil.dateStr2DateWithException(dateStr);
        stockTask.reCrawDailyData2Db(dateStr);
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
