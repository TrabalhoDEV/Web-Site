package com.example.schoolservlet.exceptions;

/**
 * Exception thrown when a field's value does not match the required regular expression pattern.
 *
 * <p>This exception extends ValidationException and provides a message indicating
 * which field has an invalid format.</p>
 *
 * @see ValidationException
 */
public class RegexException extends ValidationException{
    public RegexException(String field){
        super(String.format("O formato do campo %s está inválido", field), field);
    }
}
