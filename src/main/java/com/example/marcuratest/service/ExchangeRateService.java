package com.example.marcuratest.service;

import com.example.marcuratest.dto.ExchangeResponse;

import java.util.Date;
import java.util.Optional;

public interface ExchangeRateService {

    ExchangeResponse get(String baseCurrency, String targetCurrency, Optional<Date> date);

}
