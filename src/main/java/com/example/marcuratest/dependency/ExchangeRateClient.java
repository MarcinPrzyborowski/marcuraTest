package com.example.marcuratest.dependency;

import com.example.marcuratest.dependency.model.ExchangeRate;

import feign.RequestLine;
import org.springframework.stereotype.Service;

public interface ExchangeRateClient {

    @RequestLine("GET /latest")
    ExchangeRate getExchangeRate();

}
