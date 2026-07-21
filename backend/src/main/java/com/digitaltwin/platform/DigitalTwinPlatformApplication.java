package com.digitaltwin.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Application entry point for the AI-Powered Digital Twin & Interview
 * Readiness Platform backend.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableAsync
public class DigitalTwinPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(DigitalTwinPlatformApplication.class, args);
    }
}
