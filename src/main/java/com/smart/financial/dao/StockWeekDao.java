package com.smart.financial.dao;

import com.smart.financial.model.StockWeekMO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StockWeekDao {
    void insert(@Param("stockWeekList") List<StockWeekMO> stockWeekList);

    List<StockWeekMO> getByTsCode(@Param("tsCode") String tsCode);
}
