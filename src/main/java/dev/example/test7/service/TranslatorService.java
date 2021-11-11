package dev.example.test7.service;

import dev.example.test7.config.Translator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TranslatorService {
    private final Translator translator;

    @Autowired
    public TranslatorService(Translator translator) {
        this.translator = translator;
    }

    public String trans(String key){
        return translator.trans(key);
    }
}
