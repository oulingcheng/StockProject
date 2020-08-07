package com.olc.ejdemo.job;

import com.alibaba.fastjson.JSONObject;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.olc.ejdemo.constants.Constant;
import com.olc.ejdemo.mapper.StockDataInfoMapper;
import com.olc.ejdemo.mapper.StockImpDataInfoMapper;
import com.olc.ejdemo.modle.StockDataInfo;
import com.olc.ejdemo.modle.StockImpDataInfo;
import com.olc.ejdemo.util.AddBeanUtil;
import com.olc.ejdemo.util.ExcelProcessUtils;
import com.olc.ejdemo.util.JobUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
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
                try {
                    extractSpecifiedData(0, 4000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    private void extractSpecifiedData(int begin, int end) throws Exception {
        StockDataInfoMapper stockDataInfoMapper = AddBeanUtil.getBean(StockDataInfoMapper.class);
        List<StockDataInfo> stockDataInfos = stockDataInfoMapper.selectImpDataAll();

        log.info("提取主要----------------------------------------------------------------");
        if (CollectionUtils.isEmpty(stockDataInfos)) return;
        String format = Constant.TimeConstant.TIME_FORMAT;
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        // 获取时分秒
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int secound = cal.get(Calendar.SECOND);
        String nowStr = hour + ":" + minute + ":" + secound;
        String aplTimeStr = "12:00:00";
        Date aplTime = null;
        Date nowTime = null;
        try {
            nowTime = formatter.parse(nowStr);
            aplTime = new SimpleDateFormat(format).parse(aplTimeStr);
        } catch (ParseException e) {

        }
        Calendar nowDateTime = Calendar.getInstance();
        nowDateTime.setTime(nowTime);
        Calendar aplDateTime = Calendar.getInstance();
        aplDateTime.setTime(aplTime);
        if (nowDateTime.before(aplDateTime)) {// 12点之前
            log.info("早上");
            List<StockDataInfo> max = getMaxStockDataInfos(stockDataInfos, 600000000, 5000);
            List<StockDataInfo> min = getMinStockDataInfos(stockDataInfos, 600000000, 5000);
            if (CollectionUtils.isEmpty(max)) {
                log.info("符合条件的为空值");
            } else {

            }
            processList(max, min, 5000,"m");
        } else {// 12点之后
            log.info("下午");
            List<StockDataInfo> max = getMaxStockDataInfos(stockDataInfos, 800000000, 10000);
            List<StockDataInfo> min = getMinStockDataInfos(stockDataInfos, 800000000, 10000);
            if (CollectionUtils.isEmpty(max)) {
                log.info("符合条件的为空值");
            } else {

            }
            processList(max, min, 10000,"g");
        }
    }

    private ArrayList<StockImpDataInfo> removeDuplicateImpl(List<StockImpDataInfo> list) {
        Set<StockImpDataInfo> set = new TreeSet<StockImpDataInfo>(new Comparator<StockImpDataInfo>() {
            @Override
            public int compare(StockImpDataInfo o1, StockImpDataInfo o2) {
                //字符串,则按照asicc码升序排列
                return o1.getStockCode().compareTo(o2.getStockCode());
            }
        });
        set.addAll(list);
        return new ArrayList<StockImpDataInfo>(set);
    }

    private void processList(List<StockDataInfo> max, List<StockDataInfo> min, int num,String time) throws Exception {
        List<StockImpDataInfo> sMax = getStackImpData("流通市值/成交额 小于100-----",max);
        List<StockImpDataInfo> sMin = getStackImpData("流通市值/成交额 大于100-----",min);
        ArrayList<StockImpDataInfo> maxList = removeDuplicateImpl(sMax);
        ArrayList<StockImpDataInfo> minList = removeDuplicateImpl(sMin);
        int month = LocalDate.now().getMonthValue();
        int day = LocalDate.now().getDayOfMonth();
        String d = String.format("%02d", day);
        int year = LocalDate.now().getYear();
        String m = String.format("%02d", month);
        String fileName = "" + year + m + d;
        File file = null;
        FileInputStream fis = null;
        try {
            file = new File("D:\\test\\模型\\" + num + File.separator + fileName + ".xlsx");
            fis = new FileInputStream(file);
        } catch (Exception e) {
            log.info("找不到文件，使用模板文件");
            try {
                ClassPathResource classPathResource = new ClassPathResource("template.xlsx");
                log.info("file----------"+classPathResource.getPath());
                file = classPathResource.getFile();
                fis = new FileInputStream(file);
            } catch (Exception e1) {
                log.info("找不到文件",e1);
                try {
                    ClassPathResource classPathResource = new ClassPathResource("tmp/template.xlsx");
                    log.info("file----------"+classPathResource.getPath());
                    file = classPathResource.getFile();
                    fis = new FileInputStream(file);
                } catch (Exception e2) {
                    log.info("找不到文件",e2);
                }
            }
        }

        //新的 byte 数组输出流，缓冲区容量1024byte
        ByteArrayOutputStream bos1 = new ByteArrayOutputStream(1024);
        //缓存
        byte[] b = new byte[1024];
        int n;
        while ((n = fis.read(b)) != -1) {
            bos1.write(b, 0, n);
        }
        fis.close();
        //改变为byte[]
        byte[] fileTemplateBytes = bos1.toByteArray();
        getWorkbook(num, maxList, minList, fileTemplateBytes, fileName,time);
    }


    private void getWorkbook(int num, List<StockImpDataInfo> sMax, List<StockImpDataInfo> sMin, byte[] fileTemplateBytes, String fileName, String time) throws IOException {
        File file1 = new File("D:\\test\\模型\\" + num + File.separator + fileName + ".xlsx");
        try {
            FileOutputStream output = new FileOutputStream(file1);
            Workbook workbook = null;
            workbook = new XSSFWorkbook(new ByteArrayInputStream(fileTemplateBytes));
            if (workbook.getNumberOfSheets() > 0) {
                if (!CollectionUtils.isEmpty(sMax)){
                    Sheet sheet = workbook.getSheetAt(0);
                    processData(sMax, time, workbook, sheet);
                }
                if (!CollectionUtils.isEmpty(sMin)){
                    Sheet sheet1 = workbook.getSheetAt(1);
                    processData(sMin, time, workbook, sheet1);
                }
            }
            workbook.write(output);
        } catch (Exception e) {
            log.info("make excel failed");
        }

    }

    private void processData(List<StockImpDataInfo> impDataInfos, String time, Workbook workbook, Sheet sheet) {
        List<ExcelProcessUtils.ExcelProcessDto> excelProcessDtos = new ArrayList<>();
        if (CollectionUtils.isEmpty(impDataInfos))return;
        for (Row row : sheet) {
            for (Cell cell : row) {
                ExcelProcessUtils.cellProcess(cell, null, excelProcessDtos, row);
            }
        }
        for (ExcelProcessUtils.ExcelProcessDto e : excelProcessDtos) {
            Row row = sheet.getRow(e.getRowNum());
            for (int i = 0; i < impDataInfos.size(); i++) {
                log.info("----------------->"+impDataInfos.get(i).getStockCode());
                Row nowRow = sheet.getRow(e.getRowNum());
                nowRow.createCell(e.getCellNum()+i+1);
                ExcelProcessUtils.copyCell(workbook, nowRow.getCell(e.getCellNum()), nowRow.getCell(e.getCellNum()+1+i), true);

                for (Cell cell : row) {
                    if (time.equals("m")){
                        if (cell.getStringCellValue().contains("mCode")){
                            ExcelProcessUtils.cellProcess(cell, impDataInfos.get(i).getStockCode());
                            break;
                        }
                        if (cell.getStringCellValue().contains("mMessage")){
                            String s = "(" + impDataInfos.get(i).getStockChange() + ")(" + impDataInfos.get(i).getPriceNew() + ")";
                            ExcelProcessUtils.cellProcess(cell, s);
                            break;
                        }
                    }else if (time.equals("g")){
                        if (cell.getStringCellValue().contains("gCode")){
                            ExcelProcessUtils.cellProcess(cell, impDataInfos.get(i).getStockCode());
                            break;
                        }
                        if (cell.getStringCellValue().contains("gMessage")){
                            String s = "(" + impDataInfos.get(i).getStockChange() + ")(" + impDataInfos.get(i).getPriceNew() + ")";
                            ExcelProcessUtils.cellProcess(cell, s);
                            break;
                        }
                    }
                }
            }

        }
    }

    private List<StockImpDataInfo> getStackImpData(String flag, List<StockDataInfo> max) {
        StockImpDataInfoMapper stockImpDataInfoMapper = AddBeanUtil.getBean(StockImpDataInfoMapper.class);
        List<StockImpDataInfo> stockImpDataInfos = new ArrayList<>();
        for (StockDataInfo e : max) {
            log.info(flag+"result -----" + e.getStockCode()+"("+e.getStockChange()+")("+e.getPriceNew()+")");
            BigDecimal currentMarket = e.getCurrentMarketValue();// 流通市值
            BigDecimal mainAmount = e.getMainAmount();// 主力净额
            BigDecimal becomeAmount = e.getBecomeAmount(); // 成交额
            BigDecimal mainAcountThan = mainAmount.multiply(BigDecimal.valueOf(10000).divide(becomeAmount, 5, BigDecimal.ROUND_HALF_UP));// 主力净流入/成交额  主成比
            BigDecimal asFlowThan = e.getBecomeAmount().divide(currentMarket, 5, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100));// 成交额/流通市值  成流比
            StockImpDataInfo stockImpDataInfo = new StockImpDataInfo();
            stockImpDataInfo
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
                    .setNow(e.getStockChange().compareTo(BigDecimal.ZERO) > 0 ? "1" : "-1")
                    .setPeRatio(e.getPeRatio())
                    .setPriceNew(e.getPriceNew())
                    .setUpdateTime(new Date());
            if (mainAmount.compareTo(BigDecimal.valueOf(50000000)) >= 0) {
                stockImpDataInfo.setStatusCode(1);
            } else if (mainAmount.compareTo(BigDecimal.valueOf(25000000)) >= 0) {
                stockImpDataInfo.setStatusCode(2);
            } else {
                stockImpDataInfo.setStatusCode(3);
            }
            StockImpDataInfo s = stockImpDataInfoMapper.selectTheDay(e.getStockCode());
            if (Objects.isNull(s)){
                int insert = stockImpDataInfoMapper.insert(stockImpDataInfo);
                if (insert !=1 ){
                    log.info("添加失败--  "+ JSONObject.toJSONString(e));
                }
                stockImpDataInfos.add(stockImpDataInfo);
            }
        }
        return stockImpDataInfos;
    }

    private List<StockDataInfo> getMaxStockDataInfos(List<StockDataInfo> stockDataInfos, int bAmount, int mAmout) {//  流通市值/成交额 小于100,成交额占比比较大
        return stockDataInfos.stream().filter(s -> (
                        (BigDecimal.valueOf(bAmount).compareTo(s.getBecomeAmount()) < 0)
                                && s.getCurrentMarketValue().divide(s.getBecomeAmount(), 2, BigDecimal.ROUND_HALF_UP).compareTo(BigDecimal.valueOf(100)) < 0
                                && s.getCurrentMarketValue().compareTo(BigDecimal.valueOf(20000000000.0)) > 0
                                && s.getCurrentMarketValue().compareTo(BigDecimal.valueOf(150000000000.0)) < 0
                                && (s.getStockChange().compareTo(BigDecimal.valueOf(3)) < 0)
                                && (s.getStockChange().compareTo(BigDecimal.valueOf(-3)) > 0)
                                && s.getMainAmount().compareTo(BigDecimal.valueOf(mAmout)) > 0
                )
        ).collect(Collectors.toList());
    }

    private List<StockDataInfo> getMinStockDataInfos(List<StockDataInfo> stockDataInfos, int bAmount, int mAmout) {//  流通市值/成交额 大于100,成交额占比比较小
        return stockDataInfos.stream().filter(s -> (
                        (BigDecimal.valueOf(bAmount).compareTo(s.getBecomeAmount()) < 0)
                                && s.getCurrentMarketValue().divide(s.getBecomeAmount(), 2, BigDecimal.ROUND_HALF_UP).compareTo(BigDecimal.valueOf(100)) > 0
                                && (s.getStockChange().compareTo(BigDecimal.valueOf(3)) < 0)
                                && (s.getStockChange().compareTo(BigDecimal.valueOf(-3)) > 0)
                                && s.getMainAmount().compareTo(BigDecimal.valueOf(mAmout)) > 0
                                && s.getCurrentMarketValue().compareTo(BigDecimal.valueOf(20000000000.0)) > 0
                                && s.getCurrentMarketValue().compareTo(BigDecimal.valueOf(150000000000.0)) < 0
                )
        ).collect(Collectors.toList());
    }
}
