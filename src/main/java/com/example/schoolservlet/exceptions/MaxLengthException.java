package com.example.schoolservlet.exceptions;

/**
 * Exception thrown when a field value exceeds the maximum allowed length.
 */
public class MaxLengthException extends ValidationException{
    /**
     * Creates a new max length exception.
     *
     * @param field the field name
     * @param length the maximum allowed length
     */
    public MaxLengthException(String field, int length){
        super(String.format("O campo %s deve ter menos do que %d caracteres", field, length), field);
    }
}