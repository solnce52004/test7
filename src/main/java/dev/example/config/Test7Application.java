package dev.example.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


//@EnableAspectJAutoProxy(proxyTargetClass=true) // - отследить ошибки Async

@SpringBootApplication(scanBasePackages = "dev")
@ComponentScan(basePackages = "dev.*")
@EntityScan(basePackages = "dev.*")
@EnableJpaRepositories(basePackages = "dev.*")
@PropertySource(value = {"classpath:application.yaml"})

public class Test7Application extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(Test7Application.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Test7Application.class);
    }
}
