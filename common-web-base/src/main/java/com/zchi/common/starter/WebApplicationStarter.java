package com.zchi.common.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * WebApplicationStarter
 * web启动类
 */
@SpringBootApplication(scanBasePackages = {"com.htjc"})
public class WebApplicationStarter
    extends SpringBootServletInitializer {

    @Override protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(WebApplicationStarter.class);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(WebApplicationStarter.class, args);
    }

}
