package com.example.schoolservlet.exceptions;

/**
 * Exception thrown when a field's value does not meet the minimum length requirement.
 *
 * <p>This exception extends ValidationException and provides a formatted message
 * indicating which field failed the validation and the minimum required length.</p>
 *
 * @see ValidationException
 */
public class MinLengthException extends ValidationException{
    /**
     * Creates a new min length exception.
     *
     * @param field the field name
     * @param length the minimum required length
     */
    public MinLengthException(String field, int length){
        super(String.format("O campo %s deve ter mais do que %d caracteres", field, length), field);
    }
}