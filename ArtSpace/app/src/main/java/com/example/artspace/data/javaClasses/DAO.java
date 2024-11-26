package com.example.artspace.data.javaClasses;

public interface DAO<T>{
    void save(T entity);

    T getById(int id);

    void delete(int id);

    void update(T entity);
}
