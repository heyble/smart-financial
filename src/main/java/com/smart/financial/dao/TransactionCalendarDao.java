package com.smart.financial.dao;

import com.smart.financial.model.TransactionCalendarMO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface TransactionCalendarDao {
    void insert(@Param("transactionCalendarList") List<TransactionCalendarMO> transactionCalendarList);

    TransactionCalendarMO getByDate(@Param("date") String date);
}
