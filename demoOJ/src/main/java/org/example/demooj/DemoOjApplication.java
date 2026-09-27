package org.example.demooj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.example.demooj.mapper")
public class DemoOjApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoOjApplication.class, args);
    }

}
