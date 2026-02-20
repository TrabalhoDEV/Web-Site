package com.example.schoolservlet.exceptions;

public class DataException extends SchoolServletException{
    public DataException(String message){
        super(message);
    }
    public DataException(String message, Throwable cause){
        super(message, cause);
    }
}
