package com.smart.financial.service;

import com.smart.financial.dao.StockBaseDao;
import com.smart.financial.model.StockBaseMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockBaseService {

    @Autowired
    private StockBaseDao stockBaseDao;

    public void insert(List<StockBaseMO> stockBaseList){
        stockBaseDao.insert(stockBaseList);
    }

    public List<StockBaseMO> getByTsCode(String tsCode){
        return stockBaseDao.getByTsCode(tsCode);
    }
}
