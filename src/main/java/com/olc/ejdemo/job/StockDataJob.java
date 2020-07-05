package com.olc.ejdemo.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Oulingcheng
 * @Date 2020/7/4
 */
@Slf4j
@Component
public class StockDataJob implements SimpleJob {
    // 股票数据类型
    private static LinkedHashMap<String, String> STOCK_TYPE_MAP = new LinkedHashMap<>();
    private static LinkedHashMap<String, String> STOCK_TIME_MAP = new LinkedHashMap<>();
    private static String JS_NAME = "var {{jsName.DATA}}={pages:(pc),date:\"2014-10-22\",data:[(x)]}";
    // @Value("${nufm.dfcf.fund.flow.url}")
    private String dfcfFundFlowUrl = "http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx";

    @PostConstruct
    private void initConstruct() {
        // cmd=C._AB
        STOCK_TYPE_MAP.put("沪深A股", "C._A");
        // 注：此处东方财富反爬虫放毒，3、5、10日排行的数据返回中，股票顺序是正确的，其他数据却是当日排行的数据
        STOCK_TIME_MAP.put("今日排行", "(BalFlowMain)");

    }
    // private IStockDataInfoService stockDataInfoService;


    @Override
    public void execute(ShardingContext shardingContext) {
        Set<String> set = STOCK_TYPE_MAP.keySet();
        Set<String> timeSet = STOCK_TIME_MAP.keySet();
        // 当前发起抓取的抓取小时时间点 如：2018-05-20 17:00:00
        String formatDay = new SimpleDateFormat("yyyy-MM-dd HH:00:00").format(new Date());
        HttpClientUtils httpClientUtils = new HttpClientUtils();
        HttpGet HTMLHttpGet = HttpUtils.get("http://data.eastmoney.com/zjlx/detail.html");
        /*try {
            String html = httpClientUtils.executeWithResult(HTMLHttpGet);
            log.info("抓取东方财富的个股资金流的数据,请求抓取html, 返回的结果为[{}]", html);
        } catch (Exception e) {

        }*/
        String key = "沪深A股";
        String timeKey = "今日排行";
        try {
            String jsName = new String(JS_NAME).replace("{{jsName.DATA}}", randomJSCode());
            // 排行榜
            String stValue = STOCK_TIME_MAP.get(timeKey);
            // 股市
            String cmdValue = STOCK_TYPE_MAP.get(key);
            HttpGet httpGet = createDFCFHttpGet(jsName, stValue, cmdValue, 1, key, timeKey);
            String result = httpClientUtils.executeWithResult(httpGet, "utf-8");
            log.info("抓取东方财富的个股资金流的数据,请求抓取后,股市[{}]，排行榜[{}],[{}]页, 返回的结果为[{}]", key, timeKey, 1, result);
            String[] s1 = result.split("data:");
            String dataArr = s1[1].substring(0, s1[1].length() - 1);
            log.info("抓取东方财富的个股资金流的数据，解析返回数据,股市[{}]，排行榜[{}],[{}]页,解析返回的数据股票数值为[{}]", key, timeKey, 1, dataArr);
            String data1 = s1[0];
            int a1 = data1.indexOf("pages:");
            int a2 = data1.indexOf(",date");
            String page = data1.substring(a1 + 6, a2);
            log.info("抓取东方财富的个股资金流的数据，解析返回数据,股市[{}]，排行榜[{}],[{}]页, 解析返回的数据page值为[{}]", key, timeKey, 1, page);
            int pageInt = Integer.valueOf(page);
            if (pageInt > 1) {
                // 循环抓取分页中其他页的数据
                int allPage = pageInt + 1;
                // 获取分片项
                int shardingItem = JobUtil.execute(shardingContext);
                switch (shardingItem) {
                    case 0:
                        int beginPage0 = 1;
                        int endPage0 = 20;
                        allPageTask(formatDay, httpClientUtils, key, timeKey, jsName, stValue, cmdValue, page, beginPage0, endPage0);
                        break;
                    case 1:
                        int beginPage1 = 21;
                        int endPage1 = 40;
                        allPageTask(formatDay, httpClientUtils, key, timeKey, jsName, stValue, cmdValue, page, beginPage1, endPage1);
                        break;
                    case 2:
                        int beginPage2 = 41;
                        int endPage2 = 60;
                        allPageTask(formatDay, httpClientUtils, key, timeKey, jsName, stValue, cmdValue, page, beginPage2, endPage2);
                        break;
                    case 3:
                        int beginPage3 = 61;
                        allPageTask(formatDay, httpClientUtils, key, timeKey, jsName, stValue, cmdValue, page, beginPage3, allPage);
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            log.error("抓取东方财富的个股资金流的数据，请求抓取出现异常，异常为", e);
        } finally {
            httpClientUtils.close();
        }
    }

    private void allPageTask(String formatDay, HttpClientUtils httpClientUtils, String key, String timeKey, String jsName, String stValue, String cmdValue, String page, int beginPage,int endPage) {
        for (int i = beginPage; i < endPage; ++i) {
            HttpGet httpGet2 = createDFCFHttpGet(jsName, stValue, cmdValue, i, key, timeKey);
            try {
                // 休眠个3秒
                Thread.sleep(3 * 1000l);
                String result2 = httpClientUtils.executeWithResult(httpGet2, "utf-8");
                log.info("东方财富的数据,股市[{}]，排行榜[{}],[{}]页, 返回的结果为[{}]", key, timeKey, i, result2);
                String[] dateS2 = result2.split("data:");
                String dataArr2 = dateS2[1].substring(0, dateS2[1].length() - 1);
                log.info("解析:东方财富的数据,股市[{}]，排行榜[{}],[{}]页,解析返回的数据股票数值为[{}]", key, timeKey, 1, dataArr2);
                JSONArray jsonArray = JSONArray.parseArray(dataArr2);
                int size = jsonArray.size();
                for (int k = 0; k < size; k++) {
                    // 存储数据
                    insertDataInfo(jsonArray, k, key, timeKey, formatDay, i);
                }
            } catch (Exception e) {
                log.error("抓取东方财富的个股资金流的数据，解析返回数据,股市[{}]，排行榜[{}],[{}]页, 解析返回的数据page值为[{}],出现异常:", key, timeKey, 1, page, e);
            }
        }
    }

    private HttpGet createDFCFHttpGet(String jsName, String stValue,
                                      String cmdValue, int i, String key, String timeKey) {
        String time = String.valueOf(System.currentTimeMillis() / 30000);
        // 生成随机的js name code
        String code = randomJSCode();
        String jsNameReal = jsName.replace("{{name.data}}", code);
        Map<String, Object> params = new HashMap<>();
        params.put("type", "ct");
        params.put("st", stValue);
        params.put("sr", "-1");
        params.put("p", String.valueOf(i));
        params.put("ps", "50");
        params.put("js", jsNameReal);
        params.put("token", "894050c76af8597a853f5b408b759f5d");
        params.put("cmd", cmdValue);
        params.put("sty", "DCFFITA");
        params.put("rt", time);
        log.info("抓取东方财富的个股资金流的数据，请求抓取之前,股市[{}]，排行榜[{}],[{}]页",
                key, timeKey, i);
        HttpGet httpGet = HttpUtils.get(dfcfFundFlowUrl, params);
        httpGet = addHeadStock(httpGet);
        return httpGet;
    }

    private String randomJSCode() {
        String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        String[] codes = str.split("");
        int num = 8;
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < num; i++) {
            int a = random.nextInt(52);
            code.append(codes[a]);
        }
        return code.toString();
    }

    private HttpGet addHeadStock(HttpGet httpGet) {
        httpGet.addHeader("Accept", "*/*");
        httpGet.addHeader("Accept-Encoding", "gzip, deflate");
        httpGet.addHeader("Accept-Language", "zh-CN,zh;q=0.9");
        httpGet.addHeader("Connection", "keep-alive");
        httpGet.addHeader("Host", "nufm.dfcfw.com");
        httpGet.addHeader("Referer", "http://data.eastmoney.com/zjlx/detail.html");
        httpGet.addHeader("User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit" +
                        "/537.36 (KHTML, like Gecko) Chrome/66.0.3359.181 Safari/537.36");
        return httpGet;
    }

    private void insertDataInfo(JSONArray jsonArray, int k, String key, String timeKey, String formatDay, int stockPage) {
        try {
            String infoStr = (String) jsonArray.get(k);
            log.info("解析东方财富的个股资金流的数据,第[{}]个，当前数据为[{}]", k, infoStr);
            String[] infoStrings = infoStr.split(",");
            // 清洗数据将 "-" 转化为 "0"
            cleanInfoStringArr(infoStrings);
            String stockCode = infoStrings[1];
            String stockName = infoStrings[2];
            String stockTime = infoStrings[15];
            BigDecimal priceNew = new BigDecimal(infoStrings[3]).setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal change = new BigDecimal(infoStrings[4]).setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal mainNetInflowAmount = new BigDecimal(infoStrings[5]).setScale(4, BigDecimal.ROUND_HALF_UP);
            BigDecimal mainNetProportion = new BigDecimal(infoStrings[6]).setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal superBigPartNetInFlowAmount = new BigDecimal(infoStrings[7]).setScale(4, BigDecimal.ROUND_HALF_UP);
            BigDecimal superBigPartNetProportion = new BigDecimal(infoStrings[8]).setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal bigPartNetInFlowAmount = new BigDecimal(infoStrings[9]).setScale(4, BigDecimal.ROUND_HALF_UP);
            BigDecimal bigPartNetProportion = new BigDecimal(infoStrings[10]).setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal middlePartNetInFlowAmount = new BigDecimal(infoStrings[11]).setScale(4, BigDecimal.ROUND_HALF_UP);
            BigDecimal middlePartNetProportion = new BigDecimal(infoStrings[12]).setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal litterPartNetInFlowAmount = new BigDecimal(infoStrings[13]).setScale(4, BigDecimal.ROUND_HALF_UP);
            BigDecimal litterPartNetProportion = new BigDecimal(infoStrings[14]).setScale(2, BigDecimal.ROUND_HALF_UP);
            Date time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(infoStrings[15]);
            String timeVersion = key + "#" + timeKey + "#" + stockTime;
            BigDecimal someInfo = new BigDecimal(0);
            if (infoStrings[16] != null) {
                someInfo = new BigDecimal(infoStrings[16]).setScale(4, BigDecimal.ROUND_HALF_UP);
            }
            // 创建对象
            StockDataInfo stockDataInfo = new StockDataInfo();
            stockDataInfo.setStockCode(stockCode);
            stockDataInfo.setStockName(stockName);
            stockDataInfo.setPriceNew(priceNew);
            stockDataInfo.setStockChange(change);
            stockDataInfo.setMainAmount(mainNetInflowAmount);
            stockDataInfo.setMainRatio(mainNetProportion);
            stockDataInfo.setSuperAmount(superBigPartNetInFlowAmount);
            stockDataInfo.setSuperRatio(superBigPartNetProportion);
            stockDataInfo.setBigAmount(bigPartNetInFlowAmount);
            stockDataInfo.setBigRatio(bigPartNetProportion);
            stockDataInfo.setMiddleAmount(middlePartNetInFlowAmount);
            stockDataInfo.setMiddleRatio(middlePartNetProportion);
            stockDataInfo.setLitterAmount(litterPartNetInFlowAmount);
            stockDataInfo.setLitterRatio(litterPartNetProportion);
            stockDataInfo.setCountTime(time);
            stockDataInfo.setStockPage(stockPage);
            stockDataInfo.setSomeinfo(someInfo.toString());
            stockDataInfo.setTimeVersion(timeVersion);
            stockDataInfo.setUpdateTime(new Date());
            log.info("stock is {}",stockCode);
            QueryWrapper<StockDataInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(StockDataInfo::getStockCode,stockCode);
            StockDataInfoMapper stockDataInfoMapper = AddBeanUtil.getBean(StockDataInfoMapper.class);
            StockDataInfo newStockData = stockDataInfoMapper.selectByPrimaryKey(stockCode);
            if (Objects.isNull(newStockData)) {
                stockDataInfo.setCreateTime(new Date());
                stockDataInfo.setCrawlerVersion(1L);
                int result = stockDataInfoMapper.insert(stockDataInfo);
                log.info("第一次东方财富的数据[{}]，数据为[{}]",result,  JSON.toJSONString(stockDataInfo));
            } else {
                stockDataInfo.setId(newStockData.getId());
                stockDataInfo.setCrawlerVersion(newStockData.getCrawlerVersion()+1);
                int result = stockDataInfoMapper.updateByPrimaryKeySelective(stockDataInfo);
                log.info("更新东方财富的数据[{}]，数据为[{}]", result, JSON.toJSONString(stockDataInfo));
            }
        } catch (Exception e) {
            log.error("插入或更新东方财富的数据，出现异常{}", e);
        }
    }

    private void cleanInfoStringArr(String[] infoStrings) {
        for (int i = 0; i < infoStrings.length; ++i) {
            infoStrings[i] = infoStrings[i].equals("-") ? "0" : infoStrings[i];
        }
    }
}
