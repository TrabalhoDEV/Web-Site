package com.example.schoolservlet.exceptions;

public class DataAccessException extends SchoolServletException{
    public DataAccessException(String message){
        super(message);
    }
    public DataAccessException(String message, Throwable cause){
        super(message, cause);
    }
}
