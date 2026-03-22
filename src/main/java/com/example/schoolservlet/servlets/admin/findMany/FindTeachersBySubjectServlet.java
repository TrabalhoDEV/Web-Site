package com.example.schoolservlet.servlets.admin.findMany;

import com.example.schoolservlet.daos.SubjectTeacherDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.models.Teacher;
import com.example.schoolservlet.utils.AccessValidation;
import com.example.schoolservlet.utils.OutputFormatService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * Servlet responsible for providing the list of teachers associated with a
 * specific subject through a JSON response.
 * <p>
 * This endpoint is used to dynamically retrieve teachers linked to a subject,
 * typically for administrative interfaces that require subject-teacher
 * relationship data.
 * </p>
 * <p>
 * Access to this endpoint is restricted to administrators.
 * </p>
 */
@WebServlet(name = "admin-find-teachers-by-subject", value = "/admin/subject/teachers")
public class FindTeachersBySubjectServlet extends HttpServlet {
    private final SubjectTeacherDAO subjectTeacherDAO = new SubjectTeacherDAO();

    /**
     * Handles HTTP GET requests to retrieve teachers assigned to a specific subject.
     * <p>
     * The method reads the subject identifier from the request parameters,
     * retrieves the corresponding teachers from the data access layer,
     * formats the data as a JSON array, and writes the response to the client.
     * If the subject identifier is missing, invalid, or an error occurs,
     * an empty JSON array is returned.
     * </p>
     *
     * @param request the {@link HttpServletRequest} containing the client request
     * @param response the {@link HttpServletResponse} used to return the JSON response
     * @throws ServletException if a servlet-related error occurs
     * @throws IOException if an input or output error occurs while writing the response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (!AccessValidation.isAdmin(request, response)) return;

        String subjectIdParam = request.getParameter("subjectId");

        if (subjectIdParam == null || subjectIdParam.isBlank()) {
            response.getWriter().write("[]");
            return;
        }

        try {
            int subjectId = Integer.parseInt(subjectIdParam);
            List<Teacher> teachers = subjectTeacherDAO.findBySubject(subjectId);

            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < teachers.size(); i++) {
                Teacher t = teachers.get(i);
                json.append("{\"id\":").append(t.getId())
                        .append(",\"name\":\"").append(OutputFormatService.formatName(t.getName())).append("\"}");
                if (i < teachers.size() - 1) json.append(",");
            }
            json.append("]");

            response.getWriter().write(json.toString());

        } catch (NumberFormatException e) {
            response.getWriter().write("[]");
        } catch (DataException de) {
            response.setStatus(de.getStatus());
            response.getWriter().write("[]");
        }
    }
}