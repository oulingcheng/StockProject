package com.olc.ejdemo.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.olc.ejdemo.mapper.StockDataInfoMapper;
import com.olc.ejdemo.modle.StockDataInfo;
import com.olc.ejdemo.util.AddBeanUtil;
import com.olc.ejdemo.util.HttpClientUtils;
import com.olc.ejdemo.util.HttpUtils;
import com.olc.ejdemo.util.JobUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpGet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author Oulingcheng
 * @Date 2020/7/4
 */
@Slf4j
@Component
public class ThsStockJob implements SimpleJob {


    @Override
    public void execute(ShardingContext shardingContext) {
        HttpClientUtils httpClientUtils = new HttpClientUtils();
        // 获取分片项
        int shardingItem = JobUtil.execute(shardingContext);
        switch (shardingItem) {
            case 0:
                allPageTask(httpClientUtils,1,45);
                break;
            case 1:
                allPageTask(httpClientUtils,46,90);
                break;
            case 2:
                allPageTask(httpClientUtils,91,135);
                break;
            case 3:
                allPageTask(httpClientUtils,136,190);
                break;
            default:
                break;
        }

    }

    private void allPageTask(HttpClientUtils httpClientUtils,int beginPage,int endPage) {
        for (int i = beginPage; i < endPage; i++) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String path = "http://q.10jqka.com.cn/index/index/board/all/field/zdf/order/desc/page/"+(i+1)+"/ajax/1";
            HttpGet HTMLHttpGet = HttpUtils.get(path);
            try {
                String html = httpClientUtils.executeWithResult(HTMLHttpGet);
                log.info("抓取同花顺的个股资金流的数据,请求抓取html, 返回的结果为[{}]", html);
                Document parse = Jsoup.parse(html);
                Elements tb = parse.select("table").get(0).select("tbody");

                for (int j = 0; j < tb.get(0).select("tr").size(); j++) {
                    Element tr = tb.get(0).select("tr").get(j);
                    log.info("序号：{}，代码：{}，名称：{}，现价：{}，涨跌幅：{}，涨跌：{}，涨速度：{}，换手：{}，量比：{}，振幅：{}，成交额：{}，流通股：{}，流通市值：{}，市盈率：{}",
                            tr.select("td").get(0).text(),// 序号
                            tr.select("td").get(1).text(),// 代码
                            tr.select("td").get(2).text(),// 名称
                            tr.select("td").get(3).text(),// 现价
                            tr.select("td").get(4).text(),// 涨跌幅
                            tr.select("td").get(5).text(),// 涨跌
                            tr.select("td").get(6).text(),// 涨速度
                            tr.select("td").get(7).text(),// 换手
                            tr.select("td").get(8).text(),// 量比
                            tr.select("td").get(9).text(),// 振幅
                            tr.select("td").get(10).text(),// 成交额
                            tr.select("td").get(11).text(),// 流通股
                            tr.select("td").get(12).text(),// 流通市值
                            tr.select("td").get(13).text());// 市盈率
                    String code = tr.select("td").get(1).text();
                    String currentMarket = tr.select("td").get(12).text();
                    String peRatio = tr.select("td").get(13).text();
                    String turnOver = tr.select("td").get(10).text();
                    StockDataInfo stockDataInfo = new StockDataInfo();
                    stockDataInfo.setStockCode(code);
                    BigDecimal cmt = BigDecimal.ZERO;
                    cmt = str2Bigdecimal(currentMarket, cmt);
                    BigDecimal pr = BigDecimal.ZERO;
                    pr = str2Bigdecimal(peRatio, pr);
                    BigDecimal to = BigDecimal.ZERO;
                    to = str2Bigdecimal(turnOver, to);
                    stockDataInfo.setCurrentMarketValue(cmt);
                    stockDataInfo.setPeRatio(pr);
                    stockDataInfo.setBecomeAmount(to);
                    System.out.println("开始修改");
                    StockDataInfoMapper stockDataInfoMapper = AddBeanUtil.getBean(StockDataInfoMapper.class);
                    int i1 = stockDataInfoMapper.updateByCode(stockDataInfo);
                    System.out.println("修改结果："+i1);
                }
            } catch (Exception e) {
                log.info("ths job failed:",e);
            }
        }
    }

    private BigDecimal str2Bigdecimal(String currentMarket, BigDecimal cmt) {
        if (currentMarket.contains("亿")) {
            String[] cm = currentMarket.split("亿");
            String s = cm[0];
            double v = Double.parseDouble(s);
            BigDecimal bigDecimal = BigDecimal.valueOf(100000000);
            cmt = BigDecimal.valueOf(v).multiply(bigDecimal);
        } else if (currentMarket.contains("万")) {
            String[] cm = currentMarket.split("万");
            String s = cm[0];
            double v = Double.parseDouble(s);
            BigDecimal bigDecimal = BigDecimal.valueOf(10000);
            cmt = BigDecimal.valueOf(v).multiply(bigDecimal);
        }else if (currentMarket.contains("亏") || currentMarket.contains("-")){
            cmt = BigDecimal.valueOf(-1);
        }else {
            double v = Double.parseDouble(currentMarket);
            BigDecimal bigDecimal = BigDecimal.valueOf(10000);
            cmt = BigDecimal.valueOf(v).multiply(bigDecimal);
        }
        return cmt;
    }
}
