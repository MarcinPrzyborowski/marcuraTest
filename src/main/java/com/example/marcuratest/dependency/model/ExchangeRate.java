package com.example.marcuratest.dependency.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Data
public class ExchangeRate {

    private String base;
    private Date date;
    private Map<String, BigDecimal> rates;

}
