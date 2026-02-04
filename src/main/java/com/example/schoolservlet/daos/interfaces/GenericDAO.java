package com.example.schoolservlet.daos.interfaces;

import java.util.List;

public interface GenericDAO<T>{
    int totalCount();

    List<T> findMany(int skip, int take);
}
