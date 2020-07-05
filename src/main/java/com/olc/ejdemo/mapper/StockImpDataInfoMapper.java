package com.olc.ejdemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.olc.ejdemo.modle.StockImpDataInfo;
import org.springframework.stereotype.Repository;

@Repository
public interface StockImpDataInfoMapper extends BaseMapper<StockImpDataInfo> {
    int deleteByPrimaryKey(Long id);

    int insert(StockImpDataInfo record);

    int insertSelective(StockImpDataInfo record);

    StockImpDataInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(StockImpDataInfo record);

    int updateByPrimaryKey(StockImpDataInfo record);

}