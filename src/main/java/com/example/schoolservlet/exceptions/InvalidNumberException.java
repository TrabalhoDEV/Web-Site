package com.example.schoolservlet.exceptions;

public class InvalidNumberException extends SchoolServletException{
    public InvalidNumberException(String field, String why){
        super(String.format("Valor númerico inválido %s: %s", field, why));
    }
}
