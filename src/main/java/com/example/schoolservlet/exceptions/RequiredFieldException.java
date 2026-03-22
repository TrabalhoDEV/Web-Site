package com.example.schoolservlet.exceptions;

/**
 * Exception thrown when a required field is missing or empty.
 *
 * <p>This exception extends ValidationException and provides a message specifying
 * which field is mandatory.</p>
 *
 * @see ValidationException
 */
public class RequiredFieldException extends ValidationException{
    /**
     * Creates a new required field exception.
     *
     * @param field the required field name
     */
    public RequiredFieldException(String field){
        super(String.format("O campo %s é obrigatório", field), field);
    }
}