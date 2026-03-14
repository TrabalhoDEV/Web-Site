package com.example.schoolservlet.servlets.admin.findMany;

import java.io.IOException;
import java.util.Map;

import com.example.schoolservlet.daos.SchoolClassDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.RequiredFieldException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.SchoolClass;
import com.example.schoolservlet.utils.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "admin-find-many-school-classes", value = "/admin/school-class/find-many")
public class FindManySchoolClassesServlet extends HttpServlet {
    private final SchoolClassDAO schoolClassDAO = new SchoolClassDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        if (!AccessValidation.isAdmin(request, response)) return;

        HttpSession session = request.getSession(false);
        String pageParam = request.getParameter("page");
        String nameFilter = request.getParameter("name");
        String error = (String) session.getAttribute("error");

        int take = Constants.MAX_TAKE;
        int page = 1;

        request.setAttribute("nameFilter", nameFilter != null ? nameFilter : "");

        if (error != null) {
            request.setAttribute("error", error);
            session.removeAttribute("error");
        }

        try {
            page = Integer.parseInt(pageParam);
        } catch (NumberFormatException ignored) {}

        try {
            try {
                InputValidation.validateSchoolClassName(nameFilter);

                int totalCount = schoolClassDAO.count(nameFilter);
                int totalPages = Math.max(1, (int) Math.ceil((double) totalCount / take));

                page = Math.max(1, Math.min(page, totalPages));
                int skip = take * (page - 1);

                Map<Integer, SchoolClass> schoolClassMap = schoolClassDAO.findMany(skip, take, nameFilter);

                request.setAttribute("schoolClassMap", schoolClassMap);
                request.setAttribute("page", page);
                request.setAttribute("totalPages", totalPages);

            } catch (RequiredFieldException rfe) {
                getDefaultData(request, page);
            } catch (ValidationException ve) {
                getDefaultData(request, page);
                ErrorHandler.forward(request, response, ve.getStatus(), ve.getMessage(),
                        "/WEB-INF/views/admin/findMany/school-class.jsp");
                return;
            }
        } catch (DataException de) {
            ErrorHandler.forward(request, response, de.getStatus(), de.getMessage(),
                    "/WEB-INF/views/admin/findMany/school-class.jsp");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/admin/findMany/school-class.jsp")
                .forward(request, response);
    }

    private void getDefaultData(HttpServletRequest request, int page) throws DataException {
        int take = Constants.MAX_TAKE;
        int totalCount = schoolClassDAO.totalCount();
        int totalPages = Math.max(1, (int) Math.ceil((double) totalCount / take));

        page = Math.max(1, Math.min(page, totalPages));
        int skip = take * (page - 1);

        Map<Integer, SchoolClass> schoolClassMap = schoolClassDAO.findMany(skip, take);

        request.setAttribute("schoolClassMap", schoolClassMap);
        request.setAttribute("page", page);
        request.setAttribute("totalPages", totalPages);
    }
}