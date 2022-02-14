package com.example.marcuratest.service.impl;

import com.example.marcuratest.dto.ExchangeResponse;
import com.example.marcuratest.entity.ExchangeRateEntity;
import com.example.marcuratest.entity.SpreadEntity;
import com.example.marcuratest.exceptions.DefaultSpreadNotFoundException;
import com.example.marcuratest.exceptions.ExchangeRateNotFoundException;
import com.example.marcuratest.repository.ExchangeRateRepository;
import com.example.marcuratest.repository.SpreadRepository;
import com.example.marcuratest.service.ExchangeRateService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {

    final private ExchangeRateRepository exchangeRateRepository;
    final private SpreadRepository spreadRepository;

    public ExchangeRateServiceImpl(ExchangeRateRepository exchangeRateRepository, SpreadRepository spreadRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
        this.spreadRepository = spreadRepository;
    }

    @Override
    public ExchangeResponse get(String currencyFrom, String currencyTo, Optional<Date> date) {

        Set<String> currencies = Set.of(currencyFrom, currencyTo);
        Set<SpreadEntity> spreads = spreadRepository.findByCurrencyAndWithDefault(currencies);

        SpreadEntity defaultSpread = spreads
                .stream()
                .filter(SpreadEntity::isDefault)
                .findFirst()
                .orElseThrow(DefaultSpreadNotFoundException::new);
        SpreadEntity fromCurrencySpread = spreads
                .stream().filter(s -> s.getCurrency().equalsIgnoreCase(currencyFrom))
                .findFirst()
                .orElse(defaultSpread);
        SpreadEntity toCurrencySpread = spreads
                .stream()
                .filter(s -> s.getCurrency().equalsIgnoreCase(currencyTo))
                .findFirst()
                .orElse(defaultSpread);

        ExchangeRateEntity fromCurrencyExchange = exchangeRateRepository
                .findFirstByTargetCurrencyAndDate(currencyFrom, date.orElse(new Date()))
                .orElseThrow(ExchangeRateNotFoundException::new);
        ExchangeRateEntity toCurrencyExchange = exchangeRateRepository
                .findFirstByTargetCurrencyAndDate(currencyTo, date.orElse(new Date()))
                .orElseThrow(ExchangeRateNotFoundException::new);

        BigDecimal toCurrencyRate = toCurrencyExchange.getRate();
        BigDecimal fromCurrencyRate = fromCurrencyExchange.getRate();

        BigDecimal dividingRateResults = toCurrencyRate.divide(fromCurrencyRate, MathContext.DECIMAL128);

        BigDecimal hundred = BigDecimal.valueOf(100);
        BigDecimal maxSpreadValue = BigDecimal.valueOf(Math.max(toCurrencySpread.getSpread(),fromCurrencySpread.getSpread()));
        BigDecimal spreadResult = hundred.subtract(maxSpreadValue).divide(hundred);
        BigDecimal result = dividingRateResults.multiply(spreadResult);

        return new ExchangeResponse(currencyFrom, currencyTo, result);
    }

}
