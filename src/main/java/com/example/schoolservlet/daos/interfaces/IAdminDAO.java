package com.example.schoolservlet.daos.interfaces;

import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.NotFoundException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.Admin;

/**
 * Interface for managing operations in database's table admin
 */
public interface IAdminDAO {

    /**
     * Authenticates an administrator by validating their CPF and password
     * @param cpf      the administrator's CPF number used as a login identifier
     * @param password the plain-text password to be verified against the stored hash
     * @return          true if the credentials are valid and the administrator is authenticated;
     * @throws DataException       if a database or persistence error occurs during authentication
     * @throws ValidationException if the CPF or password fail format or business rule validation
     */
    boolean login(String cpf, String password) throws DataException, ValidationException;

    /**
     * Finds an administrator by their document number
     * @param document the cpf to search for
     * @return         a record that represents admin entity
     * @throws DataException       if a database or persistence error occurs
     * @throws NotFoundException   if no administrator is found with the given document
     * @throws ValidationException if the document fails format or business rule validation
     */
    Admin findByDocument(String document) throws DataException, NotFoundException, ValidationException;
}