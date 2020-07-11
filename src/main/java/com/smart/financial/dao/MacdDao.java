package com.smart.financial.dao;

import com.smart.financial.model.MacdMO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MacdDao {
    void insert(@Param("macdMOList") List<MacdMO> macdMOList);

    MacdMO getOne(@Param("tsCode")String tsCode, @Param("date")String date);

    List<MacdMO> getLastTen(@Param("tsCode") String tsCode);

    void delete(@Param("tsCode")String tsCode);
}
