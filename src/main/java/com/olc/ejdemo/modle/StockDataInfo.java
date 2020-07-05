package com.olc.ejdemo.modle;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class StockDataInfo {

    private static final long serialVersionUID = 1L;
    private Long id;

    private String stockCode;

    private String stockName;

    private BigDecimal priceNew = BigDecimal.ZERO;

    private BigDecimal stockChange = BigDecimal.ZERO;

    private BigDecimal peRatio = BigDecimal.ZERO;

    private BigDecimal becomeAmount = BigDecimal.ZERO;

    private BigDecimal currentMarketValue = BigDecimal.ZERO;

    private BigDecimal mainAmount = BigDecimal.ZERO;

    private BigDecimal mainRatio = BigDecimal.ZERO;

    private BigDecimal superAmount = BigDecimal.ZERO;

    private BigDecimal superRatio = BigDecimal.ZERO;

    private BigDecimal bigAmount = BigDecimal.ZERO;

    private BigDecimal bigRatio = BigDecimal.ZERO;

    private BigDecimal middleAmount = BigDecimal.ZERO;

    private BigDecimal middleRatio = BigDecimal.ZERO;

    private BigDecimal litterAmount = BigDecimal.ZERO;

    private BigDecimal litterRatio = BigDecimal.ZERO;

    private Integer stockPage;

    private Date countTime;

    private String timeVersion;

    private Long crawlerVersion;

    private Date createTime;

    private Date updateTime;

    private String someinfo;
}