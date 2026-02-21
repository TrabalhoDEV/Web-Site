package com.example.schoolservlet.exceptions;

import jakarta.servlet.http.HttpServletResponse;

public class UnauthorizedException extends ValidationException{
    public UnauthorizedException(String necessary){
        super(String.format("Para acessar essa página, é necessário logar como %s", necessary));
    }

    @Override
    public int getStatus() {
        return HttpServletResponse.SC_UNAUTHORIZED;
    }
}
