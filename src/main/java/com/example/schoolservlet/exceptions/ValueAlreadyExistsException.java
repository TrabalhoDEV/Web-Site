package com.example.schoolservlet.exceptions;

public class ValueAlreadyExistsException extends ValidationException{
    public ValueAlreadyExistsException(String field, String value){
        super(String.format("O %s com valor %s já está cadastrado", field, value), field);
    }
}
