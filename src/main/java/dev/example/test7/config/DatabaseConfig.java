package dev.example.test7.config;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = "dev")
@PropertySource(value = {"classpath:application.yaml"})

public class DatabaseConfig {

    @Bean(name = "entityManagerFactory") //обязательно!
    public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setPackagesToScan("dev.example.test7.entity");
        sessionFactory.setAnnotatedPackages("dev.example.test7.repo");
        sessionFactory.setHibernateProperties(hibernateProperties());

        return sessionFactory;
    }

    @Bean
    public Properties hibernateProperties() {
        final Properties properties = new Properties();
        final InputStream in = DatabaseConfig.class
                .getClassLoader()
                .getResourceAsStream("hibernate.properties");
        try {
            if (in != null) {
                properties.load(in);
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties;
    }

    @Bean
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        return new HibernateTransactionManager(sessionFactory);
    }

    @Bean
    public HibernateTemplate hibernateTemplate(SessionFactory sessionFactory) {
        return new HibernateTemplate(sessionFactory);
    }

   /*
        @Autowired
    private Environment env;

    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource driverManager = new DriverManagerDataSource();
        driverManager.setDriverClassName(
                Objects.requireNonNull(env.getProperty("spring.datasource.driverClassName"))
        );
        driverManager.setUrl(env.getProperty("spring.datasource.url"));
        driverManager.setUsername(env.getProperty("spring.datasource.username"));
        driverManager.setPassword(env.getProperty("spring.datasource.password"));

        return driverManager;
    }*/

//    @Bean
//    public SpringLiquibase liquibase() {
//        SpringLiquibase liquibase = new SpringLiquibase();
//        liquibase.setChangeLog(env.getProperty("output_classpath"));
//        liquibase.setDataSource(dataSource());
//        return liquibase;
//    }
}
