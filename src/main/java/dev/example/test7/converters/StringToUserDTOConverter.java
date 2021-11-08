package dev.example.test7.converters;

import dev.example.test7.dto.UserDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToUserDTOConverter implements Converter<String, UserDTO> {

    @Override
    public UserDTO convert(String source) {
        final String[] strings = source.split(",");

        return new UserDTO(
                strings[0],
                strings[1],
                Boolean.parseBoolean(strings[2])
        );
    }

    @Override
    public <U> Converter<String, U> andThen(Converter<? super UserDTO, ? extends U> after) {
        return null;
    }
}
