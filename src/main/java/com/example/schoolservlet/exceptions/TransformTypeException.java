package com.example.schoolservlet.exceptions;

public class TransformTypeException extends ValidationException{
    public TransformTypeException(String value, String to){
        super(String.format("Não foi possível converter %s para a forma de %s", value, to));
    }
}
