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
    private String field;
    public ValidationException(String message){
        super(message);
        this.field = null;
    }

    public ValidationException(String message, String field){
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
    @Override
    public int getStatus() {
        return HttpServletResponse.SC_BAD_REQUEST;
    }
}
