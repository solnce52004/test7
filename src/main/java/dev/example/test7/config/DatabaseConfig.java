package dev.example.test7.config;

import liquibase.integration.spring.SpringLiquibase;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

@Configuration
public class DatabaseConfig {

    @Autowired
    private Environment env;

   /* @Bean
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

    @Bean(name="entityManagerFactory") //обязательно!
    public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setPackagesToScan("dev.example.test7.entities");
        sessionFactory.setAnnotatedPackages("dev.example.test7.repo");
        sessionFactory.setHibernateProperties(hibernateProperties());


        return sessionFactory;
    }

    public Properties hibernateProperties() {
        final Properties properties = new Properties();

        properties.setProperty(
                "hibernate.hbm2ddl.auto",
                env.getProperty("spring.jpa.hibernate.ddl-auto")
        );
        properties.setProperty(
                "hibernate.dialect",
                env.getProperty("spring.jpa.database-platform")
        );
        properties.setProperty(
                "hibernate.show_sql",
                env.getProperty("spring.jpa.show-sql")
        );
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
}
