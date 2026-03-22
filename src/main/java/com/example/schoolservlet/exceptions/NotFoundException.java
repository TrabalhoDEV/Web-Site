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
    private final String table;
    private final String field;
    private final String value;

    /**
     * Creates a new not found exception with search metadata.
     *
     * @param table the logical table/entity name
     * @param field the searched field name
     * @param value the searched field value
     */
    public NotFoundException(String table, String field, String value){
        super(String.format("Nenhum(a) %s contendo o campo %s de valor %s foi encontrado(a)",table, field, value));
        this.table = table;
        this.field = field;
        this.value = value;
    }

    /**
     * Returns the table/entity name used in the search.
     *
     * @return table/entity name
     */
    public String getTable() {
        return table;
    }

    /**
     * Returns the field name used in the search.
     *
     * @return field name
     */
    public String getField() {
        return field;
    }

    /**
     * Returns the field value used in the search.
     *
     * @return searched value
     */
    public String getValue() {
        return value;
    }

    /**
     * Returns the HTTP status code associated with this exception.
     *
     * @return HTTP 404 status code
     */
    @Override
    public int getStatus() {
        return HttpServletResponse.SC_NOT_FOUND;
    }
}