package com.example.autoproject;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.autoproject.mapper")
public class AutoProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoProjectApplication.class, args);
    }
}
