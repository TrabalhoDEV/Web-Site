package com.example.schoolservlet.exceptions;

public class MaxLengthException extends ValidationException{
    public MaxLengthException(String field, int lenght){
        super(String.format("O campo %s deve ter menos do que %d caracteres", field, lenght), field);
    }
}
