package com.olc.ejdemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.olc.ejdemo.modle.StockImpDataInfo;

import java.util.List;

/**
 * @author Oulingcheng
 * @Date 2020/7/4
 */
public interface IStockImpDataInfoService extends IService<StockImpDataInfo> {
    List<StockImpDataInfo> selectTheDayStock();
}
