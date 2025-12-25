package com.project.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.project.platform.mapper")
public class ProjectManagement {
    public static void main(String[] args) {
        SpringApplication.run(ProjectManagement.class, args);
    }
}
