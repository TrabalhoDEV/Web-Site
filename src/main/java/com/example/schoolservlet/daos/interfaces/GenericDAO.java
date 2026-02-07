package com.example.schoolservlet.daos.interfaces;

import java.util.Map;

public interface GenericDAO<T>{
    int totalCount();

    Map<Integer, T> findMany(int skip, int take);

    T findById(int id);

    boolean delete(int id);

    boolean create(T object);

    boolean update(T objeto);
}
