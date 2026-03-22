package com.example.schoolservlet.servlets.admin.update;

import com.example.schoolservlet.daos.*;
import com.example.schoolservlet.exceptions.*;
import com.example.schoolservlet.models.*;
import com.example.schoolservlet.utils.*;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Servlet responsible for updating teachers in the system.
 * This endpoint handles teacher updating.
 */
@WebServlet(name = "admin-update-teacher",value = "/admin/teacher/update")
public class UpdateTeacherServlet extends HttpServlet {
    private SubjectDAO subjectDAO = new SubjectDAO();
    private SchoolClassDAO schoolClassDAO = new SchoolClassDAO();
    private SubjectTeacherDAO subjectTeacherDAO = new SubjectTeacherDAO();
    private SchoolClassTeacherDAO schoolClassTeacherDAO = new SchoolClassTeacherDAO();
    private TeacherDAO teacherDAO = new TeacherDAO();

    private static String enrollInURL;

    static{
        Dotenv dotenv = null;

        // Firstly, it tries to load .env locally
        try {
            dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();
        } catch (Exception e) {
            e.printStackTrace();
            dotenv = null;
        }

        enrollInURL = ConfigService.getEnv("BASE_URL", dotenv);
    }

    /**
     * Handles HTTP GET requests to load the teacher update page.
     * <p>
     * This method validates the teacher ID received from the request,
     * retrieves the corresponding teacher from the database, and loads
     * the necessary data to populate the update form. If the teacher is
     * found, the request is forwarded to the teacher update JSP page.
     * </p>
     * <p>
     * If the ID is invalid, missing, or the teacher does not exist,
     * an appropriate error message is handled and displayed.
     * Access to this endpoint is restricted to administrators.
     * </p>
     *
     * @param request the {@link HttpServletRequest} containing the client request
     * @param response the {@link HttpServletResponse} used to send the response
     * @throws ServletException if a servlet-related error occurs
     * @throws IOException if an input or output error occurs during request processing
     */
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
            Teacher teacher = teacherDAO.findById(id);
            if (teacher == null) {
                throw new NotFoundException("teacher","id",idParam);
            }
            loadUpdateData(request,id);
            request.getRequestDispatcher("/WEB-INF/views/admin/update/teacher.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            handleError(request, response, "ID inválido.");

        } catch (DataException | NotFoundException | ValidationException e) {
            handleError(request, response, e.getMessage());
        }
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

