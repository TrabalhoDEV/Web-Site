package com.example.schoolservlet.exceptions;

public class MaxLenghtException extends ValidationException{
    public MaxLenghtException(String field, int lenght){
        super(String.format("O campo %s deve ter menos do que %d caracteres", field, lenght), field);
    }
}
