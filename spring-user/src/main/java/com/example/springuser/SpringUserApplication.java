package com.example.springuser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class SpringUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringUserApplication.class, args);
    }

}
