package com.example.schoolservlet.exceptions;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Exception thrown when a requested record is not found in the database.
 *
 * <p>This exception extends SchoolServletException and contains details about
 * the table, field, and value that were searched for but not found. It also
 * returns an HTTP 404 Not Found status.</p>
 *
 * @see SchoolServletException
 */
public class NotFoundException extends SchoolServletException{
    private String table;
    private String field;
    private String value;
    public NotFoundException(String table, String field, String value){
        super(String.format("Nenhum(a) %s contendo o campo %s de valor %s foi encontrado(a)",table, field, value));
        this.table = table;
        this.field = field;
        this.value = value;
    }

    public String getTable() {
        return table;
    }

    public String getField() {
        return field;
    }

    public String getValue() {
        return value;
    }

    @Override
    public int getStatus() {
        return HttpServletResponse.SC_NOT_FOUND;
    }
}