package com.example.schoolservlet.daos.interfaces;

import com.example.schoolservlet.exceptions.*;

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
     * @throws DataException if an error in database happen
     */
    int totalCount() throws DataException;

    /**
     * Method that returns many records from database's table
     * @param skip  is how many records needed to skip
     * @param take  is how many records it should return
     * @return      a map with key as id and value as the table's object
     * @throws DataException if an error in database happen
     */
    Map<Integer, T> findMany(int skip, int take) throws DataException;

    /**
     * Method that finds one record by its id
     * @param id     is record's id
     * @return       an object that has all data from the database's record with that id
     * @throws NotFoundException if mehtod does not found the record
     * @throws DataException if an error in database happen
     * @throws ValidationException if some param is null or if its shape is wrong
     */
    T findById(int id) throws NotFoundException, DataException, ValidationException;

    /**
     * Method that deletes one record by its id
     * @param id id record's id
     * @throws NotFoundException if mehtod does not found the record
     * @throws DataException if an error in database happen
     * @throws ValidationException if some param is null or if its shape is wrong
     */
    void delete(int id) throws NotFoundException, DataException, ValidationException;

    /**
     * Method that creates one record by an object as a param
     * @param object is the object that represents all data that will be inserted
     * @throws DataException if an error in database happen
     * @throws ValidationException if some param is null or if its shape is wrong
     */
    void create(T object) throws DataException, ValidationException;

    /**
     * Method that updates a record
     * @param object is the object that represents all data that will be updated
     * @throws NotFoundException if mehtod does not found the record
     * @throws DataException if an error in database happen
     * @throws ValidationException if some param is null or if its shape is wrong
     */
    void update(T object) throws NotFoundException, DataException, ValidationException;
}