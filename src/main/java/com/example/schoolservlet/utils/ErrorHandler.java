package com.example.schoolservlet.utils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ErrorHandler {
    private ErrorHandler(){}

    public static void forward(
            HttpServletRequest request,
            HttpServletResponse response,
            int status,
            String message,
            String view
    ) throws ServletException, IOException {
        response.setStatus(status);
        request.setAttribute("error", message);
        request.getRequestDispatcher(view).forward(request, response);
    }
}