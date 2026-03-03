package com.example.schoolservlet.exceptions;

import jakarta.servlet.http.HttpServletResponse;

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
    @Override
    public int getStatus() {
        return HttpServletResponse.SC_BAD_REQUEST;
    }
}
