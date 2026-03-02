package com.example.schoolservlet.servlets.admin.delete;

import com.example.schoolservlet.daos.StudentDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.NotFoundException;
import com.example.schoolservlet.exceptions.RequiredFieldException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.Student;
import com.example.schoolservlet.models.Subject;
import com.example.schoolservlet.utils.AccessValidation;
import com.example.schoolservlet.utils.ErrorHandler;
import com.example.schoolservlet.utils.InputValidation;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;

@WebServlet(name = "DeleteStudentServlet", value = "/admin/student/delete")
public class DeleteStudentServlet extends HttpServlet {
    private final StudentDAO studentDAO = new StudentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        if (!AccessValidation.isAdmin(request, response)) return;

        String idParam = request.getParameter("id");
        HttpSession session = request.getSession(false);
        int id = 0;

        try{
            InputValidation.validateIsNull("id", idParam);
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException | RequiredFieldException e){
        }

        try{
            Student student = studentDAO.findById(id);

            studentDAO.delete(id);
        } catch (DataException | ValidationException | NotFoundException e){
            session.setAttribute("error", e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/student/find-many");
    }
}
