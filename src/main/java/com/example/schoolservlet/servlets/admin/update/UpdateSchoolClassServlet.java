package com.example.schoolservlet.servlets.admin.update;

import com.example.schoolservlet.daos.SchoolClassDAO;
import com.example.schoolservlet.daos.SchoolClassSubjectDAO;
import com.example.schoolservlet.daos.StudentSubjectDAO;
import com.example.schoolservlet.daos.SubjectDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.InvalidNumberException;
import com.example.schoolservlet.exceptions.NotFoundException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.SchoolClass;
import com.example.schoolservlet.models.SchoolClassSubject;
import com.example.schoolservlet.models.Subject;
import com.example.schoolservlet.utils.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@WebServlet(name = "UpdateSchoolClassSubjectServlet", value = "/admin/school-class/update")
public class UpdateSchoolClassServlet extends HttpServlet {
    private SchoolClassDAO schoolClassDAO = new SchoolClassDAO();
    private SubjectDAO subjectDAO = new SubjectDAO();
    private SchoolClassSubjectDAO schoolClassSubjectDAO = new SchoolClassSubjectDAO();
    private StudentSubjectDAO studentSubjectDAO = new StudentSubjectDAO();

    /**
     * Handles GET requests to load a school class and its subjects for update.
     *
     * <p>Validates the school class ID, fetches current data, and forwards to the update JSP page.
     * If the ID is invalid or an error occurs, sets an error message and redirects or forwards accordingly.</p>
     *
     * @param request  HttpServletRequest containing client request data.
     * @param response HttpServletResponse used to forward to JSP or redirect on error.
     * @throws ServletException If a servlet error occurs during forwarding.
     * @throws IOException      If an I/O error occurs during forwarding or redirect.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");

        if (!AccessValidation.isAdmin(request, response)) return;

        String idParam = request.getParameter("id");

        try {
            if (idParam == null || idParam.isEmpty()) {
                throw new InvalidNumberException(idParam, "O ID não pode estar vazio");
            }

            int id = Integer.parseInt(idParam);
            InputValidation.validateId(id, "id");

            loadUpdateData(request, id);

            request.getRequestDispatcher("/WEB-INF/views/admin/update/school-class.jsp")
                    .forward(request, response);

        } catch (NumberFormatException nfe) {
            request.getSession(false).setAttribute("error", "ID precisa ser um valor numérico");
            response.sendRedirect(request.getContextPath() + "/admin/school-class/find-many");

        } catch (NotFoundException nfe) {
            nfe.printStackTrace();
            request.getSession(false).setAttribute("error", nfe.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/school-class/find-many");

        } catch (ValidationException | DataException e) {
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/update/school-class.jsp")
                    .forward(request, response);
        }
    }

    /**
     * Handles POST requests to update a school class and its associated subjects.
     *
     * <p>Validates input, updates the school class name if changed, adds new subject associations,
     * removes unassigned subjects, updates student-subject relationships, and redirects to the listing page.
     * Errors are handled by forwarding to the update JSP with an error message.</p>
     *
     * @param request  HttpServletRequest containing form data for school class and subjects.
     * @param response HttpServletResponse used to redirect or forward on error.
     * @throws ServletException If a servlet error occurs during forwarding.
     * @throws IOException      If an I/O error occurs during forwarding or redirect.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");

        if (!AccessValidation.isAdmin(request, response)) return;

        int schoolClassId = -1;

        try {
            String idParam = request.getParameter("id");

            if (idParam == null || idParam.isEmpty()) {
                throw new InvalidNumberException(idParam, "O ID não pode estar vazio");
            }

            schoolClassId = Integer.parseInt(idParam);
            InputValidation.validateId(schoolClassId, "id");

            SchoolClass schoolClass = schoolClassDAO.findById(schoolClassId);

            String name = request.getParameter("schoolYear");

            InputValidation.validateSchoolClassName(name);

            name = InputNormalizer.normalizeName(name);

            if (!name.equals(schoolClass.getSchoolYear())){
                schoolClass.setSchoolYear(name);
                schoolClassDAO.update(schoolClass);
            }

            response.sendRedirect(request.getContextPath() + "/admin/school-class/find-many");

        } catch (NumberFormatException nfe) {
            request.getSession(false).setAttribute("error", "ID precisa ser um valor numérico");
            response.sendRedirect(request.getContextPath() + "/admin/school-class/find-many");

        } catch (NotFoundException | InvalidNumberException e) {
            e.printStackTrace();
            request.getSession(false).setAttribute("error", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/school-class/find-many");

        } catch (ValidationException | DataException e) {
            e.printStackTrace();
            loadSafely(request, schoolClassId);
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/update/school-class.jsp")
                    .forward(request, response);
        }
    }

    /**
     * Loads data required for updating a school class and sets it as request attributes.
     *
     * <p>Fetches the school class, all subjects, and subjects currently assigned to the class,
     * then sets them as attributes for use in the update JSP page.</p>
     *
     * @param request       HttpServletRequest to store attributes for the JSP page.
     * @param schoolClassId ID of the school class to load.
     * @throws DataException       If a data access error occurs.
     * @throws NotFoundException   If the school class is not found.
     * @throws ValidationException If input validation fails.
     */
    private void loadUpdateData(HttpServletRequest request, int schoolClassId)
            throws DataException, NotFoundException, ValidationException {

        SchoolClass schoolClass = schoolClassDAO.findById(schoolClassId);

        request.setAttribute("schoolClass", schoolClass);
    }

    /**
     * Safely loads update data for a school class, ignoring any exceptions.
     *
     * <p>Attempts to call {@link #loadUpdateData(HttpServletRequest, int)} and suppresses all exceptions
     * to prevent breaking the request flow.</p>
     *
     * @param request       HttpServletRequest to store attributes for the JSP page.
     * @param schoolClassId ID of the school class to load.
     */
    private void loadSafely(HttpServletRequest request, int schoolClassId) {
        try {
            loadUpdateData(request, schoolClassId);
        } catch (Exception ignored) {}
    }
}