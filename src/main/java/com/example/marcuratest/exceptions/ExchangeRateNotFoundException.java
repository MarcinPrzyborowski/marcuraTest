package com.example.marcuratest.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ExchangeRateNotFoundException extends RuntimeException {
    public ExchangeRateNotFoundException() {
        super("The rate does not exist for the selected date");
    }
}
