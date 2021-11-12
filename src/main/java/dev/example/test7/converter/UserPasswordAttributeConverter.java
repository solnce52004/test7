package dev.example.test7.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
@Component
public class UserPasswordAttributeConverter implements AttributeConverter<String, String> {

    // не должно быть конструктора
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public String convertToDatabaseColumn(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public String convertToEntityAttribute(String password) {
        return password;
    }
}
