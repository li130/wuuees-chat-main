package com.wuuees.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChatApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ChatApplication.class);
        // 不作为web应用
        application.setWebApplicationType(WebApplicationType.NONE);
        application.run(args);
        System.out.println("ChatApplication started!");
    }
}