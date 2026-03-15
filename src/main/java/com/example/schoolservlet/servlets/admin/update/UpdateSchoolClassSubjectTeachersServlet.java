package com.example.schoolservlet.servlets.admin.update;

import com.example.schoolservlet.daos.SchoolClassSubjectDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.Subject;
import com.example.schoolservlet.models.Teacher;
import com.example.schoolservlet.utils.AccessValidation;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "admin-update-school-class-subject", value = "/admin/school-class/subject/update")
public class UpdateSchoolClassSubjectTeachersServlet extends HttpServlet {
    private final SchoolClassSubjectDAO schoolClassSubjectDAO = new SchoolClassSubjectDAO();
    private final String responsePath = "/WEB-INF/views/admin/update/school-class-subject.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        if (!AccessValidation.isAdmin(request, response)) return;

        String subjectIdParam = request.getParameter("subjectId");
        String classIdParam   = request.getParameter("classId");
        HttpSession session   = request.getSession(false);

        if (subjectIdParam == null || subjectIdParam.isBlank() ||
                classIdParam   == null || classIdParam.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/admin/school-class/find-many");
            return;
        }

        int subjectId;
        int classId;
        try {
            subjectId = Integer.parseInt(subjectIdParam);
            classId   = Integer.parseInt(classIdParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/school-class/find-many");
            return;
        }

        try {
            List<Teacher> availableTeachers  = schoolClassSubjectDAO.findTeachersBySubject(subjectId);
            List<Integer> assignedTeacherIds = schoolClassSubjectDAO.findAssignedTeacherIds(subjectId, classId);

            request.setAttribute("availableTeachers",  availableTeachers);
            request.setAttribute("assignedTeacherIds", assignedTeacherIds);
            request.setAttribute("subjectId", subjectId);
            request.setAttribute("classId", classId);

        } catch (DataException de) {
            session.setAttribute("error", de.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/school-class/subject/find-many?classId=" + classId);
            return;
        }

        request.getRequestDispatcher(responsePath).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!AccessValidation.isAdmin(request, response)) return;

        String subjectIdParam = request.getParameter("subjectId");
        String classIdParam   = request.getParameter("classId");
        String[] teacherIds   = request.getParameterValues("teacherIds");
        HttpSession session   = request.getSession(false);

        if (subjectIdParam == null || subjectIdParam.isBlank() ||
                classIdParam   == null || classIdParam.isBlank()) {
            session.setAttribute("error", "ID da matéria e da turma não podem ser nulos");
            response.sendRedirect(request.getContextPath() + "/admin/school-class/subject/find-many?classId=" + classIdParam);
            return;
        }

        if (teacherIds == null || teacherIds.length == 0) {
            session.setAttribute("error", "É necessário escolher ao menos um professor para ministrar essa matéria");
            response.sendRedirect(request.getContextPath() + "/admin/school-class/subject/find-many?classId=" + classIdParam);
            return;
        }

        int subjectId;
        int classId;
        try {
            subjectId = Integer.parseInt(subjectIdParam);
            classId   = Integer.parseInt(classIdParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/school-class/find-many");
            return;
        }

        try {
            schoolClassSubjectDAO.updateTeacherRelations(classId, subjectId, teacherIds);
        } catch (DataException | ValidationException e) {
            session.setAttribute("error", e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/school-class/subject/find-many?classId=" + classId);
    }
}