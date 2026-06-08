package com.subastaapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SubastaAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(SubastaAppApplication.class, args);
    }
}
