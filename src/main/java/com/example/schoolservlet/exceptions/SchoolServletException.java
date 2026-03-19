package com.example.schoolservlet.exceptions;

/**
 * Base exception class for all school-related servlet exceptions.
 *
 * <p>This abstract class extends Exception and requires subclasses to define
 * an HTTP status code associated with the exception. It provides constructors
 * for specifying a message and an optional cause.</p>
 */
public abstract class SchoolServletException extends Exception{
    public SchoolServletException(String message){
        super(message);
    }
    public SchoolServletException(String message, Throwable cause){
        super(message, cause);
    }

    public abstract int getStatus();
}
