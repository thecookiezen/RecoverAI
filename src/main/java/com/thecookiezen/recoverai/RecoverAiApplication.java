package com.thecookiezen.recoverai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
class RecoverAiApplication {
    public static void main(String[] args) {
        SpringApplication.run(RecoverAiApplication.class, args);
    }
}