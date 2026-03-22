package com.example.schoolservlet.servlets.admin.insert;

import com.example.schoolservlet.daos.SchoolClassDAO;
import com.example.schoolservlet.daos.SchoolClassSubjectDAO;
import com.example.schoolservlet.daos.SubjectDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.NotFoundException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.SchoolClass;
import com.example.schoolservlet.models.SchoolClassSubject;
import com.example.schoolservlet.models.Subject;
import com.example.schoolservlet.utils.AccessValidation;
import com.example.schoolservlet.utils.ErrorHandler;
import com.example.schoolservlet.utils.FieldAlreadyUsedValidation;
import com.example.schoolservlet.utils.InputValidation;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "InsertSchoolClassServlet", value = "/admin/school-class/insert")
public class InsertSchoolClassServlet extends HttpServlet {
    private SchoolClassDAO schoolClassDAO = new SchoolClassDAO();
    private SubjectDAO subjectDAO = new SubjectDAO();

    /**
     * Handles HTTP GET requests for the insertion of a school class.
     *
     * <p>This method performs the following steps:</p>
     * <ol>
     *     <li>Sets the response content type to "text/html".</li>
     *     <li>Validates if the current user has administrative privileges via {@link AccessValidation#isAdmin}.</li>
     *     <li>If the user is authorized, retrieves all subjects using {@link #getAllSubjects(HttpServletRequest)}.</li>
     *     <li>Forwards the request and response to the JSP page for rendering the school class insertion form
     *         located at "/WEB-INF/views/admin/insert/school-class.jsp".</li>
     * </ol>
     *
     * @param request  The {@link HttpServletRequest} object containing client request information.
     * @param response The {@link HttpServletResponse} object used to send a response to the client.
     * @throws ServletException If an input or output error is detected when the servlet handles the GET request.
     * @throws IOException      If an input or output exception occurs during request forwarding or response writing.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        if (!AccessValidation.isAdmin(request, response)) return;

        request.getRequestDispatcher("/WEB-INF/views/admin/insert/school-class.jsp").forward(request, response);
    }

    /**
     * Handles HTTP POST requests for creating a new school class.
     *
     * <p>This method performs the following operations:</p>
     * <ol>
     *     <li>Sets the response content type to "text/html".</li>
     *     <li>Validates if the current user is an administrator using {@link AccessValidation#isAdmin}.</li>
     *     <li>Retrieves and validates input parameters, including the class name and selected subject IDs.</li>
     *     <li>Ensures the class name is not null, trimmed, lowercased, and validated via {@link InputValidation}.</li>
     *     <li>Checks for uniqueness of the school class name using {@link FieldAlreadyUsedValidation}.</li>
     *     <li>Validates that the submitted subject IDs exist and creates a list of {@link SchoolClassSubject} associations.</li>
     *     <li>Persists the new {@link SchoolClass} using {@link SchoolClassDAO#create} and associates subjects via {@link SchoolClassSubjectDAO#createMany}.</li>
     *     <li>Redirects to the school class listing page upon successful creation.</li>
     *     <li>Handles {@link DataException}, {@link NotFoundException}, and {@link ValidationException} by forwarding the request back to the JSP page with appropriate error messages via {@link ErrorHandler}.</li>
     * </ol>
     *
     * @param request  The {@link HttpServletRequest} containing the form submission data.
     * @param response The {@link HttpServletResponse} used to send the redirect or error page.
     * @throws ServletException If a servlet-specific error occurs during processing.
     * @throws IOException      If an I/O error occurs during request forwarding or response redirection.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        if (!AccessValidation.isAdmin(request, response)) return;

        String name = request.getParameter("name");

        try{
            InputValidation.validateIsNull("turma", name);

            name = name.trim().toLowerCase();

            InputValidation.validateSchoolClassName(name);

            FieldAlreadyUsedValidation.exists("school_class", "school_year", "nome da turma", name);

            SchoolClass schoolClass = new SchoolClass();

            schoolClass.setSchoolYear(name);

            this.schoolClassDAO.create(schoolClass);

            response.sendRedirect(request.getContextPath() + "/admin/school-class/find-many");
        } catch (DataException | ValidationException e){
            ErrorHandler.forward(request, response, e.getStatus(), e.getMessage(), "/WEB-INF/views/admin/insert/school-class.jsp");
        }
    }
}
