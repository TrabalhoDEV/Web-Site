package com.example.schoolservlet.servlets.admin.delete;

import com.example.schoolservlet.daos.SchoolClassDAO;
import com.example.schoolservlet.daos.StudentDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.NotFoundException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.SchoolClass;
import com.example.schoolservlet.utils.AccessValidation;
import com.example.schoolservlet.utils.ErrorHandler;
import com.example.schoolservlet.utils.InputValidation;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "DeleteSchoolClassServlet", value = "/admin/school-class/delete")
public class DeleteSchoolClassServlet extends HttpServlet {
    private final String responsePath = "/admin/school-class/find-many";

    /**
     * Handles HTTP GET requests for deleting a school class.
     *
     * <p>This method performs access validation to ensure the user is an admin,
     * validates the 'id' parameter, and checks if the school class exists.
     * If the class has no students, it deletes the class; otherwise, it forwards
     * to a JSP page showing details preventing deletion. Errors are captured and
     * stored in the session for feedback.</p>
     *
     * @param request the HttpServletRequest object containing client request data
     * @param response the HttpServletResponse object used to send responses
     * @throws ServletException if a servlet-related error occurs
     * @throws IOException if an input or output error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        if (!AccessValidation.isAdmin(request, response)) return;

        String idParam = request.getParameter("id");
        HttpSession session = request.getSession(false);

        try {
            InputValidation.validateIsNull("id", idParam);
            int id = Integer.parseInt(idParam);
            InputValidation.validateId(id, "id");

            SchoolClassDAO schoolClassDAO = new SchoolClassDAO();
            schoolClassDAO.findById(id);

            StudentDAO studentDAO = new StudentDAO();

            int howManyStudents = studentDAO.countBySchoolClass(id);

            if (howManyStudents <= 0){
                schoolClassDAO.delete(id);
            } else {
                getAllData(request, response, id);
                request.setAttribute("id", id);
                request.getRequestDispatcher("/WEB-INF/views/admin/delete/school-class.jsp").forward(request, response);
                return;
            }

        } catch (NumberFormatException nfe){
            session.setAttribute("error", "ID deve ser um número");
        } catch (DataException | NotFoundException | ValidationException e){
            session.setAttribute("error", e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + responsePath);
    }

    /**
     * Handles HTTP POST requests for deleting a school class and optionally
     * reassigning its students to a new class.
     *
     * <p>This method performs admin access validation, validates input parameters,
     * and ensures that the source and destination class IDs are different. It updates
     * students to the new class if applicable, deletes the original class, and
     * handles errors by forwarding back to a JSP page with relevant messages.</p>
     *
     * @param request the HttpServletRequest object containing client request data
     * @param response the HttpServletResponse object used to send responses
     * @throws ServletException if a servlet-related error occurs
     * @throws IOException if an input or output error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        if (!AccessValidation.isAdmin(request, response)) return;

        String idParam = request.getParameter("id");
        String newIdParam = request.getParameter("newId");
        HttpSession session = request.getSession(false);

        try {
            InputValidation.validateIsNull("id", idParam);
            InputValidation.validateIsNull("id da nova turma", newIdParam);
            int id = Integer.parseInt(idParam);
            int newId = Integer.parseInt(newIdParam);

            if (id == newId) throw new ValidationException("Essa turma será deletada, escolha outra");

            InputValidation.validateId(id, "id");

            SchoolClassDAO schoolClassDAO = new SchoolClassDAO();
            schoolClassDAO.findById(id);

            StudentDAO studentDAO = new StudentDAO();

            studentDAO.updateManyIdSchoolClass(id, newId);

            schoolClassDAO.delete(id);
        } catch (NumberFormatException nfe){
            session.setAttribute("error", "ID deve ser um número");
            getAllData(request, response, 0);
            request.setAttribute("id", idParam);
            request.getRequestDispatcher("/WEB-INF/views/admin/delete/school-class.jsp").forward(request, response);
            return;
        } catch (DataException | NotFoundException | ValidationException e){
            session.setAttribute("error", e.getMessage());
            getAllData(request, response, Integer.parseInt(idParam));
            request.setAttribute("id", idParam);
            request.getRequestDispatcher("/WEB-INF/views/admin/delete/school-class.jsp").forward(request, response);
            return;
        }

        response.sendRedirect(request.getContextPath() + responsePath);
    }

    /**
     * Retrieves all school classes and sets them as a request attribute.
     *
     * <p>This helper method queries the database for all school classes and attaches
     * the list to the request. If no classes are available for reassignment or an error
     * occurs during retrieval, an error message is set as a request attribute.</p>
     *
     * @param request the HttpServletRequest object to set attributes on
     * @param response the HttpServletResponse object (not used but kept for consistency)
     * @param id the ID of the class to exclude from reassignment checks
     */
    private void getAllData(HttpServletRequest request, HttpServletResponse response, int id){
        SchoolClassDAO schoolClassDAO = new SchoolClassDAO();

        try {
            List<SchoolClass> schoolClasses = schoolClassDAO.findAll();
            request.setAttribute("schoolClasses", schoolClasses);

            if (schoolClasses.isEmpty() || (schoolClasses.size() == 1 && schoolClasses.get(0).getId() == id)){
                request.setAttribute("error", "Nenhuma turma disponível para troca");
            }
        } catch (DataException de){
            request.setAttribute("error", de.getMessage());
        }
    }
}
