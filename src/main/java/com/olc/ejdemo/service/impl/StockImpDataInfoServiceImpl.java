package com.olc.ejdemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.olc.ejdemo.mapper.StockImpDataInfoMapper;
import com.olc.ejdemo.modle.StockImpDataInfo;
import com.olc.ejdemo.service.IStockImpDataInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Oulingcheng
 * @Date 2020/7/4
 */
@Service
public class StockImpDataInfoServiceImpl extends ServiceImpl<StockImpDataInfoMapper, StockImpDataInfo> implements IStockImpDataInfoService {

    @Autowired
    private StockImpDataInfoMapper stockImpDataInfoMapper;

    @Override
    public List<StockImpDataInfo> selectTheDayStock() {
        return stockImpDataInfoMapper.selectTheDayStock();
    }

}
