package com.example.schoolservlet.daos.interfaces;

import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.RequiredFieldException;

public interface IAdminDAO {
    boolean login(String cpf, String password) throws DataException, RequiredFieldException;
}