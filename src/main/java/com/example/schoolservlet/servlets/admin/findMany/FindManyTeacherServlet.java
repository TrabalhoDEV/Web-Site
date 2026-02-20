package com.example.schoolservlet.servlets.admin.findMany;

import com.example.schoolservlet.daos.TeacherDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.models.Teacher;
import com.example.schoolservlet.utils.Constants;
import com.example.schoolservlet.utils.enums.UserRoleEnum;
import com.example.schoolservlet.utils.records.AuthenticatedUser;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.Map;

@WebServlet(name = "admin-find-many-teachers", value = "/admin/teacher/find-many")
public class FindManyTeacherServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        try {
            HttpSession session = request.getSession();
            AuthenticatedUser user = (AuthenticatedUser) session.getAttribute("user");

            // Only administrators can register students
            if (user.role() != UserRoleEnum.ADMIN) {
                request.getRequestDispatcher("/pages/admin/login.jsp")
                        .forward(request, response);
                return;
            }

        } catch (NullPointerException npe) {
            // User not authenticated or session attribute missing
            request.setAttribute("error", "Sessão expirada, faça login novamente");
            request.getRequestDispatcher("/pages/admin/login.jsp")
                    .forward(request, response);
            return;
        }

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

        TeacherDAO teacherDAO = new TeacherDAO();
        try{
            totalCount = teacherDAO.totalCount();
        } catch (DataException de){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("error", de.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/findMany/student.jsp")
                    .forward(request, response);
            return;
        }

        int totalPages = Math.max(1, (int)Math.ceil((double) totalCount / Constants.MAX_TAKE));

        page = Math.max(1, Math.min(page, totalPages));

        skip = take * (page - 1);

        Map<Integer, Teacher> teacherMap;

        try {
            teacherMap = teacherDAO.findMany(skip, take);
        } catch (DataException de){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("error", de.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/findMany/student.jsp")
                    .forward(request, response);
            return;
        }

        request.setAttribute("teacherMap", teacherMap);
        request.setAttribute("page", page);

        request.getRequestDispatcher("/WEB-INF/views/admin/findMany/teacher.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
