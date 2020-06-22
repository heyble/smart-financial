package com.smart.financial.dao;

import com.smart.financial.model.StockListMO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StockListDao {

    public List<StockListMO> getAll();

    void insert(@Param("stockListMOList") List<StockListMO> stockListMOList);

    void delete();

    List<StockListMO> getByCondition(@Param("stockListMO") StockListMO stockListMO);
}
