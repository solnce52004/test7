package dev.example.test7.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

//@EnableWebMvc
//@EnableAspectJAutoProxy(proxyTargetClass=true) // - отследить ошибки Async

@SpringBootApplication(
        scanBasePackages = "dev",
        exclude = {
//                DataSourceAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class,//иначе 500 при отправке запросов
                JpaRepositoriesAutoConfiguration.class//иначе не видит репы
        }
)
@ComponentScan(basePackages = "dev")
@EnableJpaRepositories("dev.example.test7.repo")//обязательно!
@EntityScan("dev.example.test7.entities")

public class Test7Application extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(Test7Application.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Test7Application.class);
    }
}
