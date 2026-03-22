package com.example.schoolservlet.exceptions;

/**
 * Exception thrown when a value that must be unique already exists in the database.
 *
 * <p>This exception extends ValidationException and provides a message indicating
 * the field and value that caused the uniqueness violation.</p>
 *
 * @see ValidationException
 */
public class ValueAlreadyExistsException extends ValidationException{
    /**
     * Creates a new exception for uniqueness constraint violation.
     *
     * @param field the unique field name
     * @param value the value that already exists
     */
    public ValueAlreadyExistsException(String field, String value){
        super(String.format("O %s %s já está cadastrado", field, value), field);
    }
}