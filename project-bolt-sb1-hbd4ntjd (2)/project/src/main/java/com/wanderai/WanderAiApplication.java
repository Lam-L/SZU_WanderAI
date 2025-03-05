package com.wanderai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import com.wanderai.config.FileStorageProperties;

@SpringBootApplication
@EnableConfigurationProperties({
    FileStorageProperties.class
})
@EnableAsync
public class WanderAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(WanderAiApplication.class, args);
    }
}