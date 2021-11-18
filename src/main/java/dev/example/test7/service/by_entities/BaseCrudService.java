package dev.example.test7.service.by_entities;

import java.util.List;

public interface BaseCrudService<T> {

    T save(T obj);

    T findById(Long id);

    List<T> findAll();

    T update(T obj);

    void delete(Long id);
}
