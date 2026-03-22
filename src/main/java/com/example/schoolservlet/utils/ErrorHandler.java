package com.example.schoolservlet.utils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Utility class for handling and forwarding error responses in servlets.
 *
 * <p>Provides a static method to set the HTTP status code, attach an error message,
 * and forward the request to a specified view.
 *
 * <p>The class cannot be instantiated.
 */
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