package com.example.schoolservlet.servlets.admin.update;

import com.example.schoolservlet.daos.SchoolClassDAO;
import com.example.schoolservlet.daos.StudentDAO;
import com.example.schoolservlet.daos.StudentSubjectDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.NotFoundException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.SchoolClass;
import com.example.schoolservlet.models.Student;
import com.example.schoolservlet.utils.AccessValidation;
import com.example.schoolservlet.utils.InputNormalizer;
import com.example.schoolservlet.utils.InputValidation;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet responsible for updating student data, including
 * personal fields (name, email, CPF) and school class transfer.
 *
 * Both operations are handled in the same POST:
 *   - Name and email are always validated and persisted.
 *   - Missing student_subject records are always reconciled, regardless
 *     of whether the class changed (covers cases where a subject was added
 *     to the class after the student was already enrolled).
 *   - If {@code newClassId} differs from the student's current class,
 *     a smart subject merge is performed to preserve existing grades:
 *
 *   OLD class subjects: {Mat, Port, Hist}
 *   NEW class subjects: {Mat, Port, Fis}
 *
 *   ┌──────────────┬─────────────────────────────────────────────────────┐
 *   │  Situation   │  Action                                             │
 *   ├──────────────┼─────────────────────────────────────────────────────┤
 *   │ Mat, Port    │  KEEP   – subject in both classes, grades preserved │
 *   │ Hist         │  DELETE – not offered in the new class              │
 *   │ Fis          │  INSERT – new subject, blank grade record           │
 *   └──────────────┴─────────────────────────────────────────────────────┘
 *
 * After any class operation, {@link #ensureMissingSubjects} is always called
 * to guarantee every subject of the final class has a student_subject row.
 *
 * Access: administrators only.
 */
@WebServlet(name = "admin-update-student", value = "/admin/student/update")
public class UpdateStudentServlet extends HttpServlet {

    private static final Logger       LOGGER        = Logger.getLogger(UpdateStudentServlet.class.getName());
    private static final String       UPDATE_VIEW   = "/WEB-INF/views/admin/update/student.jsp";
    private static final String       STUDENT_LIST  = "/admin/student/find-many";
    private static final SchoolClassDAO schoolClassDAO = new SchoolClassDAO();

    // ------------------------------------------------------------------ GET --

    /**
     * Loads the student and all school classes for the edit form.
     *
     * Required query parameter:
     * - {@code id} – student enrollment (integer)
     *
     * Attributes forwarded to the view:
     * - {@code student}       – Student object with current data
     * - {@code schoolClass}   – student's current SchoolClass
     * - {@code schoolClasses} – all available SchoolClass options
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!AccessValidation.isAdmin(request, response)) return;

        String idParam = request.getParameter("id");

        try {
            int studentId = parseStudentId(idParam);
            loadFormData(request, studentId);
            request.getRequestDispatcher(UPDATE_VIEW).forward(request, response);

        } catch (ValidationException | NotFoundException | DataException e) {
            LOGGER.log(Level.WARNING, "Error loading student for update: " + e.getMessage());
            redirectWithError(request, response, e.getMessage());
        }
    }

    // ----------------------------------------------------------------- POST --

    /**
     * Processes the full student update: personal data + optional class transfer
     * + subject reconciliation.
     *
     * Required form parameters:
     * - {@code enrollment} – student ID (read-only identifier)
     * - {@code name}       – student full name
     * - {@code email}      – student e-mail address
     * - {@code cpf}        – student CPF (validated for integrity, not re-persisted)
     * - {@code newClassId} – target school class ID (may equal the current class)
     *
     * Flow:
     * 1. Parse and validate all parameters.
     * 2. Apply name and email changes to the student object.
     * 3. If {@code newClassId} differs from the current class, run the
     *    smart subject merge and update {@code id_school_class}.
     * 4. Reconcile any missing student_subject rows for the final class.
     * 5. Persist the student with a single {@code studentDAO.update()} call.
     *
     * On success  → redirects to the student list.
     * On failure  → reloads the form with an error message.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!AccessValidation.isAdmin(request, response)) return;

        String enrollmentParam = request.getParameter("enrollment");
        String name            = request.getParameter("name");
        String email           = request.getParameter("email");
        String cpf             = request.getParameter("cpf");
        String newClassParam   = request.getParameter("newClassId");

        try {
            // ---- parse & validate -------------------------------------------
            int studentId  = parseStudentId(enrollmentParam);
            int newClassId = parseClassId(newClassParam);

            name  = InputNormalizer.normalizeName(name);
            email = InputNormalizer.normalizeEmail(email);
            cpf   = InputNormalizer.normalizeCpf(cpf);

            InputValidation.validateStudentName(name);
            InputValidation.validateEmail(email);
            InputValidation.validateCpf(cpf);

            // ---- load student -----------------------------------------------
            StudentDAO studentDAO = new StudentDAO();
            Student student = studentDAO.findById(studentId);

            // ---- apply personal field changes --------------------------------
            student.setName(name);
            student.setEmail(email);

            // ---- class transfer (only when the class actually changed) -------
            if (student.getIdSchoolClass() != newClassId) {
                System.out.println(student);
                SchoolClass newClass = schoolClassDAO.findById(newClassId);
                mergeStudentSubjects(student, newClass);
                student.setIdSchoolClass(newClassId);

                LOGGER.log(Level.INFO,
                        "Student id={0} transferred to class id={1}",
                        new Object[]{ studentId, newClassId });
            }

            // ---- reconcile: insert any subject still missing for the final class
            // This covers: no class change + subject added to the class later,
            // or any gap left after a merge.
            ensureMissingSubjects(student.getId(), student.getIdSchoolClass());

            // ---- persist everything in one shot ------------------------------
            studentDAO.update(student);

            LOGGER.log(Level.INFO, "Student id={0} updated successfully", studentId);
            response.sendRedirect(request.getContextPath() + STUDENT_LIST);

        } catch (ValidationException | NotFoundException | DataException e) {
            LOGGER.log(Level.WARNING, "Error updating student: " + e.getMessage());
            handlePostError(request, response, enrollmentParam, e.getMessage());
        }
    }

    // --------------------------------------------------------------- helpers --

    /**
     * Performs a smart merge of student_subject records when a student
     * is transferred to a different class:
     *
     * <ul>
     *   <li><b>old ∩ new</b> → grades kept, no action.</li>
     *   <li><b>old − new</b> → records deleted (subject no longer offered).</li>
     *   <li><b>new − old</b> → blank records inserted (new subjects, no grades yet).</li>
     * </ul>
     *
     * Note: after this method returns, {@link #ensureMissingSubjects} is still
     * called by the caller as a safety net for any remaining gaps.
     *
     * @param student  student being transferred (still holds the old class ID)
     * @param newClass target school class
     * @throws DataException if any database operation fails
     */
    private void mergeStudentSubjects(Student student, SchoolClass newClass) throws DataException {

        StudentSubjectDAO studentSubjectDAO = new StudentSubjectDAO();

        Set<Integer> oldSubjects = schoolClassDAO.findSubjectIdsByClass(student.getIdSchoolClass());
        Set<Integer> newSubjects = schoolClassDAO.findSubjectIdsByClass(newClass.getId());

        // subjects exclusive to the old class → delete
        Set<Integer> toDelete = new HashSet<>(oldSubjects);
        toDelete.removeAll(newSubjects);
        if (!toDelete.isEmpty()) {
            studentSubjectDAO.deleteByStudentAndSubjects(student.getId(), toDelete);
        }

        // subjects exclusive to the new class → insert blank records
        Set<Integer> toInsert = new HashSet<>(newSubjects);
        toInsert.removeAll(oldSubjects);
        if (!toInsert.isEmpty()) {
            studentSubjectDAO.createManyBySubjectIds(student.getId(), toInsert);
        }
    }

    /**
     * Ensures every subject of a given class has a corresponding
     * {@code student_subject} row for the student.
     *
     * This handles two scenarios:
     * <ol>
     *   <li>The student's class did not change, but a new subject was added
     *       to the class after the student was enrolled.</li>
     *   <li>A gap remained after {@link #mergeStudentSubjects} (defensive).</li>
     * </ol>
     *
     * Implementation relies on {@code ON CONFLICT DO NOTHING} inside
     * {@code StudentSubjectDAO.createManyBySubjectIds}, so existing records
     * (with or without grades) are never overwritten.
     *
     * @param studentId     the student whose records need reconciliation
     * @param classId       the class whose subjects are the reference
     * @throws DataException if any database operation fails
     */
    private void ensureMissingSubjects(int studentId, int classId) throws DataException {

        StudentSubjectDAO studentSubjectDAO = new StudentSubjectDAO();

        // All subjects the class currently offers
        Set<Integer> classSubjects = schoolClassDAO.findSubjectIdsByClass(classId);

        // Subjects the student already has a record for
        Set<Integer> existingSubjects = studentSubjectDAO.findSubjectIdsByStudent(studentId);

        // Only the delta: class subjects the student does not yet have
        Set<Integer> missing = new HashSet<>(classSubjects);
        missing.removeAll(existingSubjects);

        if (!missing.isEmpty()) {
            LOGGER.log(Level.INFO,
                    "Student id={0}: inserting {1} missing subject record(s)",
                    new Object[]{ studentId, missing.size() });
            studentSubjectDAO.createManyBySubjectIds(studentId, missing);
        }
    }

    /**
     * Populates request attributes needed to render the edit form.
     *
     * @param request   HTTP request to populate
     * @param studentId student enrollment ID
     * @throws NotFoundException   if the student or current class is not found
     * @throws DataException       if a database error occurs
     * @throws ValidationException if the ID is invalid
     */
    private void loadFormData(HttpServletRequest request, int studentId)
            throws NotFoundException, DataException, ValidationException {

        Student           student      = new StudentDAO().findById(studentId);
        SchoolClass       currentClass = schoolClassDAO.findById(student.getIdSchoolClass());
        List<SchoolClass> allClasses   = schoolClassDAO.findAll();

        request.setAttribute("student",       student);
        request.setAttribute("schoolClass",   currentClass);
        request.setAttribute("schoolClasses", allClasses);
    }

    /**
     * Reloads the edit form with an error message after a failed POST.
     * Falls back to a session-based redirect if form data cannot be reloaded.
     */
    private void handlePostError(HttpServletRequest request, HttpServletResponse response,
                                 String enrollmentParam, String errorMessage)
            throws ServletException, IOException {
        try {
            int studentId = parseStudentId(enrollmentParam);
            loadFormData(request, studentId);
            request.setAttribute("error", errorMessage);
            request.getRequestDispatcher(UPDATE_VIEW).forward(request, response);

        } catch (ValidationException | NotFoundException | DataException e) {
            LOGGER.log(Level.WARNING, "Error reloading form during error handling", e);
            redirectWithError(request, response, "Não foi possível atualizar o aluno.");
        }
    }

    /**
     * Parses and validates a student ID parameter.
     *
     * @param param raw string from the request
     * @return validated ID as int
     * @throws ValidationException if the value is null, blank or not a positive integer
     */
    private int parseStudentId(String param) throws ValidationException {
        try {
            int id = Integer.parseInt(param.trim());
            InputValidation.validateId(id, "id do aluno");
            return id;
        } catch (NumberFormatException e) {
            throw new ValidationException("ID do aluno precisa ser um valor numérico inteiro.");
        }
    }

    /**
     * Parses and validates a school class ID parameter.
     *
     * @param param raw string from the request
     * @return validated ID as int
     * @throws ValidationException if the value is null, blank or not a positive integer
     */
    private int parseClassId(String param) throws ValidationException {
        try {
            int id = Integer.parseInt(param.trim());
            InputValidation.validateId(id, "id da turma");
            return id;
        } catch (NumberFormatException e) {
            throw new ValidationException("ID da turma precisa ser um valor numérico inteiro.");
        }
    }

    /** Stores an error message in the session and redirects to the student list. */
    private void redirectWithError(HttpServletRequest request, HttpServletResponse response,
                                   String message) throws IOException {
        HttpSession session = request.getSession();
        session.setAttribute("error", message);
        response.sendRedirect(request.getContextPath() + STUDENT_LIST);
    }
}