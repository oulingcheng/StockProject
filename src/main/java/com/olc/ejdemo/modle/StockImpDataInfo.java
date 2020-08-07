package com.olc.ejdemo.modle;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Accessors(chain = true)
@TableName("stock_imp_data_info")
public class StockImpDataInfo {
    private static final long serialVersionUID = 1L;
    private int id;

    private String stockCode;

    private String stockName;

    private BigDecimal priceNew = BigDecimal.ZERO;

    private BigDecimal mainAmountThan = BigDecimal.ZERO;

    private BigDecimal asFlowThan = BigDecimal.ZERO;

    private BigDecimal peRatio = BigDecimal.ZERO;

    private BigDecimal becomeAmount = BigDecimal.ZERO;

    private BigDecimal currentMarketValue = BigDecimal.ZERO;

    private BigDecimal stockChange = BigDecimal.ZERO;

    private BigDecimal mainAmount = BigDecimal.ZERO;

    private BigDecimal superAmount = BigDecimal.ZERO;

    private BigDecimal bigAmount = BigDecimal.ZERO;

    private BigDecimal middleAmount = BigDecimal.ZERO;

    private BigDecimal litterAmount = BigDecimal.ZERO;

    private String one;// 最近5天涨跌行情，-1 跌，1涨
    private String two;
    private String three;
    private String four;
    private String five;
    private String now;

    private Integer statusCode;

    private Date createTime;

    private Date updateTime;
}