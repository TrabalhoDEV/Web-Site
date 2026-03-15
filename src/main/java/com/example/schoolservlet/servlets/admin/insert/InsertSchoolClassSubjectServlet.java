package com.example.schoolservlet.servlets.admin.insert;

import com.example.schoolservlet.daos.SchoolClassSubjectDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.utils.AccessValidation;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;

@WebServlet(name = "admin-insert-school-class-subject", value = "/admin/school-class/subject/insert")
public class InsertSchoolClassSubjectServlet extends HttpServlet {
    private final SchoolClassSubjectDAO schoolClassSubjectDAO = new SchoolClassSubjectDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!AccessValidation.isAdmin(request, response)) return;

        String classIdParam   = request.getParameter("classId");
        String subjectIdParam = request.getParameter("subjectId");
        String[] teacherIds   = request.getParameterValues("teacherIds");
        HttpSession session = request.getSession(false);

        if (classIdParam == null || classIdParam.isBlank() ||
                subjectIdParam == null || subjectIdParam.isBlank()) {
            session.setAttribute("error", "ID da matéria e da turma não podem ser nulos");
            response.sendRedirect(request.getContextPath() + "/admin/school-class/subject/find-many?classId=" + classIdParam);
            return;
        }

        if (teacherIds == null || teacherIds.length == 0){
            session.setAttribute("error", "É necessário escolher um professor para ministrar essa matéria");
            response.sendRedirect(request.getContextPath() + "/admin/school-class/subject/find-many?classId=" + classIdParam);
            return;
        }

        int classId;
        int subjectId;
        try {
            classId   = Integer.parseInt(classIdParam);
            subjectId = Integer.parseInt(subjectIdParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/school-class/find-many");
            return;
        }

        try {
            schoolClassSubjectDAO.createWithRelations(classId, subjectId, teacherIds);
        } catch (DataException de) {
            session.setAttribute("error", de.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/school-class/subject/find-many?classId=" + classId);
    }
}