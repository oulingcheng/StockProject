package com.olc.ejdemo;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Oulingcheng
 * @Date 2020/7/12
 */
@Slf4j
public class JsonToHtmlServiceTest {
    private static final String REQUEST_HEADER_X_REQ_NAME = "TP-DocumentExport";
    private static final String REQUEST_HEADER_X_REQ = "X-REQ-NAME";
    private static final String REQUEST_HEADER_X_TRACE = "X-TRACE-ID";
    private static final String RESPONSE_CODE_500 = "500";
    private static final String RESPONSE_CODE_200 = "200";

    @Test
    public void jsonToHtml() throws Exception {
        String jsonString = "{\n" +
                "        \"loanTerm\":36,\n" +
                "        \"businessSum\":300000,\n" +
                "        \"priceType\":\"VI\",\n" +
                "        \"riskType\":\"P14\",\n" +
                "        \"executeRate\":7,\n" +
                "        \"totalCustomerCost\":146872.64,\n" +
                "        \"endingAllBalance\":300000,\n" +
                "        \"nowDate\":\"2020-07-09\",\n" +
                "        \"feeRateList\":[\n" +
                "            {\n" +
                "                \"feeType\":\"004\",\n" +
                "                \"rate\":3,\n" +
                "                \"totalFeeCost\":9999\n" +
                "            }\n" +
                "        ],\n" +
                "        \"plan\":[\n" +
                "            {\n" +
                "                \"sterm\":1,\n" +
                "                \"payCorp\":222,\n" +
                "                \"payInte\":333,\n" +
                "                \"bankMonthPay\":44,\n" +
                "                \"endingBalance\":300000,\n" +
                "                \"totalMonthPay\":0,\n" +
                "                \"feeList\":[\n" +
                "                    {\n" +
                "                        \"sterm\":0,\n" +
                "                        \"feeType\":\"004\",\n" +
                "                        \"feeCost\":445\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"sterm\":0,\n" +
                "                        \"feeType\":\"003\",\n" +
                "                        \"feeCost\":44\n" +
                "                    }\n" +
                "                ]\n" +
                "            },\n" +
                "            {\n" +
                "                \"sterm\":2,\n" +
                "                \"payCorp\":7556.96,\n" +
                "                \"payInte\":1706.17,\n" +
                "                \"bankMonthPay\":9263.13,\n" +
                "                \"endingBalance\":284929.92,\n" +
                "                \"totalMonthPay\":12413.13,\n" +
                "                \"feeList\":[\n" +
                "                    {\n" +
                "                        \"sterm\":1,\n" +
                "                        \"feeType\":\"004\",\n" +
                "                        \"feeCost\":3160\n" +
                "                    }\n" +
                "                ]\n" +
                "            },\n" +
                "            {\n" +
                "                \"sterm\":3,\n" +
                "                \"payCorp\":7556.96,\n" +
                "                \"payInte\":1706.17,\n" +
                "                \"bankMonthPay\":9263.13,\n" +
                "                \"endingBalance\":284929.92,\n" +
                "                \"totalMonthPay\":12413.13,\n" +
                "                \"feeList\":[\n" +
                "                    {\n" +
                "                        \"sterm\":1,\n" +
                "                        \"feeType\":\"004\",\n" +
                "                        \"feeCost\":3170\n" +
                "                    }\n" +
                "                ]\n" +
                "            }\n" +
                "        ]\n" +
                "    }";
        final String serialNoKey = "serialNo";
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Object parseResult = JSON.parse(jsonString);
        /*if (parseResult instanceof JSONObject){
            log.info("处理object数据");
            JSONObject jsonObject = JSONObject.parseObject(jsonString);
            List<ExcelParamParseResultPo> l = new ArrayList<>();
            ExcelParamParseResultPo ep1 = new ExcelParamParseResultPo("businessSum","B2","field");
            ExcelParamParseResultPo ep2 = new ExcelParamParseResultPo("loanTerm","F2","field");
            ExcelParamParseResultPo ep3 = new ExcelParamParseResultPo("priceType","H2","field");
            ExcelParamParseResultPo ep4 = new ExcelParamParseResultPo("riskType",	"B3"	,"field");
            ExcelParamParseResultPo ep5 = new ExcelParamParseResultPo("totalCustomerCost",	"F3"	,"field");
            ExcelParamParseResultPo ep18 = new ExcelParamParseResultPo("endingAllBalance",	"F8"	,"field");
            ExcelParamParseResultPo ep6 = new ExcelParamParseResultPo("executeRate"	,"H3",	"field");
            ExcelParamParseResultPo ep7 = new ExcelParamParseResultPo("feeRateList[feeType='004'].rate"	,"B4",	"field");
            ExcelParamParseResultPo ep8 = new ExcelParamParseResultPo("feeRateList[feeType='004'].totalFeeCost",	"F4",	"field");
            // ExcelParamParseResultPo ep9 = new ExcelParamParseResultPo("businessSum",	"F7"	,"field");
            ExcelParamParseResultPo ep10 = new ExcelParamParseResultPo("plan.sterm",	"A9",	"list");
            ExcelParamParseResultPo ep11= new ExcelParamParseResultPo("plan.payCorp",	"C9",	"list");
            ExcelParamParseResultPo ep12 = new ExcelParamParseResultPo("plan.payInte",	"D9"	,"list");
            ExcelParamParseResultPo ep13 = new ExcelParamParseResultPo("plan.bankMonthPay"	,"E9",	"list");
            ExcelParamParseResultPo ep14 = new ExcelParamParseResultPo("plan.endingBalance",	"F9"	,"list");
            ExcelParamParseResultPo ep15 = new ExcelParamParseResultPo("plan.feeList[feeType='004'].feeCost",	"G9",	"list");
            ExcelParamParseResultPo ep16 = new ExcelParamParseResultPo("plan.totalMonthPay"	,"H9"	,"list");
            ExcelParamParseResultPo ep17 = new ExcelParamParseResultPo("nowDate"	,"G15",	"field");
            l.add(ep1);
            l.add(ep2);
            l.add(ep3);
            l.add(ep4);
            l.add(ep5);
            l.add(ep6);
            l.add(ep7);
            l.add(ep8);
            // l.add(ep9);
            l.add(ep10);
            l.add(ep11);
            l.add(ep12);
            l.add(ep13);
            l.add(ep14);
            l.add(ep15);
            l.add(ep16);
            l.add(ep17);
            l.add(ep18);*/
        //如果没有参数则直接返回原文件
        // byte[] fileTemplateBytes = RemoteFileUtil.loadFromUrl(ftbProperties.getUrl().getDownload() + "?fileId=" + fileTemplatePo.getFileId());
        String path = "D:\\test\\线上面签.xlsx";
        File file = new File(path);
        FileInputStream fis = new FileInputStream(file);

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
        File file1 = new File("D:\\test\\线上面签_result.xlsx");
        FileOutputStream output = new FileOutputStream(file1);
        Workbook workbook = null;
        workbook = new XSSFWorkbook(new ByteArrayInputStream(fileTemplateBytes));
        Sheet sheet = workbook.getSheetAt(0);

        List dataArray = new ArrayList();
        dataArray.add("11");
        dataArray.add("22");
        dataArray.add("33");
        dataArray.add("44");
        Row row = sheet.getRow(8);
        for (int i = 0; i < dataArray.size(); i++) {
            Row newR = sheet.getRow(row.getRowNum()+i);
            log.info("newRowNum:   "+newR.getRowNum());
            if (i > 0) {
                //非第一行新增行，第一行直接进行替换
                log.info("创建新行");
                int toRowNum = row.getRowNum() + i;
                sheet.shiftRows(toRowNum, sheet.getLastRowNum(), 1, true, false);
                sheet.createRow(toRowNum);
                copyRow(workbook,row, sheet.getRow(toRowNum), true);
            }
        }
        workbook.write(output);
        //getByteArrayOutputStreamByJSONObject(serialNoKey, output, jsonObject, l, fileTemplateBytes);
    }

