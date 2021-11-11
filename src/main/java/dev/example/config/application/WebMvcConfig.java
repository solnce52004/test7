package dev.example.config.application;

import dev.example.test7.converter.StringToUserDTOConverter;
import dev.example.test7.interceptor.CheckUserInterceptor;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.ResourceBundleViewResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Configuration
@PropertySource("classpath:application.yaml")

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
//        converter.add(stringConverter());
//        converter.add(mappingJackson2HttpMessageConverter());
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

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setDefaultTimeout(30_000);
    }

    //Thymeleaf/////////////////////////////
    @Bean
    public LayoutDialect layoutDialect() {
        return new LayoutDialect();
    }

    /*   @Bean
       @Description("Thymeleaf template resolver serving HTML")
       public ClassLoaderTemplateResolver templateResolver() {
           ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
           templateResolver.setPrefix("templates/");
           templateResolver.setCacheable(false);
           templateResolver.setSuffix(".html");
           templateResolver.setTemplateMode("HTML");
           templateResolver.setCharacterEncoding("UTF-8");
           return templateResolver;
       }

       @Bean
       @Description("Thymeleaf template engine with Spring integration")
       public SpringTemplateEngine templateEngine() {
           SpringTemplateEngine templateEngine = new SpringTemplateEngine();
           templateEngine.addTemplateResolver(new UrlTemplateResolver());
   //            templateEngine.addDialect(new SpringSecurityDialect());
           templateEngine.setTemplateResolver(templateResolver());
           return templateEngine;
       }

       @Bean
       @Description("Thymeleaf view resolver")
       public ViewResolver viewResolver() {
           ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
           viewResolver.setTemplateEngine(templateEngine());
           viewResolver.setCharacterEncoding("UTF-8");
           return viewResolver;
       }*/

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                .defaultContentType(MediaType.APPLICATION_JSON)
                .favorPathExtension(true);
    }

    @Bean
    public ViewResolver contentNegotiatingViewResolver(ContentNegotiationManager manager) {
        ContentNegotiatingViewResolver resolver = new ContentNegotiatingViewResolver();
        resolver.setContentNegotiationManager(manager);

        List<ViewResolver> resolvers = new ArrayList<>();
        resolvers.add(exporterViewResolver());

        resolver.setViewResolvers(resolvers);
        return resolver;
    }

    @Bean
    public ViewResolver exporterViewResolver() {
        final ResourceBundleViewResolver viewResolver = new ResourceBundleViewResolver();
        viewResolver.setBasename("views");
        viewResolver.setOrder(1);
        return viewResolver;
    }
}