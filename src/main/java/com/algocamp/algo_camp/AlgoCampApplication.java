package com.algocamp.algo_camp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.algocamp")  // 新增这一行
public class AlgoCampApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlgoCampApplication.class, args);
    }
}