    public static void copyRow(Workbook wb, Row fromRow, Row toRow, boolean copyValueFlag) {
        toRow.setHeight(fromRow.getHeight());
        for (Iterator<Cell> cellIt = fromRow.cellIterator(); cellIt.hasNext(); ) {
            Cell tmpCell = cellIt.next();
            Cell newCell = toRow.createCell(tmpCell.getColumnIndex());
            copyCell(wb, tmpCell, newCell, copyValueFlag);
        }
        Sheet worksheet = fromRow.getSheet();
        for (int i = 0; i < worksheet.getNumMergedRegions(); i++) {
            CellRangeAddress cellRangeAddress = worksheet.getMergedRegion(i);
            if (cellRangeAddress.getFirstRow() == fromRow.getRowNum()) {
                CellRangeAddress newCellRangeAddress = new CellRangeAddress(toRow.getRowNum(),
                        (toRow.getRowNum() + (cellRangeAddress.getLastRow() - cellRangeAddress.getFirstRow())),
                        cellRangeAddress.getFirstColumn(), cellRangeAddress.getLastColumn());
                worksheet.addMergedRegionUnsafe(newCellRangeAddress);
            }
        }
    }
    public static void copyCell(Workbook wb, Cell srcCell, Cell distCell, boolean copyValueFlag) {
        CellStyle newStyle = wb.createCellStyle();
        CellStyle srcStyle = srcCell.getCellStyle();

        newStyle.cloneStyleFrom(srcStyle);
        newStyle.setFont(wb.getFontAt(srcStyle.getFontIndex()));

        // 样式
        distCell.setCellStyle(newStyle);

        // 内容
        if (srcCell.getCellComment() != null) {
            distCell.setCellComment(srcCell.getCellComment());
        }

        // 不同数据类型处理
        CellType srcCellType = srcCell.getCellTypeEnum();
        distCell.setCellType(srcCellType);

        if (copyValueFlag) {
            if (srcCellType == CellType.NUMERIC) {
                if (DateUtil.isCellDateFormatted(srcCell)) {
                    distCell.setCellValue(srcCell.getDateCellValue());
                } else {
                    distCell.setCellValue(srcCell.getNumericCellValue());
                }
            } else if (srcCellType == CellType.STRING) {
                distCell.setCellValue(srcCell.getRichStringCellValue());
            } else if (srcCellType == CellType.BLANK) {

            } else if (srcCellType == CellType.BOOLEAN) {
                distCell.setCellValue(srcCell.getBooleanCellValue());
            } else if (srcCellType == CellType.ERROR) {
                distCell.setCellErrorValue(srcCell.getErrorCellValue());
            } else if (srcCellType == CellType.FORMULA) {
                distCell.setCellFormula(srcCell.getCellFormula());
            }
        }
    }
}

