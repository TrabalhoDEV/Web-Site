package com.example.schoolservlet.servlets.teacher;

import com.example.schoolservlet.daos.StudentSubjectDAO;
import com.example.schoolservlet.daos.TeacherDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.NotFoundException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.StudentSubject;
import com.example.schoolservlet.models.Teacher;
import com.example.schoolservlet.utils.enums.UserRoleEnum;
import com.example.schoolservlet.utils.records.AuthenticatedUser;
import com.example.schoolservlet.utils.records.StudentsPerformance;
import com.example.schoolservlet.utils.records.TeacherPendency;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "teacher-home", value = "/teacher")
public class HomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        AuthenticatedUser user;
        Teacher teacher = null;
        Map<Integer, StudentSubject> studentSubjectMap = new HashMap<>();
        HttpSession session = request.getSession(false);
        StudentsPerformance studentsPerformance;
        List<TeacherPendency> pendencies;


        request.setAttribute("studentsPerformance", new StudentsPerformance(0, 0, 0));

        try {
            user = (AuthenticatedUser) session.getAttribute("user");
            teacher = (Teacher) session.getAttribute("teacher");

            // Only administrators can register students
            if (user.role() != UserRoleEnum.TEACHER) {
                request.getRequestDispatcher("/index.jsp")
                        .forward(request, response);
                return;
            }

        } catch (NullPointerException npe) {
            // User not authenticated or session attribute missing
            request.setAttribute("error", "Sessão expirada, faça login novamente");
            request.getRequestDispatcher("/index.jsp")
                    .forward(request, response);
            return;
        }

        if (teacher == null) {
            try {
                TeacherDAO teacherDAO = new TeacherDAO();

                teacher = teacherDAO.findById(user.id());

                session.setAttribute("teacher", teacher);
                request.setAttribute("teacher", teacher);
            } catch (DataException de) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                request.setAttribute("error", de.getMessage());
                request.getRequestDispatcher("/WEB-INF/views/teacher/index.jsp").forward(request, response);
                return;
            } catch (NotFoundException nfe) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                request.setAttribute("error", nfe.getMessage());
                request.getRequestDispatcher("/WEB-INF/views/teacher/index.jsp").forward(request, response);
                return;
            } catch (ValidationException ve) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                request.setAttribute("error", ve.getMessage());
                request.getRequestDispatcher("/WEB-INF/views/teacher/index.jsp").forward(request, response);
                return;
            }
        }
        request.setAttribute("teacher", teacher);

        try {
            StudentSubjectDAO studentSubjectDAO = new StudentSubjectDAO();

            studentSubjectMap = studentSubjectDAO.findStudentsThatRequireTeacher(teacher.getId());
            studentsPerformance = studentSubjectDAO.studentsPerformance(teacher.getId());
            pendencies = studentSubjectDAO.teacherPendency(teacher.getId());
        } catch (DataException de) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("error", de.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/teacher/index.jsp").forward(request, response);
            return;
        } catch (ValidationException ve) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("error", ve.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/teacher/index.jsp").forward(request, response);
            return;
        }

        request.setAttribute("studentsToHelpMap", studentSubjectMap);
        request.setAttribute("studentsPerformance", studentsPerformance);
        request.setAttribute("pendencies", pendencies);
        request.getRequestDispatcher("/WEB-INF/views/teacher/index.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
