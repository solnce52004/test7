package dev.example.test7.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@ComponentScan(basePackages = "dev")
//@EnableWebMvc
//@PropertySource("classpath:application.properties")
public class Test7Application {

    public static void main(String[] args) {
        SpringApplication.run(Test7Application.class, args);
    }

}
