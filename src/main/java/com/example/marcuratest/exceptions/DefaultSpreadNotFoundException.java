package com.example.marcuratest.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class DefaultSpreadNotFoundException extends RuntimeException {
    public DefaultSpreadNotFoundException() {
        super("Default spread is not defined");
    }
}
