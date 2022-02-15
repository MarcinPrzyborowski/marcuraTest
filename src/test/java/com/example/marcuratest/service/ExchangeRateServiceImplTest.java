package com.example.marcuratest.service;

import com.example.marcuratest.entity.ExchangeRateEntity;
import com.example.marcuratest.entity.SpreadEntity;
import com.example.marcuratest.exceptions.DefaultSpreadNotFoundException;
import com.example.marcuratest.exceptions.ExchangeRateNotFoundException;
import com.example.marcuratest.repository.ExchangeRateRepository;
import com.example.marcuratest.repository.RequestCounterRepository;
import com.example.marcuratest.repository.SpreadRepository;
import com.example.marcuratest.service.impl.ExchangeRateServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ExchangeRateServiceImplTest {

    @Mock
    ExchangeRateRepository exchangeRateRepository;

    @Mock
    RequestCounterRepository requestCounterRepository;

    @Mock
    SpreadRepository spreadRepository;

    @InjectMocks
    ExchangeRateServiceImpl exchangeRateService;

    @Test(expected = DefaultSpreadNotFoundException.class)
    public void getThrowExceptionWhenDefaultSpreadIsNull() {
        exchangeRateService.get("EUR", "BRc", Optional.empty());
    }

    @Test(expected = ExchangeRateNotFoundException.class)
    public void getWillThrowExceptionWhenRateNotFoundByDate()
    {
        SpreadEntity spreadEntity = mock(SpreadEntity.class);
        when(spreadEntity.isDefault()).thenReturn(true);
        when(spreadEntity.getCurrency()).thenReturn("EUR");
        when(spreadRepository.findByCurrencyAndWithDefault(anySet())).thenReturn(Set.of(spreadEntity));

        Date value = new Date();
        when(exchangeRateRepository.findFirstByTargetCurrencyAndDate("EUR", value)).thenReturn(Optional.empty());

        exchangeRateService.get("EUR", "BRc", Optional.of(value));
    }

    @Test
    public void getWillReturnExchangeResponse()
    {
        SpreadEntity defaultSpread = mock(SpreadEntity.class);
        when(defaultSpread.isDefault()).thenReturn(true);
        when(defaultSpread.getCurrency()).thenReturn("DEFAULT");

        SpreadEntity fromSpread = mock(SpreadEntity.class);
        when(fromSpread.isDefault()).thenReturn(false);
        when(fromSpread.getCurrency()).thenReturn("EUR");

        SpreadEntity toSpread = mock(SpreadEntity.class);
        when(toSpread.isDefault()).thenReturn(false);
        when(toSpread.getCurrency()).thenReturn("PLN");

        when(spreadRepository.findByCurrencyAndWithDefault(anySet())).thenReturn(Set.of(defaultSpread,fromSpread,toSpread));

        Date date = new Date();

        ExchangeRateEntity fromCurrencyExchange = mock(ExchangeRateEntity.class);
        when(fromCurrencyExchange.getRate()).thenReturn(BigDecimal.valueOf(1));

        ExchangeRateEntity toCurrencyExchange = mock(ExchangeRateEntity.class);
        when(toCurrencyExchange.getRate()).thenReturn(BigDecimal.valueOf(4.54));

        when(exchangeRateRepository.findFirstByTargetCurrencyAndDate(eq("EUR"), any(Date.class))).thenReturn(Optional.of(fromCurrencyExchange));
        when(exchangeRateRepository.findFirstByTargetCurrencyAndDate(eq("PLN"), any(Date.class))).thenReturn(Optional.of(toCurrencyExchange));
        when(requestCounterRepository.findFirstByCurrencyFromAndCurrencyToAndDate(eq("EUR"), eq("PLN"), eq(date))).thenReturn(Optional.empty());

        var x = exchangeRateService.get("EUR", "PLN", Optional.of(date));

        assertSame("EUR", x.getCurrencyFrom());
        assertSame("PLN", x.getCurrencyTo());
        assertEquals(BigDecimal.valueOf(4.540).setScale(10), x.getExchange().setScale(10));
    }
}
