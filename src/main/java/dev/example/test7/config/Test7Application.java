package dev.example.test7.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "dev")
public class Test7Application {

    public static void main(String[] args) {
        SpringApplication.run(Test7Application.class, args);
    }

}