// jsonObject输入Excel
    /*private void getByteArrayOutputStreamByJSONObject(String serialNoKey, FileOutputStream bos, JSONObject jsonObject,
                                                      List<ExcelParamParseResultPo> listByTemplateNo, byte[] fileTemplateBytes) throws IOException {
        Workbook workbook = null;
        workbook = new XSSFWorkbook(new ByteArrayInputStream(fileTemplateBytes));

        if (workbook.getNumberOfSheets() > 0) {
            Sheet sheet = workbook.getSheetAt(0);
            // 先处理字段,为了防止表格中的集合后面还有字段值
            // 如果途中处理了集合（可能会创建新行），字段值的表格位置会改变
            List<ExcelParamParseResultPo> fieldList = listByTemplateNo.stream().filter(
                    e -> FileTemplateConstants.EXCEL_PARAM_TYPE_FIELD.equals(e.getParamType())).collect(Collectors.toList());
            log.info("处理字段");
            for (ExcelParamParseResultPo parseResultPo : fieldList) {
                String cellContent = parseResultPo.getCellContent();
                CellAddress cellAddress = new CellAddress(parseResultPo.getCellLocation());
                Row row =  sheet.getRow(cellAddress.getRow());
                Cell cell = row.getCell(cellAddress.getColumn());
                String singleJsonValue = JsonHandler.getSingleJsonValue(jsonObject, cellContent);
                cell.setCellValue(singleJsonValue);
            }

            // 处理List
            log.info("处理list");
            List<ExcelParamParseResultPo> listList = listByTemplateNo.stream().filter(
                    e -> FileTemplateConstants.EXCEL_PARAM_TYPE_LIST.equals(e.getParamType())).collect(Collectors.toList());
            Set<String> jsonSet = new HashSet<>();
            for (ExcelParamParseResultPo parseResultPo : listList) {
                String cellContent = parseResultPo.getCellContent();
                String arrayName = JsonHandler.getArrayName(cellContent);// 获取list的name
                jsonSet.add(arrayName);
            }
            for (String key : jsonSet) {
                JSONArray jsonArray = jsonObject.getJSONArray(key);
                log.info("jsonArray:   "+jsonArray.toJSONString());
                getByteArrayOutputStreamByJSONArray(key,serialNoKey,jsonArray,listList,workbook);
            }
            workbook.write(bos);*/
            /*for (ExcelParamParseResultPo parseResultPo : listList) {
                CellAddress cellAddress = new CellAddress(parseResultPo.getCellLocation());
                Row row = sheet.getRow(cellAddress.getRow());// 获取该行
                Cell cell = row.getCell(cellAddress.getColumn());// 获取该行该列
                String cellContent = parseResultPo.getCellContent();
                String arrayName = JsonHandler.getArrayName(cellContent);// 获取list的name
                String arrayFidldName = JsonHandler.getArrayFidldName(cellContent);// 获取list的fieldName
                JSONArray jsonArray = jsonObject.getJSONArray(arrayName);
                for (int i = 0; i < jsonArray.size(); i++) {
                    Row newRow = row;
                    //非第一行新增行，且不在该同一个集合里面的对象，创建新一行，第一行直接进行替换
                    if (i > 0 && !jsonArrayKey.contains(arrayName)) {
                        System.out.println("arrayName:  "+arrayName);
                        newRow = sheet.createRow(cellAddress.getRow() + i);
                        copyRow(row, newRow, true);
                        jsonArrayKey.add(arrayName);
                    }

                    cell = newRow.getCell(cellAddress.getColumn()+i);
                    JSONObject arrayJsonObject = jsonArray.getJSONObject(i);
                    String str = arrayJsonObject.getString(arrayFidldName);
                   //  Object parseResult = JSON.parse(str);
                    // 数组里面有数组则根据jsonPath提取
                    if (str == null){
                        String jsonPath = JsonHandler.getInnerArrayName(cellContent);
                        str = JsonHandler.getSingleJsonValue(arrayJsonObject, jsonPath);
                    }
                    // log.info("cell:"+cell.getColumnIndex());
                    //cell.setCellValue(str);
                }*//*
            }
            jsonArrayKey.clear();
            workbook.write(bos);*/
