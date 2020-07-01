package com.smart.financial.service;

import com.smart.financial.dao.StockWeekDao;
import com.smart.financial.model.StockWeekMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockWeekService {
    @Autowired
    private StockWeekDao stockWeekDao;

    public void insert( List<StockWeekMO> stockWeekList){
        stockWeekDao.insert(stockWeekList);
    }

    public List<StockWeekMO> getByTsCode( String tsCode){
        return stockWeekDao.getByTsCode(tsCode);
    }

}
