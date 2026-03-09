package com.example.schoolservlet.exceptions;

public abstract class SchoolServletException extends Exception{
    public SchoolServletException(String message){
        super(message);
    }
    public SchoolServletException(String message, Throwable cause){
        super(message, cause);
    }

    public abstract int getStatus();
}