//}
//}

/**
 * 行复制功能,
 *
 * @param copyValueFlag true则连同cell的内容一起复制
 * <p>
 * 找到需要插入的行数，并新建一个POI的row对象
 * @param sheet
 * @param rowIndex
 * @return 行复制功能
 * @param wb            工作簿
 * @param fromRow       从哪行开始
 * @param toRow         目标行
 * @param copyValueFlag true则连同cell的内容一起复制
 * <p>
 * 复制单元格
 * @param srcCell
 * @param distCell
 * @param copyValueFlag true则连同cell的内容一起复制
 * <p>
 * 找到需要插入的行数，并新建一个POI的row对象
 * @param sheet
 * @param rowIndex
 * @return 行复制功能
 * @param wb            工作簿
 * @param fromRow       从哪行开始
 * @param toRow         目标行
 * @param copyValueFlag true则连同cell的内容一起复制
 * <p>
 * 复制单元格
 * @param srcCell
 * @param distCell
 * @param copyValueFlag true则连同cell的内容一起复制
 */
    /*private static void copyRow(Row fromRow, Row toRow, boolean copyValueFlag) {
        for (Cell cell : fromRow) {
            Cell newCell = toRow.createCell(cell.getColumnIndex());
            //设置样式
            newCell.setCellStyle(cell.getCellStyle());
            if (copyValueFlag) {
                if (cell.getCellType() == CellType.NUMERIC) {
                    newCell.setCellValue(cell.getNumericCellValue());
                } else if (cell.getCellType() == CellType.STRING) {
                    newCell.setCellValue(cell.getRichStringCellValue());
                } else if (cell.getCellType() == CellType.BOOLEAN) {
                    newCell.setCellValue(cell.getBooleanCellValue());
                } else if (cell.getCellType() == CellType.ERROR) {
                    newCell.setCellErrorValue(cell.getErrorCellValue());
                } else if (cell.getCellType() == CellType.FORMULA) {
                    newCell.setCellFormula(cell.getCellFormula());
                }else {
                    //  newCell.setCellFormula("");
                }
            }
        }
    }

    // jsonArray输入Excel
    private void getByteArrayOutputStreamByJSONArray(String arrayName,
                                                     String serialNoKey,
                                                     JSONArray dataArray,
                                                     List<ExcelParamParseResultPo> paramParsePoList,
                                                     Workbook workbook) throws IOException {

        if (workbook.getNumberOfSheets() > 0) {
            Sheet sheet = workbook.getSheetAt(0);
            String cellLocation = "";
            for (ExcelParamParseResultPo excelParamParseResultPo : paramParsePoList) {
                String cellContent = excelParamParseResultPo.getCellContent();
                if (arrayName.equals(JsonHandler.getArrayName(cellContent))){// 获取list的name
                    cellLocation = excelParamParseResultPo.getCellLocation();
                    break;
                }
            }
            CellAddress cellAddress = new CellAddress(cellLocation);
            Row row = sheet.getRow(cellAddress.getRow());
            // sheet.shiftRows( row.getRowNum(), row.getRowNum()+7, 1, true, false);
            //sheet.shiftRows( row.getRowNum(), sheet.getLastRowNum(), 1, true, false);
            for (int i = 0; i < dataArray.size(); i++) {
                Row newRow = row;

                if (i > 0) {
                    //非第一行新增行，第一行直接进行替换
                    log.info("创建新行");

                    newRow = sheet.createRow(cellAddress.getRow() + i);
                    copyRow(row,newRow,true);
                    //copyRow(workbook, row, newRow, false);
                    sheet.shiftRows( newRow.getRowNum(), sheet.getLastRowNum()+1, 1, true, false);

                }
                for (ExcelParamParseResultPo parseResultPo : paramParsePoList) {
                    CellAddress paramAddress = new CellAddress(parseResultPo.getCellLocation());
                    Cell cell = newRow.getCell(paramAddress.getColumn());
                    if (serialNoKey.equals(parseResultPo.getCellContent())) {
                        cell.setCellValue(i + 1);
                    } else {
                        String cellContent = parseResultPo.getCellContent();
                        // String arrayName = JsonHandler.getArrayName(cellContent);// 获取list的name
                        String arrayFidldName = JsonHandler.getArrayFidldName(cellContent);// 获取list的fieldName
                        JSONObject arrayJsonObject = dataArray.getJSONObject(i);
                        String str = arrayJsonObject.getString(arrayFidldName);
                        log.info("str:   "+str);
                        // String str = dataArray.getJSONObject(i).getString(cellContent);
                        if (str == null){
                            String jsonPath = JsonHandler.getInnerArrayName(cellContent);
                            str = JsonHandler.getSingleJsonValue(arrayJsonObject, jsonPath);
                        }
                        cell.setCellValue(str);
                    }
                }
            }
        }
    }

    private Workbook getWorkbookByFileType(FileTemplatePo fileTemplatePo, byte[] fileTemplateBytes) throws IOException {
        Workbook workbook = null;
        if (FileTemplateConstants.FILE_TYPE_XLSX.equals(fileTemplatePo.getFileType())) {
            workbook = new XSSFWorkbook(new ByteArrayInputStream(fileTemplateBytes));
        } else if (FileTemplateConstants.FILE_TYPE_XLS.equals(fileTemplatePo.getFileType())) {
            workbook = new HSSFWorkbook(new ByteArrayInputStream(fileTemplateBytes));
        }
        return workbook;
    }

    *//**
 * 找到需要插入的行数，并新建一个POI的row对象
 * @param sheet
 * @param rowIndex
 * @return
 *//*
    private XSSFRow createRow(XSSFSheet sheet, Integer rowIndex) {
        XSSFRow row = null;
        if (sheet.getRow(rowIndex) != null) {
            int lastRowNo = sheet.getLastRowNum();
            sheet.shiftRows(rowIndex, lastRowNo, 1);
        }
        row = sheet.createRow(rowIndex);
        return row;
    }


    *//**
 * 行复制功能
 *
 * @param wb            工作簿
 * @param fromRow       从哪行开始
 * @param toRow         目标行
 * @param copyValueFlag true则连同cell的内容一起复制
 *//*
    public static void copyRow(Workbook wb, Row fromRow, Row toRow, boolean copyValueFlag) {
        toRow.setHeight(fromRow.getHeight());

        for (Iterator<Cell> cellIt = fromRow.cellIterator(); cellIt.hasNext(); ) {
            Cell tmpCell = cellIt.next();
            Cell newCell = toRow.createCell(tmpCell.getColumnIndex());
            copyCell(wb, tmpCell, newCell, copyValueFlag);
        }

        Sheet worksheet = fromRow.getSheet();

        for (int i = 0; i < worksheet.getNumMergedRegions(); i++) {
            CellRangeAddress cellRangeAddress = worksheet.getMergedRegion(i);
            if (cellRangeAddress.getFirstRow() == fromRow.getRowNum()) {
                CellRangeAddress newCellRangeAddress = new CellRangeAddress(toRow.getRowNum(),
                        (toRow.getRowNum() + (cellRangeAddress.getLastRow() - cellRangeAddress.getFirstRow())),
                        cellRangeAddress.getFirstColumn(), cellRangeAddress.getLastColumn());
                worksheet.addMergedRegionUnsafe(newCellRangeAddress);
            }
        }
    }


    *//**
 * 复制单元格
 *
 * @param srcCell
 * @param distCell
 * @param copyValueFlag true则连同cell的内容一起复制
 *//*
    public static void copyCell(Workbook wb, Cell srcCell, Cell distCell, boolean copyValueFlag) {
        CellStyle newStyle = wb.createCellStyle();
        CellStyle srcStyle = srcCell.getCellStyle();

        newStyle.cloneStyleFrom(srcStyle);
        newStyle.setFont(wb.getFontAt(srcStyle.getFontIndex()));

        // 样式
        distCell.setCellStyle(newStyle);

        // 内容
        if (srcCell.getCellComment() != null) {
            distCell.setCellComment(srcCell.getCellComment());
        }

        // 不同数据类型处理
        CellType srcCellType = srcCell.getCellTypeEnum();
        distCell.setCellType(srcCellType);

        if (copyValueFlag) {
            if (srcCellType == CellType.NUMERIC) {
                if (DateUtil.isCellDateFormatted(srcCell)) {
                    distCell.setCellValue(srcCell.getDateCellValue());
                } else {
                    distCell.setCellValue(srcCell.getNumericCellValue());
                }
            } else if (srcCellType == CellType.STRING) {
                distCell.setCellValue(srcCell.getRichStringCellValue());
            } else if (srcCellType == CellType.BLANK) {

            } else if (srcCellType == CellType.BOOLEAN) {
                distCell.setCellValue(srcCell.getBooleanCellValue());
            } else if (srcCellType == CellType.ERROR) {
                distCell.setCellErrorValue(srcCell.getErrorCellValue());
            } else if (srcCellType == CellType.FORMULA) {
                distCell.setCellFormula(srcCell.getCellFormula());
            }
        }
    }*/


