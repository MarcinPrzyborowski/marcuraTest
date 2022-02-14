package com.example.marcuratest.controller;

import com.example.marcuratest.dto.ExchangeResponse;
import com.example.marcuratest.service.ExchangeRateService;
import com.example.marcuratest.service.ExchangeRateUpdater;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;


@RestController
@RequestMapping("/exchange")
public class ExchangeController {

    private final ExchangeRateUpdater exchangeRateUpdater;
    private final ExchangeRateService exchangeRateService;

    public ExchangeController(ExchangeRateUpdater exchangeRateUpdater, ExchangeRateService exchangeRateService) {
        this.exchangeRateUpdater = exchangeRateUpdater;
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping()
    public ExchangeResponse test(
            @RequestParam String from,
            @RequestParam String to,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<Date> date
    ) {
        return exchangeRateService.get(from, to, date);
    }

    @PutMapping()
    public void update() {
        exchangeRateUpdater.update();
    }

}
