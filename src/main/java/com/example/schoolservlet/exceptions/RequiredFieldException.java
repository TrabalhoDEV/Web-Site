package com.example.schoolservlet.exceptions;

public class RequiredFieldException extends ValidationException{
    public RequiredFieldException(String field){
        super(String.format("O campo %s é obrigatório", field), field);
    }
}
