package com.example.schoolservlet.daos.interfaces;

import com.example.schoolservlet.exceptions.DataAccessException;
import com.example.schoolservlet.exceptions.RequiredFieldException;

public interface IAdminDAO {
    boolean login(String cpf, String password) throws DataAccessException, RequiredFieldException;
}