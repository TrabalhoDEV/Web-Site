package com.example.schoolservlet.exceptions;

public class MissingSomethingException extends ValidationException{
    public MissingSomethingException(String field, String thing){
        super(String.format("O campo %s precisa conter %s", field, thing), field);
    }
}
