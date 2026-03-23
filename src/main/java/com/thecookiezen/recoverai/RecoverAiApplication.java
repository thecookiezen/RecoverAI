package com.thecookiezen.recoverai;

import com.thecookiezen.archiledger.agenticmemory.AgenticMemoryProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = {"com.thecookiezen.recoverai", "com.thecookiezen.archiledger", "com.thecookiezen.agenticmemory"})
@ConfigurationPropertiesScan
@EnableConfigurationProperties(AgenticMemoryProperties.class)
class RecoverAiApplication {
    public static void main(String[] args) {
        SpringApplication.run(RecoverAiApplication.class, args);
    }
}