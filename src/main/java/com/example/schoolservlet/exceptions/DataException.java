package com.example.schoolservlet.exceptions;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Exception thrown when an unexpected data-layer error occurs.
 *
 * <p>This exception maps to HTTP 500 (Internal Server Error).</p>
 */
public class DataException extends SchoolServletException{
    /**
     * Creates a new data exception with a message.
     *
     * @param message the exception message
     */
    public DataException(String message){
        super(message);
    }

    /**
     * Creates a new data exception with a message and cause.
     *
     * @param message the exception message
     * @param cause the original cause
     */
    public DataException(String message, Throwable cause){
        super(message, cause);
    }

    /**
     * Returns the HTTP status code associated with this exception.
     *
     * @return HTTP 500 status code
     */
    @Override
    public int getStatus() {
        return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    }
}