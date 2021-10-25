package dev.example.test7.exceptions.error_mappers;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.*;

public class MapperErrorFieldsByMethodArgumentNotValid implements MapperErrorFields<MethodArgumentNotValidException> {
    @Override
    public Map<String, Set<String>> getErrors(MethodArgumentNotValidException ex) {
        Map<String, Set<String>> errors = new HashMap<>();
        BindingResult result = ex.getBindingResult();

        if (result.hasErrors()) {
            List<ObjectError> fields = result.getAllErrors();

            for (ObjectError field : fields) {
                final FieldError fieldError = (FieldError) field;
                final String fieldName = fieldError.getField();
                final String msg = fieldError.getDefaultMessage();

                Set<String> errMessages = errors.getOrDefault(fieldName, new TreeSet<>());
                errMessages.add(msg == null ? "" : msg);

                errors.put(fieldName, errMessages);
            }
        }

        return errors;
    }
}
