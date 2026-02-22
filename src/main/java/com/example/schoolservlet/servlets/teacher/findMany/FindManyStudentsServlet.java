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
import com.example.schoolservlet.utils.PaginationUtilities;
import com.example.schoolservlet.utils.records.AuthenticatedUser;
import com.example.schoolservlet.utils.records.TeacherStudentGrades;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "teacher-find-many-students", value = "/teacher/students")
public class FindManyStudentsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        if (!AccessValidation.isTeacher(request, response)) return;

        AuthenticatedUser user;
        try {
            HttpSession session = request.getSession(false);
            user = (AuthenticatedUser) session.getAttribute("user");
        } catch (NullPointerException npe) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            request.setAttribute("error", "Sessão expirada, faça login novamente");
            request.getRequestDispatcher("/index.jsp")
                    .forward(request, response);
            return;
        }

        TeacherDAO teacherDAO = new TeacherDAO();
        try{
            int amountOfStudents = new TeacherDAO().totalCountOfStudentsForTeacher(user.id());
            int totalPages = PaginationUtilities.calculateTotalPages(amountOfStudents, Constants.MAX_TAKE);

            int page = PaginationUtilities.extractNextPage(request);
            int skip = page * Constants.MAX_TAKE;

            Map<Integer, TeacherStudentGrades> teacherStudentGradesMap = teacherDAO.findManyStudentsByTeacherID(skip, Constants.MAX_TAKE, user.id());

            request.setAttribute("studentMap", teacherStudentGradesMap);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
        } catch (DataException e){
            ErrorHandler.forward(request, response, e.getStatus(), e.getMessage(), "/WEB-INF/views/teacher/student/find-many.jsp");
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        request.getRequestDispatcher("/WEB-INF/views/teacher/student/find-many.jsp").forward(request, response);
    }
}
