package com.example.schoolservlet.servlets.admin.findMany;

import com.example.schoolservlet.daos.StudentDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.RequiredFieldException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.Student;
import com.example.schoolservlet.utils.*;
import com.example.schoolservlet.utils.enums.StudentStatusEnum;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.Map;

@WebServlet(name = "admin-find-many-student", value = "/admin/student/find-many")
public class FindManyStudentsServlet extends HttpServlet {
    private final StudentDAO studentDAO = new StudentDAO();
    private final String responsePath = "/WEB-INF/views/admin/findMany/student.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        if (!AccessValidation.isAdmin(request, response)) return;

        HttpSession session = request.getSession(false);
        String pageParam = request.getParameter("page");
        String filter = request.getParameter("filter");
        String statusParam  = request.getParameter("status");

        StudentStatusEnum statusFilter = null;
        try {
            if (statusParam != null && !statusParam.isBlank()) {
                int statusValue = Integer.parseInt(statusParam);
                statusFilter = StudentStatusEnum.values()[statusValue - 1];
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ignored) {}

        int take = Constants.MAX_TAKE;
        int page = 1;

        request.setAttribute("filter", filter != null ? filter : "");
        request.setAttribute("statusFilter", statusFilter != null ? statusFilter.ordinal() + 1 : null);

        if (session.getAttribute("error") != null) {
            request.setAttribute("error", session.getAttribute("error"));
            session.removeAttribute("error");
        }

        try {
            page = Integer.parseInt(pageParam);
        } catch (NumberFormatException ignored) {}

        try {
            int totalCount = studentDAO.count(filter, statusFilter);
            int totalPages = Math.max(1, (int) Math.ceil((double) totalCount / take));

            page = Math.max(1, Math.min(page, totalPages));
            int skip = take * (page - 1);

            Map<Integer, Student> studentMap = studentDAO.findMany(skip, take, filter, statusFilter);

            request.setAttribute("studentMap", studentMap);
            request.setAttribute("page", page);
            request.setAttribute("totalPages", totalPages);
        } catch (DataException de) {
            ErrorHandler.forward(request, response, de.getStatus(), de.getMessage(), responsePath);
            return;
        }

        request.getRequestDispatcher(responsePath).forward(request, response);
    }
}