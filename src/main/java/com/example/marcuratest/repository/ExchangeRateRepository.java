package com.example.marcuratest.repository;

import com.example.marcuratest.entity.ExchangeRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRateEntity, Long> {

    Set<ExchangeRateEntity> findAllByBaseCurrencyAndDate(String baseCurrency, Date date);

    Optional<ExchangeRateEntity> findFirstByTargetCurrencyAndDate(String targetCurrency, Date date);

}
