package com.example.marcuratest.service.impl;

import com.example.marcuratest.dependency.ExchangeRateClient;
import com.example.marcuratest.dependency.model.ExchangeRate;
import com.example.marcuratest.entity.ExchangeRateEntity;
import com.example.marcuratest.repository.ExchangeRateRepository;
import com.example.marcuratest.service.ExchangeRateUpdater;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ExchangeRateUpdaterImpl implements ExchangeRateUpdater {

    private final ExchangeRateClient exchangeRateClient;
    private final ExchangeRateRepository exchangeRateRepository;

    public ExchangeRateUpdaterImpl(ExchangeRateClient exchangeRateClient, ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateClient = exchangeRateClient;
        this.exchangeRateRepository = exchangeRateRepository;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public synchronized void update() {
        ExchangeRate exchangeRate = exchangeRateClient.getExchangeRate();
        Map<String, BigDecimal> rates = exchangeRate.getRates();

        Set<ExchangeRateEntity> exchangeRates = exchangeRateRepository.findAllByBaseCurrencyAndDate(
                exchangeRate.getBase(),
                new Date()
        );

        for (ExchangeRateEntity exchangeRateEntity : exchangeRates) {
            String key = exchangeRateEntity.getTargetCurrency();
            var rate = rates.get(key);
            if (rate != null) {
                exchangeRateEntity.setRate(rate);
                rates.remove(key);
            }
        }

        Set<ExchangeRateEntity> exchangeRatesToCreate = rates
                .entrySet()
                .stream()
                .map(e -> this.mapExchangeRate(exchangeRate.getBase(), exchangeRate.getDate(), e.getKey(), e.getValue()))
                .collect(Collectors.toSet());

        exchangeRates.addAll(exchangeRatesToCreate);

        exchangeRateRepository.saveAll(exchangeRates);
    }


    private ExchangeRateEntity mapExchangeRate(String base, Date date, String to, BigDecimal rate) {
        return ExchangeRateEntity.builder().baseCurrency(base).date(date).targetCurrency(to).rate(rate).build();
    }
}
