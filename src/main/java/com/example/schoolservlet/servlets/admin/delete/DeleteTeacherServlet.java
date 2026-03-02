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

            teacherDAO.findById(id);

            StudentsPerformance studentsPerformance = studentSubjectDAO.studentsPerformance(id);

            if (studentsPerformance.pending() == 0){
                teacherDAO.delete(id);
            } else {
                request.setAttribute("id", id);
                request.getRequestDispatcher(actualPath).forward(request, response);
                return;
            }

        } catch (NumberFormatException nfe){
            session.setAttribute("error", "ID deve ser um número");
        } catch (DataException | NotFoundException | ValidationException e){
            session.setAttribute("error", e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + responsePath);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        if (!AccessValidation.isAdmin(request, response)) return;

        String idParam = request.getParameter("id");
        String username = request.getParameter("username");
        HttpSession session = request.getSession(false);

        try{
            InputValidation.validateIsNull("id", idParam);
            InputValidation.validateIsNull("usuário", username);

            int id = Integer.parseInt(idParam);
            InputValidation.validateId(id, "id");
            InputValidation.validateUserName(username);

            Teacher oldTeacher = teacherDAO.findById(id);
            Teacher newTeacher = teacherDAO.findByUserName(username);

            if (oldTeacher.getUsername().equals(newTeacher.getUsername())) throw new ValidationException("Usuário não pode ser o professor que está sendo removido");

            schoolClassTeacherDAO.updateTeacher(id, newTeacher.getId());
            subjectTeacherDAO.updateTeacher(id, newTeacher.getId());
            teacherDAO.delete(id);

            response.sendRedirect(request.getContextPath() + "/admin/teacher/find-many");
        } catch (NumberFormatException nfe) {
            ErrorHandler.forward(request, response, HttpServletResponse.SC_BAD_REQUEST, "ID deve ser um número", actualPath);
        } catch (DataException | NotFoundException | ValidationException e){
            ErrorHandler.forward(request, response, e.getStatus(), e.getMessage(), actualPath);
        }

    }
}
