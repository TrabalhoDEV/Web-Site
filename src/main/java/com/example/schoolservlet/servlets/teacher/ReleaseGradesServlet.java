package com.example.schoolservlet.servlets.teacher;

import com.example.schoolservlet.daos.StudentSubjectDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.NotFoundException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.StudentSubject;
import com.example.schoolservlet.utils.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import static com.example.schoolservlet.servlets.teacher.RenderStudentSubjectGradesDashboardServlet.getAllData;

/**
 * Servlet responsible for releasing student grades by teachers.
 * This servlet handles both GET and POST requests for grade release operations:
 * <ul>
 *     <li>GET: Displays the grade release form with the student subject information</li>
 *     <li>POST: Processes and saves the updated grades to the database</li>
 * </ul>
 *
 * <p>The servlet requires a {@code studentSubjectId} parameter to identify the specific
 * student-subject association for which grades are being released.</p>
 *
 * @author Teacher Management System
 * @see StudentSubject
 * @see StudentSubjectDAO
 */
@WebServlet("/teacher/students/grades/release")
public class ReleaseGradesServlet extends HttpServlet {
    private final StudentSubjectDAO studentSubjectDAO = new StudentSubjectDAO();
    /**
     * Handles POST requests to save updated student grades.
     *
     * <p>This method validates and processes grade submissions from the teacher,
     * updating the StudentSubject record with the new grades and observations,
     * then persisting the changes to the database.</p>
     *
     * <p>Required Parameters:</p>
     * <ul>
     *     <li>{@code studentSubjectId} - The ID of the StudentSubject record to update</li>
     *     <li>{@code grade1} - The first grade (must be between 0 and 10)</li>
     *     <li>{@code grade2} - The second grade (must be between 0 and 10)</li>
     * </ul>
     *
     * <p>Optional Parameters:</p>
     * <ul>
     *     <li>{@code obs} - Observations or comments about the grades</li>
     * </ul>
     *
     * <p>Request Attributes Set on Error:</p>
     * <ul>
     *     <li>{@code error} - Error message describing what went wrong</li>
     * </ul>
     *
     * <p>Validation:</p>
     * <ul>
     *     <li>Both grades must be valid double values</li>
     *     <li>Both grades must be within the range of 0 to 10</li>
     *     <li>studentSubjectId parameter is required</li>
     * </ul>
     *
     * <p>Behavior on Success:</p>
     * <p>Redirects to the teacher students page after successfully updating the grades.</p>
     *
     * <p>Behavior on Error:</p>
     * <p>Sets error attribute and forwards back to the grade release form with the original
     * studentSubjectId to allow the teacher to correct and resubmit.</p>
     *
     * @param request  the HttpServletRequest object containing grade data
     * @param response the HttpServletResponse object
     * @throws IOException      if an I/O error occurs
     * @throws ServletException if a servlet error occurs
     * @see InputValidation#validateGrade(double)
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!AccessValidation.isTeacher(request, response)) return;

        HttpSession session = request.getSession(false);
        if (session == null){
            ErrorHandler.forward(request, response, HttpServletResponse.SC_UNAUTHORIZED, "Sessão expirada, faça login novamente", "/index.jsp");
            return;
        }
        String studentSubjectIdParam = request.getParameter("studentSubjectId");
        String grade1Param = request.getParameter("grade1");
        String grade2Param = request.getParameter("grade2");
        String observationsParam = request.getParameter("obs");
        Double grade1 = null;
        Double grade2 = null;

        int studentSubjectId;
        try {
            studentSubjectId = Integer.parseInt(studentSubjectIdParam);
        } catch (NumberFormatException e) {
            session.setAttribute("error", "ID deve ser um número inteiro");
            response.sendRedirect(request.getContextPath() + "/teacher/students");
            return;
        }

        // Validate grades:
        try{
            if (grade1Param != null && !grade1Param.isEmpty()){
                if (InputValidation.validateGrade(Double.parseDouble(grade1Param)) ){
                    grade1 = Double.parseDouble(grade1Param);
                } else throw new ValidationException("Nota 1 deve ser um número entre " + Constants.MIN_GRADE + " e " + Constants.MAX_GRADE);
            }
            if (grade2Param != null && !grade2Param.isEmpty()){
                if (InputValidation.validateGrade(Double.parseDouble(grade2Param))){
                    grade2 = Double.parseDouble(grade2Param);
                } else throw new ValidationException("Nota 2 deve ser um número entre " + Constants.MIN_GRADE + " e " + Constants.MAX_GRADE);
            }
        } catch (ClassCastException | NumberFormatException | ValidationException e){
            ErrorHandler.forward(request, response, HttpServletResponse.SC_BAD_REQUEST, "Notas devem ser números entre " + Constants.MIN_GRADE + " e " + Constants.MAX_GRADE, request.getContextPath() + "/teacher/students");
            return;
        }

        try {
            StudentSubject studentSubject = studentSubjectDAO.findById(studentSubjectId);

            studentSubject.setGrade1(grade1);
            studentSubject.setGrade2(grade2);
            studentSubject.setObs(observationsParam.isEmpty() ? null : InputNormalizer.normalizeObs(observationsParam));

            studentSubjectDAO.update(studentSubject);
            response.sendRedirect(request.getContextPath() + "/teacher/students");

        } catch (NotFoundException e) {
            session.setAttribute("error", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/teacher/students");
        } catch (DataException | ValidationException e) {
            request.setAttribute("error", e.getMessage());
            getAllData(request, response, studentSubjectIdParam);
            request.getRequestDispatcher("/WEB-INF/views/teacher/student/releaseGrade.jsp")
                    .forward(request, response);
        } catch (NumberFormatException e){
            session.setAttribute("error", "ID deve ser um número");
            response.sendRedirect(request.getContextPath() + "/teacher/students");
        }
    }
}
