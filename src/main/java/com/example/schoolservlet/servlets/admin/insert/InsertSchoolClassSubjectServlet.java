package com.example.schoolservlet.servlets.admin.insert;

import com.example.schoolservlet.daos.SchoolClassSubjectDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.utils.AccessValidation;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;

/**
 * Servlet responsible for handling the creation of a relationship between
 * a school class and a subject.
 * <p>
 * This servlet processes form submissions that associate a subject with a
 * specific class and assign one or more teachers responsible for teaching
 * that subject. The operation also triggers the creation of related records
 * through the corresponding DAO.
 * </p>
 * <p>
 * Access to this endpoint is restricted to administrators.
 * </p>
 */
@WebServlet(name = "admin-insert-school-class-subject", value = "/admin/school-class/subject/insert")
public class InsertSchoolClassSubjectServlet extends HttpServlet {
    private final SchoolClassSubjectDAO schoolClassSubjectDAO = new SchoolClassSubjectDAO();

    /**
     * Handles HTTP POST requests to associate a subject with a school class
     * and assign teachers to that subject.
     * <p>
     * The method retrieves parameters from the request, validates the required
     * fields, and invokes the data access layer to create the relationship
     * between the class, subject, and teachers. In case of validation or data
     * access errors, appropriate error messages are stored in the session and
     * the user is redirected to the subject listing page of the selected class.
     * </p>
     *
     * @param request the {@link HttpServletRequest} containing the client request
     * @param response the {@link HttpServletResponse} used to send the response
     * @throws ServletException if a servlet-related error occurs
     * @throws IOException if an input or output error occurs during request processing
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!AccessValidation.isAdmin(request, response)) return;

        String classIdParam   = request.getParameter("classId");
        String subjectIdParam = request.getParameter("subjectId");
        String[] teacherIds   = request.getParameterValues("teacherIds");
        HttpSession session = request.getSession(false);

        if (subjectIdParam == null || subjectIdParam.isBlank()) {
            session.setAttribute("error", "ID da matéria é obrigatório");
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
        } catch (DataException | ValidationException e) {
            session.setAttribute("error", e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/school-class/subject/find-many?classId=" + classId);
    }
}