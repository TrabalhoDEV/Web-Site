package com.example.schoolservlet.exceptions;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Exception thrown when a user attempts to access a resource without proper authorization.
 *
 * <p>This exception extends ValidationException and provides a message specifying
 * the required role or permission. It also returns an HTTP 401 Unauthorized status.</p>
 *
 * @see ValidationException
 */
public class UnauthorizedException extends ValidationException{
    /**
     * Creates a new unauthorized exception with the required role/permission.
     *
     * @param necessary the required role or permission
     */
    public UnauthorizedException(String necessary){
        super(String.format("Para acessar essa página, é necessário ser um %s", necessary));
    }

    /**
     * Returns the HTTP status code associated with this exception.
     *
     * @return HTTP 401 (Unauthorized)
     */
    @Override
    public int getStatus() {
        return HttpServletResponse.SC_UNAUTHORIZED;
    }
}