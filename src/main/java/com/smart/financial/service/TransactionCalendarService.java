package com.smart.financial.service;

import com.smart.financial.dao.TransactionCalendarDao;
import com.smart.financial.model.TransactionCalendarMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class TransactionCalendarService {

    @Autowired
    private TransactionCalendarDao transactionCalendarDao;

    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public void insert(List<TransactionCalendarMO> transactionCalendarMOList){
        transactionCalendarDao.insert(transactionCalendarMOList);
    }

    public TransactionCalendarMO getByDate(String date){
        return transactionCalendarDao.getByDate(date);
    }

    public boolean isTransactionDay(){
        final TransactionCalendarMO byDate = transactionCalendarDao.getByDate(dateFormat.format(new Date()));
        return byDate.getIsOpen() == 1;
    }
}
