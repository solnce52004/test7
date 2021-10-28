package dev.example.test7.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@ComponentScan(basePackages = "dev")
//@EnableWebMvc
//@EnableAspectJAutoProxy(proxyTargetClass=true) // - отследить ошибки Async

public class Test7Application {

    public static void main(String[] args) {
        SpringApplication.run(Test7Application.class, args);
    }

}
