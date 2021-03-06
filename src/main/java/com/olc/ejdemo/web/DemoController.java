package com.olc.ejdemo.web;

import com.alibaba.fastjson.JSONObject;
import com.olc.ejdemo.modle.StockImpDataInfo;
import com.olc.ejdemo.service.IStockImpDataInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Oulingcheng
 * @Date 2020/7/4
 */
@RestController
@Slf4j
public class DemoController {

    @Autowired
    IStockImpDataInfoService stockImpDataInfoService;

    @GetMapping("/test")
    public void insert(){
        List<StockImpDataInfo> stockImpDataInfos =  stockImpDataInfoService.selectTheDayStock();
        log.info("----------------------------------------------------------------");
        log.info(JSONObject.toJSONString(stockImpDataInfos));
        log.info("end");
    }
}
