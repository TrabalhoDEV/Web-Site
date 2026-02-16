package com.example.schoolservlet.daos.interfaces;

import com.example.schoolservlet.exceptions.*;

import java.util.Map;
import java.util.Optional;

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
    int totalCount() throws DataAccessException;

    /**
     * Method that returns many records from database's table
     * @param skip  is how many records needed to skip
     * @param take  is how many records it should return
     * @return      a map with key as id and value as the table's object
     */
    Map<Integer, T> findMany(int skip, int take) throws DataAccessException;

    /**
     * Method that finds one record by its id
     * @param id is record's id
     * @return   an object that represents the record
     */
    T findById(int id) throws NotFoundException, DataAccessException, InvalidNumberException;

    /**
     * Method that deletes one record by its id
     * @param id is record's id
     * @return   true if the deletion happen
     */
    void delete(int id) throws ValidationException, NotFoundException, DataAccessException, InvalidNumberException;

    /**
     * Method that deletes one record by its id
     * @param object is new record's representation
     * @return   true if the creation happen
     */
    void create(T object) throws DataAccessException, RequiredFieldException, InvalidDateException;

    /**
     * Method that updates a record
     * @param object is record's new representation with same id
     * @return   true if the update happen
     */
    void update(T object) throws NotFoundException, DataAccessException, InvalidNumberException, RequiredFieldException, InvalidDateException;
}