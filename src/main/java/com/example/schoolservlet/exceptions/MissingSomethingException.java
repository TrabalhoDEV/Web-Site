package com.example.schoolservlet.exceptions;

/**
 * Exception thrown when a required element is missing from a field.
 *
 * <p>This exception extends ValidationException and provides a message specifying
 * which field is missing a particular required element.</p>
 *
 * @see ValidationException
 */
public class MissingSomethingException extends ValidationException{
    public MissingSomethingException(String field, String thing){
        super(String.format("O campo %s precisa conter %s", field, thing), field);
    }
}
