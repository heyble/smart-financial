package com.smart.financial.service;

import com.smart.financial.dao.StockListDao;
import com.smart.financial.model.StockListMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockListService {

    @Autowired
    private StockListDao stockListDao;

    public List<StockListMO> getAll(){
        return stockListDao.getAll();
    }

    public List<StockListMO> getAvailableList(){
        final StockListMO condition = new StockListMO();
        condition.setListStatus("L");
        return stockListDao.getByCondition(condition);
    }
}
