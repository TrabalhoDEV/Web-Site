package com.example.schoolservlet.exceptions;

public class MinLengthException extends ValidationException{
    public MinLengthException(String field, int lenght){
        super(String.format("O campo %s deve ter mais do que %d caracteres", field, lenght), field);
    }
}
