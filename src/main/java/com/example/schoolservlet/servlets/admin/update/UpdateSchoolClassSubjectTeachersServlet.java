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

/**
 * Servlet responsible for updating the teachers assigned to a subject
 * within a specific school class.
 * <p>
 * This servlet allows administrators to view and modify which teachers
 * are responsible for teaching a given subject in a class. It retrieves
 * the available teachers for the subject and the teachers currently
 * assigned to the class-subject relation.
 * </p>
 * <p>
 * Access to this endpoint is restricted to administrators.
 * </p>
 */
@WebServlet(name = "admin-update-school-class-subject", value = "/admin/school-class/subject/update")
public class UpdateSchoolClassSubjectTeachersServlet extends HttpServlet {
    private final SchoolClassSubjectDAO schoolClassSubjectDAO = new SchoolClassSubjectDAO();
    private final String responsePath = "/WEB-INF/views/admin/update/school-class-subject.jsp";

    /**
     * Handles HTTP GET requests to load the update page for the teachers
     * assigned to a subject in a specific class.
     * <p>
     * The method validates the received parameters, retrieves the list of
     * teachers available to teach the subject, and the teachers currently
     * assigned to the class. These data are sent to the view to populate
     * the update form.
     * </p>
     *
     * @param request the {@link HttpServletRequest} containing the client request
     * @param response the {@link HttpServletResponse} used to send the response
     * @throws ServletException if a servlet-related error occurs
     * @throws IOException if an input or output error occurs during request processing
     */
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

    /**
     * Handles HTTP POST requests to update the teachers assigned to a subject
     * within a specific school class.
     * <p>
     * The method validates the received parameters and updates the relationship
     * between the class, subject, and teachers using the data access layer.
     * If validation or persistence errors occur, an error message is stored
     * in the session and the user is redirected back to the subject list of
     * the selected class.
     * </p>
     *
     * @param request the {@link HttpServletRequest} containing the submitted form data
     * @param response the {@link HttpServletResponse} used to send the response
     * @throws ServletException if a servlet-related error occurs
     * @throws IOException if an input or output error occurs during request processing
     */
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