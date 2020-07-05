package com.olc.ejdemo.web;

import com.alibaba.fastjson.JSONObject;
import com.olc.ejdemo.mapper.StockDataInfoMapper;
import com.olc.ejdemo.modle.StockDataInfo;
import com.olc.ejdemo.util.AddBeanUtil;
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
    StockDataInfoMapper stockDataInfoService;

    @GetMapping("/test")
    public void insert(){
        log.info("testbegin");
        StockDataInfoMapper stockDataInfoMapper = AddBeanUtil.getBean(StockDataInfoMapper.class);
        List<StockDataInfo> stockDataInfos = stockDataInfoMapper.selectImpData(0, 1000);
        log.info("提取主要股票----------------------------------------------------------------");
        log.info("数据长度：{}",stockDataInfos.size());
        log.info("{}", JSONObject.toJSONString(stockDataInfos));
        log.info("end");
    }
}
