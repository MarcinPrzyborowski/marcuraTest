package com.example.marcuratest.service.impl;

import com.example.marcuratest.dto.ExchangeResponse;
import com.example.marcuratest.entity.ExchangeRateEntity;
import com.example.marcuratest.entity.RequestCounterEntity;
import com.example.marcuratest.entity.SpreadEntity;
import com.example.marcuratest.exceptions.DefaultSpreadNotFoundException;
import com.example.marcuratest.exceptions.ExchangeRateNotFoundException;
import com.example.marcuratest.repository.ExchangeRateRepository;
import com.example.marcuratest.repository.RequestCounterRepository;
import com.example.marcuratest.repository.SpreadRepository;
import com.example.marcuratest.service.ExchangeRateService;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {

    final private ExchangeRateRepository exchangeRateRepository;
    final private SpreadRepository spreadRepository;
    final private RequestCounterRepository requestCounterRepository;

    public ExchangeRateServiceImpl(
            ExchangeRateRepository exchangeRateRepository,
            SpreadRepository spreadRepository,
            RequestCounterRepository requestCounterRepository
    ) {
        this.exchangeRateRepository = exchangeRateRepository;
        this.spreadRepository = spreadRepository;
        this.requestCounterRepository = requestCounterRepository;
    }

    @Override
    @Transactional
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

        Date selectedDate = date.orElse(new Date());
        ExchangeRateEntity fromCurrencyExchange = exchangeRateRepository
                .findFirstByTargetCurrencyAndDate(currencyFrom, selectedDate)
                .orElseThrow(ExchangeRateNotFoundException::new);
        ExchangeRateEntity toCurrencyExchange = exchangeRateRepository
                .findFirstByTargetCurrencyAndDate(currencyTo, selectedDate)
                .orElseThrow(ExchangeRateNotFoundException::new);

        Optional<RequestCounterEntity> requestCounter = requestCounterRepository
                .findFirstByCurrencyFromAndCurrencyToAndDate(currencyFrom, currencyTo, selectedDate);
        RequestCounterEntity requestCounterEntity;
        if (requestCounter.isEmpty()) {
            requestCounterEntity = RequestCounterEntity
                    .builder()
                    .currencyFrom(currencyFrom)
                    .currencyTo(currencyTo)
                    .date(selectedDate)
                    .counter(0L)
                    .build();
        } else {
            requestCounterEntity = requestCounter.get();
        }

        requestCounterEntity.increaseCounter();
        requestCounterRepository.save(requestCounterEntity);

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
