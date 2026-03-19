package com.example.schoolservlet.servlets.forgotPassword;

import com.example.schoolservlet.daos.AdminDAO;
import com.example.schoolservlet.daos.StudentDAO;
import com.example.schoolservlet.daos.TeacherDAO;
import com.example.schoolservlet.exceptions.*;
import com.example.schoolservlet.models.Admin;
import com.example.schoolservlet.models.Student;
import com.example.schoolservlet.models.Teacher;
import com.example.schoolservlet.utils.EmailService;
import com.example.schoolservlet.utils.InputNormalizer;
import com.example.schoolservlet.utils.InputValidation;
import com.example.schoolservlet.utils.enums.UserRoleEnum;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.security.SecureRandom;

@WebServlet(name = "forget-password-send-code", value = "/auth/forgot-password/send-code")
public class SendCodeServlet extends HttpServlet {

    /**
     * Handles password recovery requests.
     *
     * <p>GET: Forwards to the send code JSP page.
     * POST: Validates the user input, identifies the user role (Student, Admin, Teacher),
     * retrieves the email, generates a verification code, sends it, and sets session attributes.
     *
     * <p>Session attributes include userId, role, and code with a 15-minute timeout.
     * Errors are handled by forwarding to the JSP page or redirecting to the validation page.
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/forgotPassword/sendCode.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

//        Variables:
        String input = "";
        HttpSession session = request.getSession();
        boolean hasException = false;
        String email = null;
        UserRoleEnum role = null;

//        Catching user's input:
        input = request.getParameter("input");

        try {
            InputValidation.validateIsNull("identificador", input);
        } catch (ValidationException e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/forgotPassword/sendCode.jsp").forward(request, response);
            return;
        }

        input = input.trim();

        try {
            InputValidation.validateEnrollment(input);
            role = UserRoleEnum.STUDENT;
        } catch (ValidationException e) {}

        try {
            InputValidation.validateCpf(input);
            role = UserRoleEnum.ADMIN;
        } catch (ValidationException e) {}

        try{
            InputValidation.validateUserName(input);
            role = UserRoleEnum.TEACHER;
        } catch (ValidationException e) {}

        if (role == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("error", "Identificador inválido");
            hasException = true;
        }

        if (role == UserRoleEnum.STUDENT) {
            try {
                int id = InputNormalizer.normalizeEnrollment(input);
                StudentDAO studentDAO = new StudentDAO();
                Student student = studentDAO.findById(id);

                if (student != null) {
                    email = student.getEmail();

                    session.setAttribute("userId", student.getId());
                    session.setAttribute("role", UserRoleEnum.STUDENT);
                    session.setMaxInactiveInterval(60 * 15);
                } else {
                    request.setAttribute("error", "Usuário não encontrado");
                    hasException = true;
                }
            } catch (ValidationException e) {
                request.setAttribute("error", e.getMessage());
                hasException = true;
            } catch (DataException dae) {
                request.setAttribute("error", dae.getMessage());
                hasException = true;
            } catch (NotFoundException nfe) {
                response.sendRedirect(request.getContextPath() + "/auth/forgot-password/validate-code");
                return;
            }
        } else if (role == UserRoleEnum.ADMIN) {
            try {
                AdminDAO adminDAO = new AdminDAO();
                Admin admin = adminDAO.findByDocument(InputNormalizer.normalizeCpf(input));

                if (admin != null) {
                    email = admin.getEmail();

                    session.setAttribute("userId", admin.getId());
                    session.setAttribute("role", UserRoleEnum.ADMIN);
                    session.setMaxInactiveInterval(60 * 15);
                } else {
                    request.setAttribute("error", "Usuário não encontrado");
                    hasException = true;
                }
            } catch (DataException | ValidationException e) {
                request.setAttribute("error", e.getMessage());
                hasException = true;
            } catch (NotFoundException nfe){
                response.sendRedirect(request.getContextPath() + "/auth/forgot-password/validate-code");
                return;
            }
        } else if (role == UserRoleEnum.TEACHER) {
            try {
                TeacherDAO teacherDAO = new TeacherDAO();
                Teacher teacher = teacherDAO.findByUserName(InputNormalizer.normalizeUserName(input));

                if (teacher != null) {
                    email = teacher.getEmail();

                    session.setAttribute("userId", teacher.getId());
                    session.setAttribute("role", UserRoleEnum.TEACHER);
                    session.setMaxInactiveInterval(60 * 15);
                } else {
                    request.setAttribute("error", "Usuário não encontrado");
                    hasException = true;
                }
            } catch (DataException | RequiredFieldException e) {
                request.setAttribute("error", e.getMessage());
                hasException = true;
            } catch (NotFoundException nfe) {
                response.sendRedirect(request.getContextPath() + "/auth/forgot-password/validate-code");
                return;
            }
        }

        if (hasException) {
            request.getRequestDispatcher("/WEB-INF/views/forgotPassword/sendCode.jsp").forward(request, response);
            return;
        }

        if (email != null && !email.isBlank()) {
            String code = String.format("%06d", (new SecureRandom()).nextInt(1000000));
            session.setAttribute("code", code);

            try {
                EmailService.sendEmail(email, "Recuperação de senha", "<h1 style=\"text-align:center;\">Recuperação da sua senha no Colégio Vértice</h1>" +
                        "<p style=\"text-align:center;\">Esse código expirará em 15 minutos, caso não tenha sido você, ignore.</p>" +
                        "<br>" +
                        "<div><h3 style=\"text-align:center;\">Código:</h3>" +
                        "<h2 style=\"text-align:center;\">" + code + "</h2></div>"
                        + "<br>"
                        + "<p style=\"text-align:center;\">Atenciosamente,<br>"
                        + "Secretaria Vértice</p>"
                );
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
                request.setAttribute("error", "Não foi possível enviar o email nesse momento, tente novamente mais tarde");
                request.getRequestDispatcher("/WEB-INF/views/forgotPassword/sendCode.jsp").forward(request, response);
                return;
            }
        }

        response.sendRedirect(request.getContextPath() + "/auth/forgot-password/validate-code");
    }
}
