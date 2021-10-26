package dev.example.test7.config;

import dev.example.test7.converters.StringToUserDTOConverter;
import dev.example.test7.interceptors.CheckUserInterceptor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.List;
import java.util.Locale;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
        registry.addInterceptor(new CheckUserInterceptor());
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToUserDTOConverter());
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

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        converters.add(stringConverter());
//        converters.add(mappingJackson2HttpMessageConverter());
        converters.add(excelConverter());
    }

    @Bean
    public ResourceHttpMessageConverter excelConverter() {
        final ResourceHttpMessageConverter converter = new ResourceHttpMessageConverter();
        converter.setSupportedMediaTypes(
                List.of(new MediaType("application", "vnd.ms-excel"))
        );
        return converter;
    }
}