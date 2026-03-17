package com.example.schoolservlet.servlets.student.generate;

import com.example.schoolservlet.daos.SchoolClassDAO;
import com.example.schoolservlet.daos.StudentDAO;
import com.example.schoolservlet.daos.StudentSubjectDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.NotFoundException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.SchoolClass;
import com.example.schoolservlet.models.Student;
import com.example.schoolservlet.models.StudentSubject;
import com.example.schoolservlet.utils.AccessValidation;
import com.example.schoolservlet.utils.ErrorHandler;
import com.example.schoolservlet.utils.GenerateBulletinPdf;
import com.example.schoolservlet.utils.records.AuthenticatedUser;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

@WebServlet(name = "GenerateBulletinPdfServlet", value = "/student/bulletin/generate-pdf")
public class GenerateBulletinPdfServlet extends HttpServlet {
    private final StudentSubjectDAO studentSubjectDAO = new StudentSubjectDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final SchoolClassDAO schoolClassDAO = new SchoolClassDAO();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!AccessValidation.isStudent(request, response)) return;

        HttpSession session = request.getSession(false);
        if (session == null) {
            ErrorHandler.forward(request, response, HttpServletResponse.SC_UNAUTHORIZED, "Sessão expirada, faça login novamente", "/index.jsp");
            return;
        }

        AuthenticatedUser user = (AuthenticatedUser) session.getAttribute("user");

        if (user == null){
            ErrorHandler.forward(request, response, HttpServletResponse.SC_UNAUTHORIZED, "Sessão expirada, faça login novamente", "/index.jsp");
            return;
        }

        response.setContentType("application/pdf");

        try{
            int take = studentSubjectDAO.totalCount(user.id());

            Map<Integer, List<StudentSubject>> studentSubjectsMap = studentSubjectDAO.findManyByStudentId(0, take, user.id(), null);
            Student student = studentDAO.findById(user.id());
            SchoolClass schoolClass = schoolClassDAO.findById(student.getIdSchoolClass());

            System.out.println(take);
            System.out.println(student);

            GenerateBulletinPdf.generatePDF(request, response, student, studentSubjectsMap.get(user.id()), schoolClass);
        } catch (ValidationException | NotFoundException | DataException e){
            ErrorHandler.forward(request, response, e.getStatus(), e.getMessage(), "/WEB-INF/views/student/bulletin.jsp");
        }
    }
}
