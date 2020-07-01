package com.smart.financial.dao;

import com.smart.financial.model.StockBaseMO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StockBaseDao {
    void insert(@Param("stockBaseList") List<StockBaseMO> stockBaseList);

    List<StockBaseMO> getByTsCode(@Param("tsCode") String tsCode);

    StockBaseMO getByTsCodeAndDate(@Param("tsCode")String tsCode,@Param("date") String date);
}