        int id = -1;
        try{
            String idParam = request.getParameter("id");
            if(idParam == null || idParam.isEmpty()){
                throw new InvalidNumberException(idParam,"O ID não pode estar vazio");
            }
            id = Integer.parseInt(idParam);
            Teacher teacher = teacherDAO.findById(id);
            if (teacher == null) {
                throw new NotFoundException("teacher", "id", idParam);
            }

            String name = InputNormalizer.normalizeName(request.getParameter("name"));
            String email = InputNormalizer.normalizeEmail(request.getParameter("email"));
            String username = InputNormalizer.normalizeUserName(request.getParameter("username"));
            String[] subjectIdsParam = request.getParameterValues("subjectIds");
            String[] schoolClassIdsParam = request.getParameterValues("schoolClassIds");

            InputValidation.validateTeacherName(name);
            InputValidation.validateEmail(email);
            InputValidation.validateUserName(username);

            if(!teacher.getEmail().equals(email)){
                FieldAlreadyUsedValidation.exists("teacher","email","Email",email);
            }

            if(!teacher.getUsername().equals(username)){
                FieldAlreadyUsedValidation.exists("teacher","username","Username",username);
            }

            teacher.setName(name);
            teacher.setEmail(email);
            teacher.setUsername(username);

            teacherDAO.update(teacher);

            List<Integer> validSubjectIds = InputValidation.validateIdsExist(subjectIdsParam, subjectDAO.findAllIds());

            Set<Integer> newSubjectIds = new HashSet<>();
            if (validSubjectIds != null) {
                newSubjectIds.addAll(validSubjectIds);
            }

            List<Subject> currentSubjects = subjectDAO.findByTeacherId(teacher.getId());

            Set<Integer> currentSubjectIds = new HashSet<>();
            for (Subject s : currentSubjects) {
                currentSubjectIds.add(s.getId());
            }

            Set<Integer> toAddSubjects = new HashSet<>(newSubjectIds);
            toAddSubjects.removeAll(currentSubjectIds);

            Set<Integer> toRemoveSubjects = new HashSet<>(currentSubjectIds);
            toRemoveSubjects.removeAll(newSubjectIds);

            if (!toAddSubjects.isEmpty()) {
                List<SubjectTeacher> subjectTeachersToInsert = new ArrayList<>();

                for (Integer subjectId : toAddSubjects) {
                    Subject subject = subjectDAO.findById(subjectId);

                    SubjectTeacher st = new SubjectTeacher();
                    st.setTeacher(teacher);
                    st.setSubject(subject);
                    subjectTeachersToInsert.add(st);
                }

                subjectTeacherDAO.createMany(subjectTeachersToInsert);
            }

            subjectTeacherDAO.deleteManyByTeacherAndSubjects(teacher.getId(), toRemoveSubjects);

            if (!toRemoveSubjects.isEmpty()) {
                schoolClassTeacherDAO.removeSubjectsFromList(teacher.getId(), toRemoveSubjects);
            }

            List<SchoolClass> currentSchoolClasses = schoolClassDAO.findByTeacherId(teacher.getId());

            Set<Integer> currentSchoolClassIds = new HashSet<>();
            for (SchoolClass sc : currentSchoolClasses) {
                currentSchoolClassIds.add(sc.getId());
            }

            try {
                String topic = "Acesso ao Sistema Escolar";
                String message = "<h3 style=\"text-align:center;\">Olá " + OutputFormatService.formatName(teacher.getName()) + ",</h3>"
                        + "<p style=\"text-align:center;\">Sua conta foi alterada pelo seu administrador.</p>"
                        + "<p style=\"text-align:center;\">Caso tenha sido um engano fale com ele,</p>"
                        + "<p style=\"text-align:center;\">Para logar, acesse o link abaixo:</p><br>"
                        + "<p style=\"text-align:center;\">" + enrollInURL+"/auth" + "</p><br>"
                        + "<p style=\"text-align:center;\">Atenciosamente,<br>"
                        + "Secretaria Vértice</p>";

                EmailService.sendEmail(teacher.getEmail(), topic, message);
            } catch (Exception e) {
                e.printStackTrace();
            }

            response.sendRedirect(request.getContextPath()+ "/admin/teacher/find-many");

        } catch (NumberFormatException e) {
            request.getSession().setAttribute("error", "ID inválido.");
            response.sendRedirect(request.getContextPath() + "/admin/teacher/find-many");
        } catch (DataException | NotFoundException e) {
            request.getSession().setAttribute("error", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/teacher/find-many");
        } catch (ValueAlreadyExistsException vaee){
            loadSafely(request, id);
            request.setAttribute("error", vaee.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/update/teacher.jsp").forward(request, response);
        } catch (ValidationException ve){
            loadSafely(request, id);
            request.setAttribute("error", ve.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/update/teacher.jsp").forward(request, response);
        }
    }

    /**
     * Loads the data required to populate the teacher update page.
     * <p>
     * This method retrieves the teacher information, the list of all
     * available subjects, and the subjects currently associated with
     * the teacher. These data are then stored as request attributes so
     * they can be used to pre-fill the update form in the view layer.
     * </p>
     *
     * @param request the {@link HttpServletRequest} used to store attributes for the view
     * @param teacherId the unique identifier of the teacher whose data will be loaded
     * @throws DataException if an error occurs while accessing the data source
     * @throws NotFoundException if the teacher with the given ID does not exist
     * @throws ValidationException if the provided teacher ID fails validation
     */
    private void loadUpdateData(HttpServletRequest request, int teacherId)
            throws DataException, NotFoundException, ValidationException {

        Teacher teacher = teacherDAO.findById(teacherId);
        List<Subject> allSubjects = subjectDAO.findAll();
        List<Subject> teacherSubjects =
                subjectDAO.findByTeacherId(teacherId);

        request.setAttribute("teacher", teacher);
        request.setAttribute("subjects", allSubjects);
        request.setAttribute("teacherSubjects", teacherSubjects);
    }

    /**
     * Safely loads the data required for the teacher update page.
     * <p>
     * This method wraps the {@code loadUpdateData} call to prevent
     * checked exceptions from interrupting the request flow. Any
     * exception thrown during the loading process is silently ignored,
     * allowing the application to continue rendering the view even if
     * some data cannot be retrieved.
     * </p>
     *
     * @param request the {@link HttpServletRequest} used to store attributes for the view
     * @param id the unique identifier of the teacher whose data should be loaded
     */
    private void loadSafely(HttpServletRequest request, int id) {
        try {
            loadUpdateData(request, id);
        } catch (Exception ignored) {}
    }

    /**
     * Handles errors that occur during the teacher update flow.
     * <p>
     * This method sets an error message as a request attribute and forwards
     * the request to the teacher listing page so the message can be displayed
     * to the user.
     * </p>
     *
     * @param request the {@link HttpServletRequest} containing the current request
     * @param response the {@link HttpServletResponse} used to send the response
     * @param message the error message to be shown to the user
     * @throws ServletException if an error occurs while forwarding the request
     * @throws IOException if an input or output error occurs during request processing
     */
    private void handleError(HttpServletRequest request,
                             HttpServletResponse response,
                             String message)
            throws ServletException, IOException {

        request.setAttribute("error", message);

        request.getRequestDispatcher("/WEB-INF/views/admin/findMany/teacher.jsp")
                .forward(request, response);
    }
}
