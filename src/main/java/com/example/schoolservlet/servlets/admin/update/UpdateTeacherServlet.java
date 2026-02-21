package com.example.schoolservlet.servlets.admin.update;

import com.example.schoolservlet.daos.TeacherDAO;
import com.example.schoolservlet.exceptions.*;
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
 * Servlet responsible for updating teachers in the system.
 * This endpoint handles teacher updating.
 */
@WebServlet(name = "admin-update-teacher",value = "/admin/teacher/update")
public class UpdateTeacherServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        if (!AccessValidation.isAdmin(request, response)) return;

        try{
            String idParam = request.getParameter("id");
            if(idParam == null || idParam.isEmpty()){
                throw new InvalidNumberException(idParam,"O ID não pode estar vazio");
            }

            int id = Integer.parseInt(idParam);
            TeacherDAO teacherDAO = new TeacherDAO();
            Teacher teacher = teacherDAO.findById(id);
            request.setAttribute("teacher",teacher);

        } catch (NumberFormatException nfe){
            request.setAttribute("error",nfe.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/update/teacher.jsp").forward(request, response);
            return;

        } catch (DataException de){
            request.setAttribute("error", de.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/update/teacher.jsp").forward(request, response);
            return;
        } catch (NotFoundException nfe){
            request.setAttribute("error", nfe.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/findmany/teacher.jsp").forward(request, response);
            return;
        } catch (ValidationException ve){
            request.setAttribute("error", ve.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/update/teacher.jsp").forward(request, response);
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/admin/update/teacher.jsp").forward(request, response);

    }

    /**
     * Handles POST requests for teacher updating.
     *
     * @param request  HTTP request containing name, email, username, password parameters
     * @param response HTTP response object
     * @throws ServletException if servlet processing fails
     * @throws IOException if I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!AccessValidation.isAdmin(request, response)) return;

        try{
            String idParam = request.getParameter("id");
            if(idParam == null || idParam.isEmpty()){
                throw new InvalidNumberException(idParam,"O ID não pode estar vazio");
            }
            int id = Integer.valueOf(idParam);
            TeacherDAO teacherDAO = new TeacherDAO();
            Teacher teacher = teacherDAO.findById(id);

            String name = InputNormalizer.normalizeName(request.getParameter("name"));
            String email = InputNormalizer.normalizeEmail(request.getParameter("email"));
            String username = InputNormalizer.normalizeUserName(request.getParameter("username"));
            String password = request.getParameter("password");

            InputValidation.validateTeacherName(name);
            InputValidation.validateEmail(email);
            InputValidation.validateUserName(username);

            if(!teacher.getEmail().equals(email)){
                FieldAlreadyUsedValidation.exists("teacher","email",email);
            }

            if(!teacher.getUsername().equals(username)){
                FieldAlreadyUsedValidation.exists("teacher","username",username);
            }

            teacher.setName(name);
            teacher.setEmail(email);
            teacher.setUsername(username);

            teacherDAO.update(teacher);

            if (password != null && !password.isBlank()) {
                InputValidation.validatePassword(password);
                teacherDAO.updatePassword(id, password);
            }

            response.sendRedirect(request.getContextPath()+ "/admin/teacher/find-many");

        }  catch (DataException de){
            request.setAttribute("error", de.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/update/teacher.jsp").forward(request, response);
            return;
        } catch (NotFoundException nfe){
            request.setAttribute("error", nfe.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/update/teacher.jsp").forward(request, response);
            return;
        } catch (ValueAlreadyExistsException vaee){
            request.setAttribute("error", vaee.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/update/teacher.jsp").forward(request, response);
            return;
        } catch (ValidationException ve){
            request.setAttribute("error", ve.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/update/teacher.jsp").forward(request, response);
            return;
        }
    }
}
