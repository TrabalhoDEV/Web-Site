package com.example.schoolservlet.servlets.admin.delete;

import com.example.schoolservlet.daos.*;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.NotFoundException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.SchoolClassTeacher;
import com.example.schoolservlet.models.Teacher;
import com.example.schoolservlet.utils.AccessValidation;
import com.example.schoolservlet.utils.ErrorHandler;
import com.example.schoolservlet.utils.InputValidation;
import com.example.schoolservlet.utils.records.StudentsPerformance;
import com.example.schoolservlet.utils.records.TeacherPendency;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "DeleteTeacherServlet", value = "/admin/teacher/delete")
public class DeleteTeacherServlet extends HttpServlet {
    private final String responsePath = "/admin/teacher/find-many";
    private final String actualPath = "/WEB-INF/views/admin/delete/teacher.jsp";
    private final TeacherDAO teacherDAO = new TeacherDAO();
    private final StudentSubjectDAO studentSubjectDAO = new StudentSubjectDAO();
    private final SchoolClassTeacherDAO schoolClassTeacherDAO = new SchoolClassTeacherDAO();
    private final SubjectTeacherDAO subjectTeacherDAO = new SubjectTeacherDAO();

    /**
     * Handles HTTP GET requests for deleting a teacher.
     *
     * <p>This method validates admin access and the teacher ID parameter. It checks
     * if the teacher exists and evaluates any pending student performances. If no
     * pending performances exist, the teacher is deleted. Otherwise, it forwards
     * to a JSP page showing why deletion is blocked. Errors are captured in the
     * session for feedback. After processing, it redirects to the teacher listing page.</p>
     *
     * @param request the HttpServletRequest object containing client request data
     * @param response the HttpServletResponse object used to send responses
     * @throws ServletException if a servlet-related error occurs
     * @throws IOException if an input or output error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        if (!AccessValidation.isAdmin(request, response)) return;

        String idParam = request.getParameter("id");
        HttpSession session = request.getSession(false);

        try {
            InputValidation.validateIsNull("id", idParam);
            int id = Integer.parseInt(idParam);
            InputValidation.validateId(id, "id");

            teacherDAO.delete(id);
        } catch (NumberFormatException nfe){
            session.setAttribute("error", "ID deve ser um número");
        } catch (DataException | NotFoundException | ValidationException e){
            session.setAttribute("error", e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + responsePath);
    }
}
