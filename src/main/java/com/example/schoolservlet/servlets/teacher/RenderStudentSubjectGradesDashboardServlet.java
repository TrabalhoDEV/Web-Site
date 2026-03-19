package com.example.schoolservlet.servlets.teacher;

import com.example.schoolservlet.daos.StudentSubjectDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.NotFoundException;
import com.example.schoolservlet.exceptions.UnauthorizedException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.StudentSubject;
import com.example.schoolservlet.models.Teacher;
import com.example.schoolservlet.utils.AccessValidation;
import com.example.schoolservlet.utils.records.AuthenticatedUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Renders the grade release dashboard for a specific student-subject relation.
 *
 * <p>This servlet expects a {@code studentSubjectId} parameter in a POST request.
 * When the identifier is valid, it loads the matching {@link StudentSubject} and
 * forwards the request to the grade release view.</p>
 *
 * <p>If the identifier is missing, invalid, or the record cannot be loaded, the
 * servlet stores an error message in the request, updates the HTTP status code,
 * and forwards to the same view with a {@code null} {@code studentSubject}
 * attribute so the JSP can render a safe fallback state.</p>
 */
@WebServlet("/teacher/students/grades/render")
public class RenderStudentSubjectGradesDashboardServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(RenderStudentSubjectGradesDashboardServlet.class.getName());
    private static final String STUDENT_SUBJECT_ID_PARAM = "studentSubjectId";
    private static final String STUDENT_SUBJECT_ATTRIBUTE = "studentSubject";
    private static final String ERROR_ATTRIBUTE = "error";
    private static final String RELEASE_GRADE_VIEW = "/WEB-INF/views/teacher/student/releaseGrade.jsp";

    /**
     * Handles the request that opens the grade release page for a teacher.
     *
     * @param request contains the student-subject identifier to load
     * @param response sends the HTTP response or forwards to the JSP view
     * @throws ServletException if the target view cannot be rendered
     * @throws IOException if an I/O error occurs while processing the request
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!AccessValidation.isTeacher(request, response)) {
            return;
        }

        AuthenticatedUser user;
        Teacher teacher;
        HttpSession session = request.getSession(false);

        try {
            user = (AuthenticatedUser) session.getAttribute("user");
            teacher = (Teacher) session.getAttribute("teacher");
        } catch (NullPointerException npe) {
            // User not authenticated or session attribute missing
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            request.setAttribute("error", "Sessão expirada, faça login novamente");
            request.getRequestDispatcher("/WEB-INF/views/teacher/index.jsp")
                    .forward(request, response);
            return;
        }

        String studentSubjectIdParam = request.getParameter(STUDENT_SUBJECT_ID_PARAM);
        try{
            getAllData(request, response, studentSubjectIdParam, user.id());
        } catch (UnauthorizedException | NotFoundException e){
            response.sendRedirect(request.getContextPath() + "/teacher/student/find-many");
            return;
        }
        request.getRequestDispatcher(RELEASE_GRADE_VIEW).forward(request, response);
    }

    /**
     * Loads all data required by the grade release view.
     *
     * <p>On success, this method stores the resolved {@link StudentSubject} in the
     * request under the {@code studentSubject} attribute. On failure, it sets the
     * HTTP status code, stores a user-facing error message in the {@code error}
     * attribute, and stores {@code null} as {@code studentSubject} so the JSP can
     * safely render its fallback state.</p>
     *
     * @param request current HTTP request
     * @param response current HTTP response
     * @param studentSubjectIdParam raw student-subject identifier from the request
     * @return {@code true} when the relation is loaded successfully; {@code false} otherwise
     */
    public static boolean getAllData(HttpServletRequest request, HttpServletResponse response, String studentSubjectIdParam, int teacherId) throws UnauthorizedException, NotFoundException{
        if (studentSubjectIdParam == null || studentSubjectIdParam.trim().isEmpty()) {
            setRequestError(
                    request,
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "ID da relação aluno-matéria é obrigatório"
            );
            return false;
        }

        try {
            StudentSubject studentSubject = new StudentSubjectDAO().findById(Integer.parseInt(studentSubjectIdParam.trim()), teacherId);
            request.removeAttribute(ERROR_ATTRIBUTE);
            request.setAttribute(STUDENT_SUBJECT_ATTRIBUTE, studentSubject);
            return true;
        } catch (NumberFormatException e) {
            setRequestError(
                    request,
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "ID da relação aluno-matéria deve ser um número inteiro válido"
            );
        } catch (UnauthorizedException | NotFoundException e) {
            throw e;
        } catch (ValidationException | DataException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return false;
    }

    private static void setRequestError(HttpServletRequest request, HttpServletResponse response, int status, String message) {
        response.setStatus(status);
        request.setAttribute(ERROR_ATTRIBUTE, message);
        request.setAttribute(STUDENT_SUBJECT_ATTRIBUTE, null);
    }
}
