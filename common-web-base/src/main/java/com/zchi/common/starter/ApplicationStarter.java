package com.zchi.common.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ApplicationStarter
 * app启动类
 */
@SpringBootApplication(scanBasePackages = {"com.htjc"})
public class ApplicationStarter {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationStarter.class, args);
    }
}
