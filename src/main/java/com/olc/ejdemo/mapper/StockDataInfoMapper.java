package com.olc.ejdemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.olc.ejdemo.modle.StockDataInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockDataInfoMapper extends BaseMapper<StockDataInfo> {
    int deleteByPrimaryKey(String id);

    int insert(StockDataInfo record);

    int insertSelective(StockDataInfo record);

    StockDataInfo selectByPrimaryKey(@Param("stockCode") String stockCode);

    int updateByPrimaryKeySelective(StockDataInfo record);

    int updateByPrimaryKeyWithBLOBs(StockDataInfo record);

    int updateByPrimaryKey(StockDataInfo record);

    int updateByCode(StockDataInfo stockDataInfo);

    List<StockDataInfo> selectImpData(@Param("begin") int begin,@Param("end") int end);
}