package com.example.schoolservlet.exceptions;

public class InvalidDateException extends ValidationException{
    public InvalidDateException(String field, String why){
        super(String.format("Valor de data inválida %s: %s", field, why), field);
    }
}
