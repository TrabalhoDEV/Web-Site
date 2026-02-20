package com.example.schoolservlet.exceptions;

public class RegexException extends ValidationException{
    public RegexException(String field){
        super(String.format("O formato do campo %s está inválido", field), field);
    }
}
