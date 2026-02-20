package com.example.schoolservlet.servlets.admin.findMany;

import com.example.schoolservlet.daos.SchoolClassDAO;
import com.example.schoolservlet.daos.SubjectDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.models.SchoolClass;
import com.example.schoolservlet.models.Subject;
import com.example.schoolservlet.utils.AccessValidation;
import com.example.schoolservlet.utils.Constants;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.Map;

@WebServlet(name = "admin-subjects-find-many", value = "/admin/subject/find-many")
public class FindManySubjectsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        if (!AccessValidation.isAdmin(request, response)) return;

        String pageParam = request.getParameter("page");

        int take = Constants.MAX_TAKE;
        int skip = 0;
        int page;
        int totalCount = 0;

        try {
            page = Integer.parseInt(pageParam);
        } catch (NumberFormatException nfe) {
            page = 1;
        }

        SubjectDAO subjectDAO = new SubjectDAO();
        try{
            totalCount = subjectDAO.totalCount();
        } catch (DataException de){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("error", de.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/findMany/subject.jsp")
                    .forward(request, response);
            return;
        }

        int totalPages = Math.max(1, (int)Math.ceil((double) totalCount / Constants.MAX_TAKE));

        page = Math.max(1, Math.min(page, totalPages));

        skip = take * (page - 1);

        Map<Integer, Subject> subjectMap;

        try {
            subjectMap = subjectDAO.findMany(skip, take);
        } catch (DataException de){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("error", de.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/findMany/subject.jsp")
                    .forward(request, response);
            return;
        }

        request.setAttribute("subjectMap", subjectMap);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("page", page);

        request.getRequestDispatcher("/WEB-INF/views/admin/findMany/subject.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
