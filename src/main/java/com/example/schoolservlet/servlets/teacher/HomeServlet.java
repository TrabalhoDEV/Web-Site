package com.example.schoolservlet.servlets.teacher;

import com.example.schoolservlet.daos.StudentSubjectDAO;
import com.example.schoolservlet.daos.TeacherDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.NotFoundException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.StudentSubject;
import com.example.schoolservlet.models.Teacher;
import com.example.schoolservlet.utils.AccessValidation;
import com.example.schoolservlet.utils.ErrorHandler;
import com.example.schoolservlet.utils.enums.UserRoleEnum;
import com.example.schoolservlet.utils.records.AuthenticatedUser;
import com.example.schoolservlet.utils.records.StudentsPerformance;
import com.example.schoolservlet.utils.records.TeacherPendency;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "teacher-home", value = "/teacher")
public class HomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        if (!AccessValidation.isTeacher(request, response)) return;

        request.setAttribute("studentsPerformance", new StudentsPerformance(0, 0, 0));
        request.setAttribute("studentsToHelpMap", new HashMap<Integer, StudentSubject>());
        request.setAttribute("pendencies", new ArrayList<TeacherPendency>());

        HttpSession session = request.getSession(false);
        AuthenticatedUser user;
        Teacher teacher = null;
        Map<Integer, StudentSubject> studentSubjectMap;
        StudentsPerformance studentsPerformance;
        List<TeacherPendency> pendencies;

        try {
            user = (AuthenticatedUser) session.getAttribute("user");
            teacher = (Teacher) session.getAttribute("teacher");

        } catch (NullPointerException npe) {
            // User not authenticated or session attribute missing
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            request.setAttribute("error", "Sessão expirada, faça login novamente");
            request.getRequestDispatcher("/index.jsp")
                    .forward(request, response);
            return;
        }

        try {
            StudentSubjectDAO studentSubjectDAO = new StudentSubjectDAO();

            if (teacher == null) {
                TeacherDAO teacherDAO = new TeacherDAO();

                teacher = teacherDAO.findById(user.id());

                session.setAttribute("teacher", teacher);
            }
            request.setAttribute("teacher", teacher);

            studentSubjectMap = studentSubjectDAO.findStudentsThatRequireTeacher(teacher.getId());
            studentsPerformance = studentSubjectDAO.studentsPerformance(teacher.getId());
            pendencies = studentSubjectDAO.teacherPendency(teacher.getId());
        } catch (ValidationException | NotFoundException | DataException e) {
            ErrorHandler.forward(request, response, e.getStatus(), e.getMessage(), "/WEB-INF/views/teacher/index.jsp");
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        request.setAttribute("studentsToHelpMap", studentSubjectMap);
        request.setAttribute("studentsPerformance", studentsPerformance);
        request.setAttribute("pendencies", pendencies);
        request.getRequestDispatcher("/WEB-INF/views/teacher/index.jsp").forward(request, response);
    }
}
