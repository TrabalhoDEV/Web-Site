package com.example.schoolservlet.exceptions;

import jakarta.servlet.http.HttpServletResponse;

public class DataException extends SchoolServletException{
    public DataException(String message){
        super(message);
    }
    public DataException(String message, Throwable cause){
        super(message, cause);
    }

    @Override
    public int getStatus() {
        return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    }
}
