package com.example.marcuratest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ExchangeResponse {

    private String currencyFrom;
    private String currencyTo;
    private BigDecimal exchange;

}
