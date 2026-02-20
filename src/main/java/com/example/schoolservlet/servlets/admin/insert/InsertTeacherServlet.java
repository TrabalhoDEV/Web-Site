package com.example.schoolservlet.servlets.admin.insert;

import com.example.schoolservlet.daos.TeacherDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.RequiredFieldException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.exceptions.ValueAlreadyExistsException;
import com.example.schoolservlet.models.Teacher;
import com.example.schoolservlet.utils.AccessValidation;
import com.example.schoolservlet.utils.FieldAlreadyUsedValidation;
import com.example.schoolservlet.utils.InputNormalizer;
import com.example.schoolservlet.utils.InputValidation;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Servlet respnsible for registering new teachers in the system.
 * This endpoint handles teacher registration.
 * Only administrators are allowed to access this funcionality
 */
@WebServlet(name = "admin-add-teacher", value = "/admin/add-teacher")
public class InsertTeacherServlet extends HttpServlet {
    /**
     * Handles POST requests for teacher registration.
     *
     * @param request  the HTTP request containing name, email, username, password parameters
     * @param response the HTTP response object
     * @throws ServletException if servlet processing fails
     * @throws IOException      if I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!AccessValidation.isAdmin(request, response)) return;

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        name=InputNormalizer.normalizeName(name);
        email=InputNormalizer.normalizeEmail(email);
        username=InputNormalizer.normalizeUserName(username);


        try {
            InputValidation.validateTeacherName(name);
            InputValidation.validateEmail(email);
            InputValidation.validateUserName(username);
            InputValidation.validatePassword(password);

            FieldAlreadyUsedValidation.exists("teacher","email",email);
            FieldAlreadyUsedValidation.exists("teacher","username",username);

        }catch (RequiredFieldException rfe){
            request.setAttribute("error", rfe.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/index.jsp")
                    .forward(request, response);
            return;
        } catch (ValueAlreadyExistsException vaee) {
            request.setAttribute("error", vaee.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/index.jsp")
                    .forward(request, response);
            return;
        } catch (ValidationException ve){
            request.setAttribute("error",ve.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/index.jsp").forward(request,response);
            return;
        }catch (DataException de) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("error", de.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/index.jsp")
                    .forward(request, response);
            return;
        }


        Teacher teacher = new Teacher();
        teacher.setName(name);
        teacher.setEmail(email);
        teacher.setUsername(username);
        teacher.setPassword(password);
        TeacherDAO teacherDAO = new TeacherDAO();

        try {
            teacherDAO.create(teacher);
            request.setAttribute("success",true);
        } catch (ValidationException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("error", e.getMessage());
        } catch (DataException de){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("error", de.getMessage());
        }

        request.getRequestDispatcher("/WEB-INF/views/admin/index.jsp")
                .forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(!AccessValidation.isAdmin(request,response)) return;
        request.getRequestDispatcher("/WEB-INF/views/admin/index.jsp").forward(request, response);
    }
}
