package com.example.schoolservlet.servlets.teacher.findMany;

import com.example.schoolservlet.daos.StudentDAO;
import com.example.schoolservlet.daos.StudentSubjectDAO;
import com.example.schoolservlet.daos.TeacherDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.Student;
import com.example.schoolservlet.utils.AccessValidation;
import com.example.schoolservlet.utils.Constants;
import com.example.schoolservlet.utils.ErrorHandler;
import com.example.schoolservlet.exceptions.NotFoundException;
import com.example.schoolservlet.models.StudentSubject;
import com.example.schoolservlet.models.Teacher;
import com.example.schoolservlet.utils.*;
import com.example.schoolservlet.utils.records.AuthenticatedUser;
import com.example.schoolservlet.utils.records.StudentsPerformanceCount;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "teacher-find-many-students", value = "/teacher/students")
public class FindManyStudentsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        if (!AccessValidation.isTeacher(request, response)) return;

        String pageParam = request.getParameter("page");
        String enrollmentFilter = request.getParameter("enrollment");
        int id = 0;
        boolean hasFilter = false;
        HttpSession session = request.getSession(false);
        AuthenticatedUser user;
        Teacher teacher = null;
        StudentSubjectDAO studentSubjectDAO = new StudentSubjectDAO();

        try {
            user = (AuthenticatedUser) session.getAttribute("user");
            teacher = (Teacher) session.getAttribute("teacher");
        } catch (NullPointerException npe) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            request.setAttribute("error", "Sessão expirada, faça login novamente");
            request.getRequestDispatcher("/index.jsp")
                    .forward(request, response);
            return;
        }

        request.setAttribute("studentSubjectMap",new HashMap<>());
        request.setAttribute("page", 1);
        request.setAttribute("totalPages", 1);
        request.setAttribute("studentsPerformanceCount", new StudentsPerformanceCount(0,0,0));

        if (teacher == null) {
            try {
                TeacherDAO teacherDAO = new TeacherDAO();

                teacher = teacherDAO.findById(user.id());

                session.setAttribute("teacher", teacher);
                request.setAttribute("teacher", teacher);
            } catch (DataException | NotFoundException | ValidationException e) {
                ErrorHandler.forward(request, response, e.getStatus(), e.getMessage(), "/WEB-INF/views/teacher/student/find-many.jsp");
                return;
            }
        }

        try{
            InputValidation.validateIsNull("matrícula", enrollmentFilter);
            enrollmentFilter = enrollmentFilter.trim();
            hasFilter = true;
            request.setAttribute("hasFilter", hasFilter);
        } catch (ValidationException e){}

        if (hasFilter){
            try {
                InputValidation.validateEnrollment(enrollmentFilter);
                id = InputNormalizer.normalizeEnrollment(enrollmentFilter);
            } catch (ValidationException e){
                ErrorHandler.forward(request, response, e.getStatus(), e.getMessage(), "/WEB-INF/views/teacher/student/find-many.jsp");
                return;
            }
        }

        int take = Constants.MAX_TAKE;
        int skip = 0;
        int page;
        int count = 0;

        request.setAttribute("studentMap", new HashMap<Integer, Student>());
        request.setAttribute("page", 1);
        request.setAttribute("totalPages", 1);

        try {
            page = Integer.parseInt(pageParam);
        } catch (NumberFormatException nfe) {
            page = 1;
        }

        Map<Integer, List<StudentSubject>> studentSubjectMap = new HashMap<>();

        int totalPages = 0;
        try {
            if (hasFilter) {
                count = studentSubjectDAO.countByStudentId(id);
                totalPages = Math.max(1, (int)Math.ceil((double) count / Constants.MAX_TAKE));

                page = Math.max(1, Math.min(page, totalPages));

                skip = take * (page - 1);
                studentSubjectMap = studentSubjectDAO.findManyByStudentId(skip, take, id);
            } else {
                count = studentSubjectDAO.countByTeacherId(teacher.getId());
                totalPages = Math.max(1, (int)Math.ceil((double) count / Constants.MAX_TAKE));

                page = Math.max(1, Math.min(page, totalPages));

                skip = take * (page - 1);
                studentSubjectMap = studentSubjectDAO.findManyByTeacherId(skip, take, teacher.getId());
            }

            StudentsPerformanceCount studentsPerformanceCount = studentSubjectDAO.studentsPerformanceCount(teacher.getId());
            request.setAttribute("studentsPerformanceCount", studentsPerformanceCount);

        } catch (DataException | ValidationException e){
            ErrorHandler.forward(request, response, e.getStatus(), e.getMessage(), "/WEB-INF/views/teacher/student/find-many.jsp");
            return;
        }

        request.setAttribute("studentSubjectMap", studentSubjectMap);
        request.setAttribute("page", page);
        request.setAttribute("totalPages", totalPages);

        request.getRequestDispatcher("/WEB-INF/views/teacher/student/find-many.jsp").forward(request, response);
    }
}
