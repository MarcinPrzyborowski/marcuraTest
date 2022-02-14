package com.example.marcuratest.crons;

import com.example.marcuratest.service.ExchangeRateUpdater;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ExchangeRateUpdateJob {

    private final ExchangeRateUpdater updater;

    public ExchangeRateUpdateJob(ExchangeRateUpdater updater) {
        this.updater = updater;
    }

    @Scheduled(cron = "${exchange.rate.cron}", zone = "${exchange.rate.timezone}")
    public void schedule() {
        updater.update();
    }

}
