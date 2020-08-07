package com.olc.ejdemo.web;

import com.olc.ejdemo.constants.Urls;
import com.olc.ejdemo.modle.StockImpDataInfo;
import com.olc.ejdemo.service.IStockImpDataInfoService;
import com.olc.ejdemo.web.base.view.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description
 * @Author ex-oulingcheng
 * @Date 2020/8/7 15:30
 */
@RestController
@Slf4j
public class CommonController {

    @Autowired
    IStockImpDataInfoService stockImpDataInfoService;

    @ResponseBody
    @PostMapping(Urls.GET_TODAY_DATA)
    public ResponseVo<List<StockImpDataInfo>> getTodayData(){
        List<StockImpDataInfo> stockImpDataInfos =  stockImpDataInfoService.selectTheDayStock();
        return ResponseVo.success(stockImpDataInfos);
    }

}
