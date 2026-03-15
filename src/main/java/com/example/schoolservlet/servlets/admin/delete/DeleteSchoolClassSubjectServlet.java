package com.example.schoolservlet.servlets.admin.delete;

import com.example.schoolservlet.daos.SchoolClassSubjectDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.RequiredFieldException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.utils.AccessValidation;
import com.example.schoolservlet.utils.InputValidation;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;

@WebServlet(name = "DeleteClassSubjectServlet", value = "/admin/school-class/subject/delete")
public class DeleteSchoolClassSubjectServlet extends HttpServlet {
    private final SchoolClassSubjectDAO schoolClassSubjectDAO = new SchoolClassSubjectDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        if (!AccessValidation.isAdmin(request, response)) return;

        String classIdParam = request.getParameter("classId");
        String subjectIdParam = request.getParameter("subjectId");
        HttpSession session = request.getSession(false);
        int classId = 0;
        int subjectId = 0;

        try {
            InputValidation.validateIsNull("classId", classIdParam);
            InputValidation.validateIsNull("subjectId", subjectIdParam);
            classId = Integer.parseInt(classIdParam);
            subjectId = Integer.parseInt(subjectIdParam);
        } catch (RequiredFieldException e) {
            session.setAttribute("error", e.getMessage());
        } catch (NumberFormatException e){
            session.setAttribute("error", "ID da turma e da matéria precisam ser informados");
            response.sendRedirect(request.getContextPath() + "/admin/school-class/find-many");
        }

        try {
            schoolClassSubjectDAO.deleteSubjectFromClassAndStudents(classId, subjectId);
        } catch (DataException | ValidationException e) {
            session.setAttribute("error", e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/school-class/subject/find-many?classId=" + classIdParam);
    }
}