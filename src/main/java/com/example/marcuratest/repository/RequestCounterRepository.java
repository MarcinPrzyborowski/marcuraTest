package com.example.marcuratest.repository;

import com.example.marcuratest.entity.RequestCounterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.Date;
import java.util.Optional;

public interface RequestCounterRepository extends JpaRepository<RequestCounterEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RequestCounterEntity> findFirstByCurrencyFromAndCurrencyToAndDate(String baseCurrency, String targetCurrency, Date date);

}
