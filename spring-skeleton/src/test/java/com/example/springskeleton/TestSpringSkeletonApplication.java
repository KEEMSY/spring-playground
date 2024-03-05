package com.example.springskeleton;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestSpringSkeletonApplication {
    public static void main(String[] args) {
        SpringApplication.from(SpringSkeletonApplication::main).with(TestSpringSkeletonApplication.class).run(args);
    }

}
