package com.example.marcuratest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MarcuraTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarcuraTestApplication.class, args);
    }

}
