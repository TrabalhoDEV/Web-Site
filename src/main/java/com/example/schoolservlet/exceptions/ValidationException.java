package com.example.schoolservlet.exceptions;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Exception thrown when a validation error occurs in a school-related servlet operation.
 *
 * <p>This exception extends SchoolServletException and can optionally specify
 * the field that caused the validation failure. It returns an HTTP 400 Bad Request status.</p>
 *
 * @see SchoolServletException
 */
public class ValidationException extends SchoolServletException{
    /**
     * Optional field name related to the validation error.
     */
    private final String field;

    /**
     * Creates a new validation exception without field context.
     *
     * @param message the validation error message
     */
    public ValidationException(String message){
        super(message);
        this.field = null;
    }

    /**
     * Creates a new validation exception with field context.
     *
     * @param message the validation error message
     * @param field the field name associated with the validation error
     */
    public ValidationException(String message, String field){
        super(message);
        this.field = field;
    }

    /**
     * Returns the field related to the validation error.
     *
     * @return the field name, or {@code null} when not specified
     */
    public String getField() {
        return field;
    }

    /**
     * Returns the HTTP status code associated with this exception.
     *
     * @return HTTP 400 (Bad Request)
     */
    @Override
    public int getStatus() {
        return HttpServletResponse.SC_BAD_REQUEST;
    }
}