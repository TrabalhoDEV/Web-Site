package com.example.schoolservlet.utils;

import com.example.schoolservlet.utils.Constants;
import com.example.schoolservlet.utils.enums.UserRoleEnum;
import com.example.schoolservlet.utils.records.AuthenticatedUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class AccessValidation {
    public static void isAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            HttpSession session = request.getSession(false);
            AuthenticatedUser user = (AuthenticatedUser) session.getAttribute("user");

            if (user.role() != UserRoleEnum.ADMIN) {
                request.getRequestDispatcher("/pages/admin/login.jsp").forward(request, response);
                return;
            }

        } catch (NullPointerException npe) {
            request.setAttribute("error", Constants.EXPIRED_SESSION_MESSAGE);
            request.getRequestDispatcher("/pages/admin/login.jsp")
                    .forward(request, response);
            return;
        }
    }

    public static void isTeacher(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            HttpSession session = request.getSession(false);
            AuthenticatedUser user = (AuthenticatedUser) session.getAttribute("user");

            if (user.role() != UserRoleEnum.TEACHER) {
                request.getRequestDispatcher("/index.jsp").forward(request, response);
            }

        } catch (NullPointerException npe) {
            request.setAttribute("error", Constants.EXPIRED_SESSION_MESSAGE);
            request.getRequestDispatcher("/index.jsp")
                    .forward(request, response);
        }
    }

    public static void isStudent(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            HttpSession session = request.getSession(false);
            AuthenticatedUser user = (AuthenticatedUser) session.getAttribute("user");

            if (user.role() != UserRoleEnum.STUDENT) {
                request.getRequestDispatcher("/index.jsp").forward(request, response);
            }

        } catch (NullPointerException npe) {
            request.setAttribute("error", Constants.EXPIRED_SESSION_MESSAGE);
            request.getRequestDispatcher("/index.jsp")
                    .forward(request, response);
        }
    }
}
