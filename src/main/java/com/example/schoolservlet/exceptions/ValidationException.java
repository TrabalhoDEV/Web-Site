package com.example.schoolservlet.exceptions;

public class ValidationException extends SchoolServletException{
    private String field;
    public ValidationException(String message){
        super(message);
        this.field = null;
    }

    public ValidationException(String message, String field){
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
