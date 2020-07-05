package com.olc.ejdemo.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.olc.ejdemo.mapper.StockDataInfoMapper;
import com.olc.ejdemo.mapper.StockImpDataInfoMapper;
import com.olc.ejdemo.modle.StockDataInfo;
import com.olc.ejdemo.modle.StockImpDataInfo;
import com.olc.ejdemo.util.AddBeanUtil;
import com.olc.ejdemo.util.JobUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Oulingcheng
 * @Date 2020/7/5
 */
@Slf4j
public class ImportStockJob implements SimpleJob {
    @Override
    public void execute(ShardingContext shardingContext) {
        log.info("test start");
        int shardingItem = JobUtil.execute(shardingContext);
        switch (shardingItem) {
            case 0:
                // 提取指定数据
                extractSpecifiedData(0, 1000);
                break;
            case 1:
                extractSpecifiedData(1001, 2000);
                break;
            case 2:
                extractSpecifiedData(2001, 3000);
                break;
            case 3:
                extractSpecifiedData(3001, 4000);
                break;
            default:
                break;
        }
    }

    private void extractSpecifiedData(int begin, int end) {
        StockDataInfoMapper stockDataInfoMapper = AddBeanUtil.getBean(StockDataInfoMapper.class);
        List<StockDataInfo> stockDataInfos = stockDataInfoMapper.selectImpData(begin, end);
        List<StockImpDataInfo> stockImpDataInfos = new ArrayList<>();
        log.info("提取主要股票----------------------------------------------------------------");
        if (CollectionUtils.isEmpty(stockDataInfos))return;
        List<StockDataInfo> collect = stockDataInfos.stream().filter(s -> (
                        (BigDecimal.valueOf(100000000).compareTo(s.getBecomeAmount())<0)
                        && (BigDecimal.valueOf(1000).compareTo(s.getLitterAmount()) < 0)
                        && (BigDecimal.valueOf(1000).compareTo(s.getMiddleAmount()) < 0)

                        /*&& (BigDecimal.valueOf(1000).compareTo(s.getMainAmount()) < 0)
                        && (BigDecimal.valueOf(1000).compareTo(s.getSuperAmount()) < 0)
                        && (BigDecimal.valueOf(1000).compareTo(s.getLitterAmount()) > 0)*/

                        /*&& ((s.getBecomeAmount().divide(s.getCurrentMarketValue(),5,BigDecimal.ROUND_HALF_UP).compareTo(BigDecimal.valueOf(0.04)))>0)
                        && (s.getMainAmount().multiply(BigDecimal.valueOf(10000).divide(s.getBecomeAmount(),5,BigDecimal.ROUND_HALF_UP)).compareTo(BigDecimal.valueOf(0.04))>0)
                        && (s.getMainAmount().multiply(BigDecimal.valueOf(10000).divide(s.getBecomeAmount(),5,BigDecimal.ROUND_HALF_UP)).compareTo(BigDecimal.valueOf(0.1))<0)*/
                        && (s.getStockChange().compareTo(BigDecimal.valueOf(0))<0)
                    )
                ).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collect)) {
            log.info("符合条件的为空值");
            return;
        }
        for (StockDataInfo e : collect) {
            BigDecimal currentMarket = e.getCurrentMarketValue();// 流通市值
            BigDecimal mainAmount = e.getMainAmount();// 主力净额
            BigDecimal becomeAmount = e.getBecomeAmount(); // 成交额
            BigDecimal mainAcountThan = mainAmount.multiply(BigDecimal.valueOf(10000).divide(becomeAmount,5,BigDecimal.ROUND_HALF_UP));// 主力净流入/成交额  主成比
            BigDecimal asFlowThan = e.getBecomeAmount().divide(currentMarket,5,BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100));// 成交额/流通市值  成流比
            StockImpDataInfo stockImpDataInfo = new StockImpDataInfo();
            stockImpDataInfo
                    .setId(e.getStockCode())
                    .setAsFlowThan(asFlowThan)
                    .setBecomeAmount(becomeAmount)
                    .setCurrentMarketValue(currentMarket)
                    .setMainAmountThan(mainAcountThan)
                    .setStockName(e.getStockName())
                    .setStockCode(e.getStockCode())
                    .setStockChange(e.getStockChange())
                    .setLitterAmount(e.getLitterAmount())
                    .setBigAmount(e.getBigAmount())
                    .setMainAmount(e.getMainAmount())
                    .setSuperAmount(e.getSuperAmount())
                    .setMiddleAmount(e.getMiddleAmount())
                    .setPeRatio(e.getPeRatio())
                    .setPriceNew(e.getPriceNew())
                    .setUpdateTime(new Date());
            if (mainAmount.compareTo(BigDecimal.valueOf(50000000)) >= 0) {
                stockImpDataInfo.setStatusCode(1);
            } else if (mainAmount.compareTo(BigDecimal.valueOf(25000000)) >= 0) {
                stockImpDataInfo.setStatusCode(2);
            } else{
                stockImpDataInfo.setStatusCode(3);
            }
            stockImpDataInfos.add(stockImpDataInfo);
        }
        StockImpDataInfoMapper stockImpDataInfoMapper =
                AddBeanUtil.getBean(StockImpDataInfoMapper.class);
        for (StockImpDataInfo s : stockImpDataInfos) {
            StockImpDataInfo stockImpDataInfo = stockImpDataInfoMapper.selectByPrimaryKey(s.getId());
            if (Objects.isNull(stockImpDataInfo)){
                stockImpDataInfoMapper.insert(s);
            }else {
                stockImpDataInfoMapper.updateByPrimaryKeySelective(s);
            }
        }
    }
}
