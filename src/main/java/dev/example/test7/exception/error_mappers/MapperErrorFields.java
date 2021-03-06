package dev.example.test7.exception.error_mappers;

import java.util.Map;
import java.util.Set;

public interface MapperErrorFields<T extends Exception> {
    Map<String, Set<String>> getErrors(T ex);
}
