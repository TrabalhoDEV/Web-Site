package com.example.schoolservlet.daos.interfaces;

<<<<<<< HEAD
import java.util.Map;

/**
 * Generic interface that defines some CRUD (Create, Read, Update and Delete) methodos that every DAO should have.
 * these methods are the basic of CRUD operations;
 * @param <T> is the model that represents table's fields
 */
public interface GenericDAO<T>{
    /**
     * A method that count the total number of records from database's table
     * @return      The total count
     */
    int totalCount();

    /**
     * Method that returns many records from database's table
     * @param skip  is how many records needed to skip
     * @param take  is how many records it should return
     * @return      a map with key as id and value as the table's object
     */
    Map<Integer, T> findMany(int skip, int take);

    /**
     * Method that finds one record by its id
     * @param id is record's id
     * @return   an object that represents the record
     */
    T findById(int id);

    /**
     * Method that deletes one record by its id
     * @param id is record's id
     * @return   true if the deletion happen
     */
    boolean delete(int id);

    /**
     * Method that deletes one record by its id
     * @param object is new record's representation
     * @return   true if the creation happen
     */
    boolean create(T object);

    /**
     * Method that updates a record
     * @param object is record's new representation with same id
     * @return   true if the update happen
     */
    boolean update(T object);
}
=======
import java.util.List;

public interface GenericDAO<T>{
    int totalCount();

    List<T> findMany(int skip, int take);
}
>>>>>>> 6e33e589d430f2f48d0a3e464132537d3bfe8ce9
