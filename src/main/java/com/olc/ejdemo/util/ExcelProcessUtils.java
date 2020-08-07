package com.olc.ejdemo.util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Excel 处理工具
 *
 * @Description
 * @Author ex-oulingcheng
 * @Date 2020/7/13 14:03
 */
@Slf4j
public class ExcelProcessUtils {

    private final static String BASE_PRE = "$.";
    private final static Pattern compile = Pattern.compile("(\\[[^\\]]*\\])");

    private final static Pattern fieldPatternAll = Pattern.compile("\\$\\{(.+?)}");
    private final static Pattern listPatternAll = Pattern.compile("\\#\\{(.+?)}");
    private final static String fieldMatchAll = "\\$\\{[^\\}]+\\}";
    private final static String listMatchAll = "\\#\\{[^\\}]+\\}";

    /**
     * 行复制功能
     *
     * @param wb            工作簿
     * @param fromRow       从哪行开始
     * @param copyValueFlag true则连同cell的内容一起复制
     */
    public static void copyRow(Workbook wb, Row fromRow, boolean copyValueFlag) {

        for (Iterator<Cell> cellIt = fromRow.cellIterator(); cellIt.hasNext(); ) {
            Cell tmpCell = cellIt.next();
            if (listPatternAll.matcher(tmpCell.getStringCellValue()).find()){
                Cell newCell = fromRow.getCell(tmpCell.getColumnIndex()+1);
                copyCell(wb, tmpCell, newCell, copyValueFlag);
            }

        }
    }


    /**
     * 复制单元格
     *
     * @param wb            wb
     * @param srcCell       被复制cell
     * @param distCell      复制cell
     * @param copyValueFlag true则连同cell的内容一起复制
     */
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

    /**
     * cell处理
     *
     * @param cell       处理的cell
     * @param jsonObject 所有数据
     */
    public static void cellProcess(Cell cell, JSONObject jsonObject, List<ExcelProcessDto> excelProcessDtos, Row row) {
        String content = getCellStringValue(cell);
        if (fieldPatternAll.matcher(content).find()) {
            // 匹配到字段
            if (cell.getColumnIndex() == 0 || cell.getColumnIndex() == 1) {
                String cellValue = LocalDate.now().toString();
                cell.setCellValue(cellValue);
            }
        } else if (listPatternAll.matcher(content).find()) {
            // 匹配到列表
            int rowNum = row.getRowNum();
            ExcelProcessDto excelProcessDto = new ExcelProcessDto();
            if (CollectionUtils.isEmpty(excelProcessDtos)) {
                excelProcessDto.setFieldName(content);
                excelProcessDto.setRowNum(rowNum);
                excelProcessDto.setCellNum(cell.getColumnIndex());
                excelProcessDtos.add(excelProcessDto);
            } else {
                for (ExcelProcessDto processDto : excelProcessDtos) {
                    if (processDto.getRowNum().equals(rowNum)) {
                        return;
                    }
                }
                excelProcessDto.setFieldName(content);
                excelProcessDto.setRowNum(rowNum);
                excelProcessDto.setCellNum(cell.getColumnIndex());
                excelProcessDtos.add(excelProcessDto);
            }
        }
    }

    /**
     * cell处理
     *
     * @param cell       处理的cell
     * @param code 所有数据
     */
    public static void cellProcess(Cell cell, String code) {
        String stringCellValue = cell.getStringCellValue();
        if (listPatternAll.matcher(stringCellValue).find()) {
            if (stringCellValue.contains("Code")){
                code = "码值-"+code;
            }
            cell.setCellValue(code);
        }
    }

    /**
     * 单个字段处理
     *
     * @param content    匹配到的表格内容
     * @param jsonObject 数据
     * @return
     */
    private static String fieldProcess(String content, JSONObject jsonObject) {
        String jsonValue = getSingleJsonValue(jsonObject, getCellContent(content)); // 数据
        return content.replaceAll(fieldMatchAll, jsonValue);
    }

    /**
     * 集合处理
     *
     * @param content    匹配到的表格内容
     * @param jsonObject 数据
     * @return string
     */
    private static String listProcess(String content, JSONObject jsonObject) {
        String jsonValue = getSingleJsonValue(jsonObject, getInnerArrayName(getCellContent(content)));// 数据
        return content.replaceAll(listMatchAll, jsonValue);
    }

    /**
     * 获取单元格文本
     *
     * @param cell excel单元格
     * @return 单元格文本
     */
    private static String getCellStringValue(Cell cell) {
        switch (cell.getCellTypeEnum()) {
            case _NONE:
                return "";
            case BLANK:
                return "";
            case STRING:
                return cell.getStringCellValue();
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            default:
                return "";
        }
    }

    /**
     * 获取object的集合名称
     *
     * @param cellValue 模板未解析的格子
     * @return string
     */
    private static String getListName(String cellValue) {
        return getArrayName(getCellContent(cellValue));
    }

    /**
     * 获取未解析格子的jsonPath
     *
     * @param cellValue 模板未解析的格子
     * @return string Excel格子{}里面的信息
     */
    private static String getCellContent(String cellValue) {
        return cellValue.substring(cellValue.indexOf("{") + 1, cellValue.indexOf("}"));
    }

    /**
     * 获取内层数组的jsonPath名称
     *
     * @param cellContent 内层数组模板名称
     * @return
     */
    private static String getInnerArrayName(String cellContent) {
        int splitIndex = cellContent.indexOf(".") + 1;
        return cellContent.substring(splitIndex);
    }

    /**
     * 获取集合对象名称
     *
     * @param cellContent 内层数组模板名称
     * @return
     */
    private static String getArrayName(String cellContent) {
        String[] split = cellContent.split("\\.");
        return split[0];
    }

    @Setter
    @Getter
    public static class ExcelProcessDto {

        // 内层集合名称
        private String fieldName;
        // 该集合所在行
        private Integer rowNum;
        // 该集合所在列
        private Integer cellNum;

    }

    /**
     * 获取唯一参数的值
     *
     * @param jsonObject json格式的产品信息字符串
     * @param jsonPath   JsonPath表达式
     * @return
     */
    private static String getSingleJsonValue(JSONObject jsonObject, String jsonPath) {
        String jsonEXP = BASE_PRE + jsonPath;
        String obj = "";
        try {
            obj = JSONPath.eval(jsonObject, jsonEXP).toString();
        } catch (Exception e) {
            log.info("jsonPath error :   " + jsonEXP);
        }
        if (compile.matcher(obj).matches()) {
            obj = obj.replace("[", "").replace("]", "");
        }
        return obj;
    }

}

