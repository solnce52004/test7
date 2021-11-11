package dev.example.config.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class Translator {
    private final MessageSource messageSource;

    @Autowired
    private Translator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String trans(String keyMsg) {
        return messageSource.getMessage(
                keyMsg,
                null,
                LocaleContextHolder.getLocale()
        );
    }
}
