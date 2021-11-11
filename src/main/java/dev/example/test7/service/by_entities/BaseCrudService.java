package dev.example.test7.service.by_entities;

import dev.example.test7.entity.User;

import java.util.List;

public interface BaseCrudService<T> {

    void save(T obj);

    T findById(Long id);

    List<T> findAll();

    User update(T obj);

    void delete(Long id);
}
