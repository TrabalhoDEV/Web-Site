package com.example.schoolservlet.exceptions;

/**
 * Base exception class for all school-related servlet exceptions.
 *
 * <p>This abstract class extends Exception and requires subclasses to define
 * an HTTP status code associated with the exception. It provides constructors
 * for specifying a message and an optional cause.</p>
 */
public abstract class SchoolServletException extends Exception{
    /**
     * Creates a new servlet exception with a message.
     *
     * @param message the exception message
     */
    public SchoolServletException(String message){
        super(message);
    }

    /**
     * Creates a new servlet exception with a message and cause.
     *
     * @param message the exception message
     * @param cause the original cause
     */
    public SchoolServletException(String message, Throwable cause){
        super(message, cause);
    }

    /**
     * Returns the HTTP status code associated with this exception.
     *
     * @return HTTP status code
     */
    public abstract int getStatus();
}