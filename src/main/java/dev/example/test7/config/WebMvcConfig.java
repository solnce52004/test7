package dev.example.test7.config;

import dev.example.test7.interceptors.CheckUserInterceptor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
        registry.addInterceptor(new CheckUserInterceptor());
    }

    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver clr = new CookieLocaleResolver();
        //даем возможность переключать локаль через куки
        clr.setDefaultLocale(new Locale("ru"));
        clr.setCookieName("I18N_cookie_lang");
        return clr;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setFallbackToSystemLocale(false);
//        messageSource.setBasename("classpath:i18n/home_page/messages");
        messageSource.setBasename("classpath:i18n/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        final LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");//если перехватываем все и принудительно устанавливаем
        return lci;
    }
}