//}
// 一代征信报告
//{"askAt":"2016.08.26 14:32:22","reason":"贷款审批","checkDetails":[{"reason":"贷款审批","checkedAt":"2016.07.25","operator":"GX"},{"reason":"贷款审批","checkedAt":"2016.02.15","operator":"VZ"},{"reason":"贷款审批","checkedAt":"2015.09.06","operator":"ZX"},{"reason":"贷款审批","checkedAt":"2015.08.31","operator":"YC"},{"reason":"贷款审批","checkedAt":"2015.08.28","operator":"BV"},{"reason":"贷款审批","checkedAt":"2015.08.26","operator":"TK"},{"reason":"贷款审批","checkedAt":"2015.08.24","operator":"IL"},{"reason":"贷款审批","checkedAt":"2015.07.24","operator":"GX"},{"reason":"贷款审批","checkedAt":"2015.07.23","operator":"YC"},{"reason":"信用卡审批","checkedAt":"2015.06.08","operator":"QL"},{"reason":"信用卡审批","checkedAt":"2015.06.01","operator":"GG"},{"reason":"信用卡审批","checkedAt":"2015.05.14","operator":"LY"},{"reason":"贷款审批","checkedAt":"2015.05.12","operator":"ZE"}],"whoWasChecked":"杨光","loans":[{"finAt":"2016年04月18日","businessNo":"X","amount":"99,000","speBusinesses":[{"amount":"71,500","changeMonths":"0","detail":"--","type":"提前还款（全部）","startAt":"2016.04.18"}],"rate":"按月归还","organization":"信托投资公司“SH”","guarantee":"信用/免担保","periods":"36","currency":"人民币","type":"个人消费贷款","startAt":"2015年06月16日","status":"结清"},{"amount":"150,000","speBusinesses":[{"amount":"120,115","changeMonths":"-27","detail":"--","type":"提前还款（全部）","startAt":"2016.05.11"}],"overdueRecordSummary":{"records":[{"amount":"5,500","overdueMonthsNum":"1","overdueMonth":"2016.03"},{"amount":"--","overdueMonthsNum":"--","overdueMonth":"--"}],"endAt":"2016年05月","startAt":"2015年07月"},"guarantee":"信用/免担保","type":"其他贷款","finAt":"2016年05月11日","businessNo":"X","rate":"按月归还","organization":"商业银行“GX”","periods":"36","currency":"人民币","startAt":"2015年07月27日","status":"结清"},{"finAt":"2016年05月20日","businessNo":"X","amount":"100,000","rate":"按月归还","organization":"商业银行“BV”","guarantee":"信用/免担保","periods":"48","currency":"人民币","type":"其他贷款","startAt":"2015年09月02日","status":"结清"}],"profile":{"birthday":"1989.08.31","connubialInfo":{"idType":"--","name":"--","tel":"--","compony":"--"},"education":"大学专科和专科学校","gender":"女性","livedInfos":[{"address":"北京朝阳常营乡连心园小区42号楼6-602号","updateAt":"2015.07.27","status":"自置"},{"address":"直辖市北京市朝阳区常营乡连心小区35#楼六单元602","updateAt":"2015.06.16","status":"亲属楼宇"},{"address":"北京市朝阳区常营乡连心园小区４２号楼６单元６０２","updateAt":"2015.05.18","status":"自置"},{"address":"北京市朝阳区黑庄户乡黑果宿舍275号","updateAt":"2013.07.03","status":"亲属楼宇"},{"address":"中国朝阳区黑庄户家属院75号(郝荣凤转杨光)","updateAt":"2012.05.22","status":"未知"}],"postAddr":"北京市北京市通州区台湖镇光机电一体化产业基地北京邮件综合","mobile":"13810784473","experiences":[{"sortNo":"1","no":"1","address":"--","level":"无","jobNature":"商业、服务业人员","startedAt":"2008","updateAt":"2015.09.02","company":"中国邮政集团公司北京市邮区中心局","industry":"交通运输、仓储和邮政业","title":"--"},{"sortNo":"2","no":"2","address":"北京通州区台湖镇机电一体化产业基地北京邮件综合处理中心","level":"--","jobNature":"办事人员和有关人员","startedAt":"--","updateAt":"2015.07.27","company":"北京邮区中心局","industry":"居民服务和其他服务业","title":"一般员工"},{"sortNo":"3","no":"3","address":"--","level":"--","jobNature":"--","startedAt":"--","updateAt":"2014.09.04","company":"北京东方慧博人力资源顾问有限公司","industry":"--","title":"--"},{"sortNo":"4","no":"4","address":"中国通州区台湖镇光机电一体化产业基地","level":"--","jobNature":"办事人员和有关人员","startedAt":"--","updateAt":"2012.05.22","company":"北京邮件处理中心","industry":"--","title":"--"},{"sortNo":"5","no":"5","address":"--","level":"--","jobNature":"--","startedAt":"--","updateAt":"2008.12.01","company":"北京同力达通信服务有限公司","industry":"--","title":"--"}],"isMarried":"已婚","officeTel":"01057917200","qualification":"其他","registerAddr":"直辖市北京市朝阳区常营乡常营八队1973号","homeTel":"85385846"},"destroyedCrtCardCollection":{"amount":"52,600","companys":"4","usedAmount":"5,805","organizations":"4","accounts":"4","maxAmount":"24,000","leastAmount":"600","avg6monthPay":"9,038"},"type":"个人信用报告","IDType":"身份证","createAt":"2016.08.26 14:32:23","operator":"xw*r001*86","IDNum":"110105198908318828","serialNo":"2016082600003133876599","socialSecuritys":[{"unit":"--","ginsengto":"北京市东城区","monthAmount":"259","cause":"--","personageCostNumber":"3,245","countMonth":"--","insuredDate":"2008.10.01","messageUpdateDate":"2011.03.01","jobMonth":"2008.10","status":"参保缴费"}],"creditCards":[{"amountType":"授信额度","last24Status":"NNNNNNNNNNNNNNNNNNNNNNNN","lastPayedAt":"2016.06.27","amount":"18,000","currOverdue":"0","payAt":"2016.07.26","accountCategory":"人民币账户","currOverduePeriods":"0","guarantee":"信用/免担保","avgUsed6Months":"5,782","used":"4,965","sharedAmountType":"共享授信额度","scheduledPayed":"1,318","startRt":"2014年08月","finAt":"2016年07月26日","businessNo":"X","sharedAmount":"18,000","organization":"商业银行“NY”","maxAmount":"17,977","startAt":"2010年10月30日","payed":"1,700","endRt":"2016年07月","status":"正常"},{"amountType":"授信额度","last24Status":"*****N******************","lastPayedAt":"2014.12.31","amount":"600","currOverdue":"0","payAt":"2016.07.16","accountCategory":"人民币账户","currOverduePeriods":"0","guarantee":"信用/免担保","avgUsed6Months":"0","used":"0","sharedAmountType":"共享授信额度","scheduledPayed":"0","startRt":"2014年08月","finAt":"2016年07月16日","businessNo":"X","sharedAmount":"600","organization":"商业银行“HG”","maxAmount":"499","startAt":"2012年05月22日","payed":"0","endRt":"2016年07月","status":"正常"},{"amountType":"授信额度","last24Status":"NNNNNNNNNNNNNNNNNNNNNNNN","lastPayedAt":"2016.05.12","amount":"10,000","currOverdue":"0","payAt":"2016.07.18","accountCategory":"人民币账户","currOverduePeriods":"0","guarantee":"信用/免担保","avgUsed6Months":"2,452","used":"0","sharedAmountType":"共享授信额度","scheduledPayed":"0","startRt":"2014年08月","finAt":"2016年07月18日","businessNo":"X","sharedAmount":"10,000","organization":"商业银行“MH”","maxAmount":"9,912","startAt":"2013年07月03日","payed":"0","endRt":"2016年07月","status":"正常"},{"amountType":"授信额度","last24Status":"/////////*NNNNNNNNNNNNNN","lastPayedAt":"2016.06.27","amount":"24,000","currOverdue":"0","payAt":"2016.07.18","accountCategory":"人民币账户","currOverduePeriods":"0","guarantee":"信用/免担保","avgUsed6Months":"807","used":"840","sharedAmountType":"共享授信额度","scheduledPayed":"793","startRt":"2014年08月","finAt":"2016年07月18日","businessNo":"X","sharedAmount":"24,000","organization":"商业银行“LY”","maxAmount":"1,192","startAt":"2015年05月18日","payed":"800","endRt":"2016年07月","status":"正常"}],"overDueInfoCollection":{"crdCardAccounts":"0","crdCardMaxAmount":"0","loanNum":"1","loanMonth":"1","qcCardAccounts":"0","crdCardMaxMonths":"0","qcCardMaxAmount":"0","crdCardMonth":"0","loanMaxAmount":"5,500","loanMaxMonths":"1","qcCardMaxMonths":"0","qcCardMonth":"0"},"organization":"廊坊银行股份有限公司营业部","houseFundPayedRecord":[{"no":"1","amount":"1,004","lastPayedAt":"2016.07","address":"北京市市辖区东城区","payAt":"2014.09.04","updateAt":"2016.07.01","firstPayedAt":"--","compony":"北京东方慧博人力资源顾问股份有限公司","personalPercent":"12%","componyPercent":"12%","status":"缴交"},{"no":"2","amount":"900","lastPayedAt":"2015.05","address":"北京市市辖区东城区","payAt":"2008.10.23","updateAt":"2015.05.01","firstPayedAt":"--","compony":"北京同力达通信服务有限公司","personalPercent":"12%","componyPercent":"12%","status":"销户"}],"checkedRecord":{"last2YearsAfterLoaned":"13","lastMonthCheckBySelf":"0","last2YearsVIPNameCheck":"0","lastMonthCardCheckOrgs":"0","lastMonthLoanCheckOrgs":"0","last2YearsQualificationCheck":"0","lastMonthLoanCheck":"0","lastMonthCardCheck":"0"},"creditInfo":{"firstQuasiCardGotAt":"0","quasiCardsNum":"2010.10","selfDeclaredNum":"--","firstCardGotAt":"4","objectionOne":"0","otherLoansNum":"0","firstLoanGotAt":"3","houseLoansNum":"0","accountsNum":"2015.06","objection":"0"}}
// 二代征信报告
//[{"amount":"1,000","updateDate":"2018.10.01","orgName":"北京市","bizType":"公积金","payState":"缴交","payAmount":"--","payMouth":"2008.11","ownPercent":"12%","firstPayDate":"2008.08","belongOrg":"北京银行","companyPercent":"12%","fundName":"--","payDate":"2008.08.01"}],"postPayRecords":[{"keepAccountsDate":"2018.10","dissentTags":[],"currentPayState":"欠费","bizType":"固定电话","sectionPaymentRecord":{"beginDate":"2016年11月","paymentRecords":[
// {"stateApr":"N","year":"2018","stateMay":"N","amountJul":"N","amountDec":"N","amountFeb":"N","amountJun":"N","amountNov":"N","amountJan":"N","stateNov":"","stateJan":"N","stateMar":"N","stateOct":"2","amountSept":"N","stateDec":"","stateJul":"N","amountMay":"N","stateFeb":"N","stateJun":"N","amountOct":"N","amountApr":"N","amountMar":"N","stateSept":"1","stateAug":"N","amountAug":"N"},
// {"stateApr":"","year":"2016","stateMay":"","amountJul":"","amountDec":"","amountFeb":"","amountJun":"","amountNov":"","amountJan":"","stateNov":"N","stateJan":"","stateMar":"","stateOct":"","amountSept":"","stateDec":"N","stateJul":"","amountMay":"","stateFeb":"","stateJun":"","amountOct":"","amountApr":"","amountMar":"","stateSept":"","stateAug":"","amountAug":""}],"endDate":"2018年10月"},"oneselfDeclares":[],"organization":"中国电信集团江苏省电信公司","organizationIlluminates":[],"currentArrearsAmount":"1,000","bizOpenDate":"2009.08.17","specialTags":[]},{"keepAccountsDate":"2018.10","dissentTags":[],"currentPayState":"欠费","bizType":"--","oneselfDeclares":[],"organization":"人力资源和社会保障部","organizationIlluminates":[],"currentArrearsAmount":"200","bizOpenDate":"--","specialTags":[]},{"keepAccountsDate":"2018.10","dissentTags":[],"currentPayState":"正常","bizType":"移动电话","sectionPaymentRecord":{"beginDate":"2016年11月","paymentRecords":[
// {"stateApr":"N","year":"2018","stateMay":"N","amountJul":"N","amountDec":"N","amountFeb":"N","amountJun":"N","amountNov":"N","amountJan":"N","stateNov":"","stateJan":"N","stateMar":"N","stateOct":"N","amountSept":"N","stateDec":"","stateJul":"N","amountMay":"1","stateFeb":"N","stateJun":"N","amountOct":"N","amountApr":"N","amountMar":"N","stateSept":"N","stateAug":"N","amountAug":"N"},
// {"stateApr":"","year":"2016","stateMay":"","amountJul":"","amountDec":"","amountFeb":"","amountJun":"","amountNov":"","amountJan":"","stateNov":"N","stateJan":"","stateMar":"","stateOct":"","amountSept":"","stateDec":"N","stateJul":"","amountMay":"","stateFeb":"","stateJun":"","amountOct":"","amountApr":"","amountMar":"","stateSept":"","stateAug":"","amountAug":""}
