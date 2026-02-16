package com.example.schoolservlet.exceptions;

public class MinLenghtException extends ValidationException{
    public MinLenghtException(String field, int lenght){
        super(String.format("O campo %s deve ter menos do que %d caracteres", field, lenght), field);
    }
}
