package com.example.schoolservlet.servlets.admin.findMany;

import com.example.schoolservlet.daos.SchoolClassDAO;
import com.example.schoolservlet.daos.SubjectDAO;
import com.example.schoolservlet.daos.TeacherDAO;
import com.example.schoolservlet.exceptions.*;
import com.example.schoolservlet.models.SchoolClass;
import com.example.schoolservlet.models.Subject;
import com.example.schoolservlet.models.Teacher;
import com.example.schoolservlet.utils.AccessValidation;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name ="admin-teacher-details", value = "/admin/teacher/details")
public class FindTeacherDetailsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(!AccessValidation.isAdmin(request, response)) return;
        String idParam = request.getParameter("id");
        try {
            if(idParam == null || idParam.isEmpty()) {
                throw new InvalidNumberException(idParam, "O ID não pode estar vazio");
            }
            int id = Integer.parseInt(idParam);

            TeacherDAO teacherDAO = new TeacherDAO();
            SubjectDAO subjectDAO = new SubjectDAO();
            SchoolClassDAO schoolClassDAO = new SchoolClassDAO();

            Teacher teacher = teacherDAO.findById(id);

            List<Subject> subjects = subjectDAO.findByTeacherId(id);
            List<SchoolClass> classes = schoolClassDAO.findByTeacherId(id);

            request.setAttribute("teacher", teacher);
            request.setAttribute("subjects", subjects);
            request.setAttribute("classes", classes);

            request.getRequestDispatcher("/WEB-INF/views/admin/details/teacher.jsp")
                    .forward(request, response);
        }catch (InvalidNumberException ine){
            request.getSession().setAttribute("error", ine.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/teacher/find-many");
            response.sendRedirect(request.getContextPath() + "/admin/teacher/find-many");
            return;
        }
        catch (ValidationException ve){
            request.setAttribute("error",ve.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/details/teacher.jsp").forward(request,response);
            return;
        } catch (NotFoundException nfe) {
            request.getSession().setAttribute("error", nfe.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/teacher/find-many");
            response.sendRedirect(request.getContextPath() + "/admin/teacher/find-many");
            return;
        } catch (DataException de) {
            request.setAttribute("error", de.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/details/teacher.jsp").forward(request,response);
            return;
        }
    }
}
