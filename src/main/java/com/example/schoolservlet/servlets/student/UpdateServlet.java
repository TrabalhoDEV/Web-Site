package com.example.schoolservlet.servlets.student;

import com.example.schoolservlet.daos.SchoolClassDAO;
import com.example.schoolservlet.daos.StudentDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.NotFoundException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.Student;
import com.example.schoolservlet.utils.AccessValidation;
import com.example.schoolservlet.utils.InputNormalizer;
import com.example.schoolservlet.utils.InputValidation;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet for managing student data updates.
 *
 * Responsibilities:
 * - Load student data for editing (doGet)
 * - Process and persist student data updates (doPost)
 *
 * Workflow:
 * 1. GET: Retrieves and displays form pre-filled with student data
 * 2. POST: Validates, updates and persists modified data
 *
 * @author School Management System
 * @version 1.0
 */
@WebServlet("/admin/student/update")
public class UpdateServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(UpdateServlet.class.getName());
    private static final String UPDATE_VIEW = "/WEB-INF/views/admin/update/student.jsp";
    private static final String LIST_VIEW = "/WEB-INF/views/admin/findMany/student.jsp";
    private static final String STUDENT_LIST_URL = "/admin/student/find-many";

    /**
     * Loads student data for display in the update form.
     *
     * Required parameters:
     * - enrollment: Student enrollment number (will be normalized and validated)
     *
     * Request attributes added on success:
     * - student: Student object containing current student data
     * - schoolYear: School year of the student's class
     *
     * On validation or not found errors:
     * - "error" attribute is added with descriptive message
     * - Redirects to student list page
     *
     * @param request HTTP request containing "enrollment" parameter
     * @param response HTTP response for forwarding or redirecting
     * @throws ServletException If servlet processing error occurs
     * @throws IOException If input/output error occurs
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Validate session:
        if (!AccessValidation.isAdmin(request, response)) return;

        // Get parameters:
        String enrollmentParam = request.getParameter("enrollment");

        try {
            // Validate and normalize enrollment parameter:
            int enrollment = validateAndNormalizeEnrollment(enrollmentParam);

            // Load student and school year data:
            StudentData studentData = loadStudentData(enrollment);

            // Forward student and school year data to the JSP form:
            request.setAttribute("student", studentData.getStudent());
            request.setAttribute("schoolYear", studentData.getSchoolYear());
            request.getRequestDispatcher(UPDATE_VIEW).forward(request, response);

        } catch (ValidationException | NotFoundException | DataException e) {
            // Log error and redirect to student list:
            logger.log(Level.WARNING, "Error loading student data: " + e.getMessage());
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher(LIST_VIEW).forward(request, response);
        }
    }

    /**
     * Processes student data update request.
     *
     * Required form parameters:
     * - enrollment: Student enrollment number (unique identifier, read-only)
     * - name: Student full name (editable)
     * - email: Student email address (editable)
     * - cpf: Student CPF number (validation, read-only)
     *
     * Validations performed:
     * - Enrollment is normalized according to system standards
     * - Name and email are validated according to business rules
     * - CPF is validated to ensure data integrity
     *
     * Success flow:
     * - All parameters are validated
     * - Student data is updated in the database
     * - User is redirected to student list page
     *
     * Error flow:
     * - Error message is displayed on the form
     * - Form is reloaded with current data and error message
     * - If error loading data, redirects to list with generic message
     *
     * @param request HTTP request containing form parameters
     * @param response HTTP response for redirecting or forwarding
     * @throws ServletException If servlet processing error occurs
     * @throws IOException If input/output error occurs
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Validate session:
        if (!AccessValidation.isAdmin(request, response)) return;

        // Get form parameters:
        String enrollmentParam = request.getParameter("enrollment");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String cpf = request.getParameter("cpf");

        if (name != null) {
            name = name.trim();
        }
        if (email != null) {
            email = email.trim();
        }
        if (cpf != null) {
            cpf = cpf.trim();
        }

        try {
            // Validate all parameters:
            int enrollment = validateAndNormalizeEnrollment(enrollmentParam);
            validateStudentFields(name, email, cpf);

            // Load student and update editable fields:
            Student student = new StudentDAO().findById(enrollment);
            student.setName(name);
            student.setEmail(email);

            // Persist updated student data to database:
            new StudentDAO().update(student);

            // Log successful update:
            logger.log(Level.INFO, "Student updated successfully - Enrollment: " + enrollment);

            // Redirect to student list page on successful update:
            response.sendRedirect(request.getContextPath() + STUDENT_LIST_URL);

        } catch (ValidationException | NotFoundException | DataException e) {
            // Log error:
            logger.log(Level.WARNING, "Error updating student: " + e.getMessage());

            // Handle validation and data access errors by reloading form:
            handleUpdateError(request, response, enrollmentParam, e.getMessage());
        } catch (Exception e) {
            // Log unexpected exceptions:
            logger.log(Level.SEVERE, "Unexpected error during student update", e);
            request.setAttribute("error", "An unexpected error occurred. Please try again.");
            request.getRequestDispatcher(LIST_VIEW).forward(request, response);
        }
    }

    /**
     * Validates and normalizes the enrollment parameter.
     *
     * @param enrollmentParam The raw enrollment parameter from request
     * @return Normalized enrollment number
     * @throws ValidationException If enrollment is invalid
     */
    private int validateAndNormalizeEnrollment(String enrollmentParam) throws ValidationException {
        int enrollment = InputNormalizer.normalizeEnrollment(enrollmentParam);
        InputValidation.validateEnrollment(String.format("%06d", enrollment));
        return enrollment;
    }

    /**
     * Validates all student fields.
     *
     * @param name Student name
     * @param email Student email
     * @param cpf Student CPF
     * @throws ValidationException If any field is invalid
     */
    private void validateStudentFields(String name, String email, String cpf) throws ValidationException {
        InputValidation.validateName(name);
        InputValidation.validateEmail(email);
        InputValidation.validateCpf(cpf);
    }

    /**
     * Loads student and school year data from database.
     *
     * @param enrollment Student enrollment number
     * @return StudentData containing student object and school year
     * @throws NotFoundException If student or school class not found
     * @throws DataException If database error occurs
     */
    private StudentData loadStudentData(int enrollment) throws NotFoundException, DataException, ValidationException {
        Student student = new StudentDAO().findById(enrollment);
        String schoolYear = new SchoolClassDAO().findById(student.getIdSchoolClass()).getSchoolYear();
        return new StudentData(student, schoolYear);
    }

    /**
     * Handles errors during student update by reloading the form with error message.
     * If data reload fails, redirects to student list page.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param enrollmentParam The enrollment parameter
     * @param errorMessage The error message to display
     * @throws ServletException If request dispatcher error occurs
     * @throws IOException If I/O error occurs
     */
    private void handleUpdateError(HttpServletRequest request, HttpServletResponse response,
                                   String enrollmentParam, String errorMessage) throws ServletException, IOException {
        try {
            // Attempt to reload student data for form re-population:
            int enrollment = validateAndNormalizeEnrollment(enrollmentParam);
            StudentData studentData = loadStudentData(enrollment);

            request.setAttribute("student", studentData.getStudent());
            request.setAttribute("schoolYear", studentData.getSchoolYear());
            request.setAttribute("error", errorMessage);

            // Forward back to update form with error message:
            request.getRequestDispatcher(UPDATE_VIEW).forward(request, response);

        } catch (Exception ex) {
            // If unable to reload student data, redirect to list:
            logger.log(Level.WARNING, "Error reloading student data during error handling", ex);
            request.setAttribute("error", "Error updating student. Please try again.");
            request.getRequestDispatcher(LIST_VIEW).forward(request, response);
        }
    }

    /**
     * Inner class to encapsulate student and school year data.
     */
    private static class StudentData {
        private final Student student;
        private final String schoolYear;

        public StudentData(Student student, String schoolYear) {
            this.student = student;
            this.schoolYear = schoolYear;
        }

        public Student getStudent() {
            return student;
        }

        public String getSchoolYear() {
            return schoolYear;
        }
    }
}