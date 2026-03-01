package com.example.schoolservlet.servlets.admin.insert;

import com.example.schoolservlet.daos.SchoolClassDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.RequiredFieldException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.exceptions.ValueAlreadyExistsException;
import com.example.schoolservlet.models.SchoolClass;
import com.example.schoolservlet.utils.AccessValidation;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Servlet responsável pela inserção de novas turmas escolares.
 * 
 * Validações:
 * - Usuário deve ser administrador
 * - Campo schoolYear é obrigatório
 * 
 * @author Sistema de Gerenciamento Escolar
 * @version 1.0
 */
@WebServlet("/admin/insert/school-class")
public class InsertSchoolClassServlet extends HttpServlet {
    
    /**
     * Processa requisições GET para exibir o formulário de inserção de turma escolar.
     * 
     * @param request objeto HttpServletRequest da requisição
     * @param response objeto HttpServletResponse para enviar resposta ao cliente
     * @throws ServletException se ocorrer erro durante o processamento
     * @throws IOException se ocorrer erro de I/O
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Validate session and permissions
        if (!AccessValidation.isAdmin(request, response)) return;
        
        // Forward to JSP form
        request.getRequestDispatcher("/WEB-INF/views/admin/insert/school-class.jsp").forward(request, response);
    }
    
    /**
     * Processa requisições POST para inserir uma nova turma escolar.
     * 
     * @param request objeto HttpServletRequest contendo os parâmetros do formulário
     * @param response objeto HttpServletResponse para enviar resposta ao cliente
     * @throws ServletException se ocorrer erro durante o processamento
     * @throws IOException se ocorrer erro de I/O
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Validate session and permissions
        if (!AccessValidation.isAdmin(request, response)) return;

        // Get form parameters
        String schoolYearParam = request.getParameter("schoolYear");
        
        try {
            // Validate required field
            if (schoolYearParam == null || schoolYearParam.trim().isEmpty()) {
                throw new RequiredFieldException("O ano letivo é obrigatório");
            }
            
            // Insert into database
            SchoolClass newClass = new SchoolClass();
            newClass.setSchoolYear(schoolYearParam.trim());
            new SchoolClassDAO().create(newClass);
            
            // Redirect to success page
            response.sendRedirect(request.getContextPath() + "/admin/school-class/find-many");
            
        } catch (DataException | ValidationException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/insert/school-class.jsp").forward(request, response);
        }
    }
}