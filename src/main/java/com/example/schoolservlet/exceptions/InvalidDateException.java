package com.example.schoolservlet.exceptions;

/**
 * Exception thrown when a date value is invalid for a given field.
 */
public class InvalidDateException extends ValidationException{
    /**
     * Creates a new invalid date exception.
     *
     * @param field the field name
     * @param why the reason why the date is invalid
     */
    public InvalidDateException(String field, String why){
        super(String.format("Valor de data inválida: %s", why), field);
    }
}