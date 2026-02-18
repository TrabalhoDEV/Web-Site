package com.example.schoolservlet.daos.interfaces;

import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.ValidationException;

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
}