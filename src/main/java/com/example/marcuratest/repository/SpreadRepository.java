package com.example.marcuratest.repository;

import com.example.marcuratest.entity.SpreadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface SpreadRepository extends JpaRepository<SpreadEntity, Long> {

    @Query("select s from SpreadEntity s where s.currency in :currencies or s.isDefault = true")
    Set<SpreadEntity> findByCurrencyAndWithDefault(@Param("currencies") Set<String> currencies);

}
