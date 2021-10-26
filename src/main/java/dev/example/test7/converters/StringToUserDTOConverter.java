package dev.example.test7.converters;

import dev.example.test7.dto.UserDTO;
import org.springframework.core.convert.converter.Converter;

public class StringToUserDTOConverter implements Converter<String, UserDTO> {

    /**
     * Convert the source object of type {@code S} to target type {@code T}.
     *
     * @param source the source object to convert, which must be an instance of {@code S} (never {@code null})
     * @return the converted object, which must be an instance of {@code T} (potentially {@code null})
     * @throws IllegalArgumentException if the source cannot be converted to the desired target type
     */
    @Override
    public UserDTO convert(String source) {
        final String[] strings = source.split(",");

        return new UserDTO(
                strings[0],
                strings[1],
                Boolean.parseBoolean(strings[2])
        );
    }

    /**
     * Construct a composed {@link Converter} that first applies this {@link Converter}
     * to its input, and then applies the {@code after} {@link Converter} to the
     * result.
     *
     * @param after the {@link Converter} to apply after this {@link Converter}
     *              is applied
     * @return a composed {@link Converter} that first applies this {@link Converter}
     * and then applies the {@code after} {@link Converter}
     * @since 5.3
     */
    @Override
    public <U> Converter<String, U> andThen(Converter<? super UserDTO, ? extends U> after) {
        return null;
    }
}
