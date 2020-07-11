package com.smart.financial.service;

import com.smart.financial.dao.MacdDao;
import com.smart.financial.model.MacdMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MacdService {

    @Autowired
    private MacdDao macdDao;

    public void insert(List<MacdMO> macdMOList){
        macdDao.insert(macdMOList);
    }

    public MacdMO getOne(String tsCode, String  date){
        return macdDao.getOne(tsCode,date);
        // return null;
    }

    public List<MacdMO> getLastTen(String tsCode) {

        return  macdDao.getLastTen(tsCode);
    }
}
