package com.example.schoolservlet.exceptions;

/**
 * Exception thrown when a numeric value is invalid for a given field.
 */
public class InvalidNumberException extends ValidationException{
    /**
     * Creates a new invalid number exception.
     *
     * @param field the field name
     * @param why the reason why the number is invalid
     */
    public InvalidNumberException(String field, String why){
        super(String.format("Valor numérico inválido %s: %s", field, why), field);
    }
}