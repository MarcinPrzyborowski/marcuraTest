package com.example.marcuratest.config;

import com.example.marcuratest.dependency.ExchangeRateClient;
import feign.*;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;

import feign.slf4j.Slf4jLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig {

    @Bean
    ExchangeRateClient exchangeRateClient(
            @Value("${exchange.rate.api.url}") final String url,
            @Value("${exchange.rate.api.key}") final String key
    ) {
        return Feign.builder()
                .client(new OkHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .logger(new Slf4jLogger(ExchangeRateClient.class))
                .requestInterceptor(new AuthInterceptor(key))
                .logLevel(Logger.Level.FULL)
                .target(ExchangeRateClient.class, url);
    }

    @Bean
    public Contract feignContract() {
        return new Contract.Default();
    }

    static class AuthInterceptor implements RequestInterceptor {

        final String accessKey;

        AuthInterceptor(String accessKey) {
            this.accessKey = accessKey;
        }

        @Override
        public void apply(RequestTemplate requestTemplate) {
            requestTemplate.query("access_key", accessKey);
        }
    }
}
