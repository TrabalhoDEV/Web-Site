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
    public MinLengthException(String field, int lenght){
        super(String.format("O campo %s deve ter mais do que %d caracteres", field, lenght), field);
    }
}
