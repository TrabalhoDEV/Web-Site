package com.example.schoolservlet.servlets.admin.update;

import com.example.schoolservlet.daos.*;
import com.example.schoolservlet.exceptions.*;
import com.example.schoolservlet.models.*;
import com.example.schoolservlet.utils.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Servlet responsible for updating teachers in the system.
 * This endpoint handles teacher updating.
 */
@WebServlet(name = "admin-update-teacher",value = "/admin/teacher/update")
public class UpdateTeacherServlet extends HttpServlet {
    private final SubjectDAO subjectDAO = new SubjectDAO();
    private final SchoolClassDAO schoolClassDAO = new SchoolClassDAO();
    private final SubjectTeacherDAO subjectTeacherDAO = new SubjectTeacherDAO();
    private final SchoolClassTeacherDAO schoolClassTeacherDAO = new SchoolClassTeacherDAO();
    private final TeacherDAO teacherDAO = new TeacherDAO();

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

            List<Integer> validClassIds = InputValidation.validateIdsExist(schoolClassIdsParam, schoolClassDAO.findAllIds());
            Set<Integer> newSchoolClassIds = new HashSet<>(validClassIds);

            List<Integer> validSubjectIds = InputValidation.validateIdsExist(subjectIdsParam, subjectDAO.findAllIds());
            Set<Integer> newSubjectIds = new HashSet<>(validSubjectIds);

            Set<Integer> allowedSubjectIds = findAllowedSubjectIdsBySchoolClasses(newSchoolClassIds);

            if (!newSubjectIds.isEmpty() && newSchoolClassIds.isEmpty()) {
                throw new ValidationException("Selecione ao menos uma turma antes de escolher as matérias.");
            }

            if (!allowedSubjectIds.containsAll(newSubjectIds)) {
                throw new ValidationException("As matérias selecionadas não pertencem às turmas escolhidas.");
            }

            teacherDAO.update(teacher);

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

            List<SchoolClass> currentSchoolClasses = schoolClassDAO.findByTeacherId(teacher.getId());
            Set<Integer> currentSchoolClassIds = new HashSet<>();
            for (SchoolClass sc : currentSchoolClasses) {
                currentSchoolClassIds.add(sc.getId());
            }

            Set<Integer> toAddClasses = new HashSet<>(newSchoolClassIds);
            toAddClasses.removeAll(currentSchoolClassIds);

            Set<Integer> toRemoveClasses = new HashSet<>(currentSchoolClassIds);
            toRemoveClasses.removeAll(newSchoolClassIds);

            if (!toAddClasses.isEmpty()) {
                List<SchoolClassTeacher> classTeachersToInsert = new ArrayList<>();

                for (Integer classId : toAddClasses) {
                    SchoolClass schoolClass = schoolClassDAO.findById(classId);

                    SchoolClassTeacher sct = new SchoolClassTeacher();
                    sct.setTeacher(teacher);
                    sct.setSchoolClass(schoolClass);
                    classTeachersToInsert.add(sct);
                }

                schoolClassTeacherDAO.createMany(classTeachersToInsert);
            }

            schoolClassTeacherDAO.deleteManyByTeacherAndClasses(teacher.getId(), toRemoveClasses);

            try {
                String assunto = "Edição dos dados do Sistema Escolar";
                String mensagem = "Olá " + OutputFormatService.formatName(teacher.getName()) + ",<br><br>"
                        + "Seus dados foram atualizados!.<br>"
                        + String.format("<a href=\"%s/index.jsp\">Clique aqui para logar</a><br><br>", request.getContextPath())
                        + "Atenciosamente,<br>"
                        + "Secretaria Vértice";

                EmailService.sendEmail(teacher.getEmail(), assunto, mensagem);
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
        } catch (ValidationException e){
            loadSafely(request, id);
            preserveFormSelections(request);
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/update/teacher.jsp").forward(request, response);
        }
    }

    private void loadUpdateData(HttpServletRequest request, int teacherId)
            throws DataException, NotFoundException, ValidationException {

        Teacher teacher = teacherDAO.findById(teacherId);
        List<Subject> teacherSubjects = subjectDAO.findByTeacherId(teacherId);
        List<SchoolClass> allSchoolClasses = schoolClassDAO.findAll();
        List<SchoolClass> teacherSchoolClasses = schoolClassDAO.findByTeacherId(teacherId);
        Map<Integer, List<Subject>> subjectsBySchoolClass = buildSubjectsBySchoolClass(allSchoolClasses);

        request.setAttribute("teacher", teacher);
        request.setAttribute("teacherSubjects", teacherSubjects);
        request.setAttribute("schoolClasses", allSchoolClasses);
        request.setAttribute("teacherSchoolClasses", teacherSchoolClasses);
        request.setAttribute("subjectsBySchoolClass", subjectsBySchoolClass);
    }

    private Map<Integer, List<Subject>> buildSubjectsBySchoolClass(List<SchoolClass> schoolClasses)
            throws DataException {
        Map<Integer, List<Subject>> subjectsBySchoolClass = new HashMap<>();

        for (SchoolClass schoolClass : schoolClasses) {
            subjectsBySchoolClass.put(schoolClass.getId(), subjectDAO.findBySchoolClassId(schoolClass.getId()));
        }

        return subjectsBySchoolClass;
    }

    private Set<Integer> findAllowedSubjectIdsBySchoolClasses(Set<Integer> schoolClassIds)
            throws DataException {
        Set<Integer> allowedSubjectIds = new HashSet<>();

        for (Integer schoolClassId : schoolClassIds) {
            for (Subject subject : subjectDAO.findBySchoolClassId(schoolClassId)) {
                allowedSubjectIds.add(subject.getId());
            }
        }

        return allowedSubjectIds;
    }

    private void preserveFormSelections(HttpServletRequest request) {
        request.setAttribute("submittedSchoolClassIds", parseSelectedIds(request.getParameterValues("schoolClassIds")));
        request.setAttribute("submittedSubjectIds", parseSelectedIds(request.getParameterValues("subjectIds")));
    }

    private Set<Integer> parseSelectedIds(String[] idsParam) {
        Set<Integer> ids = new HashSet<>();
        if (idsParam == null) {
            return ids;
        }

        for (String idParam : idsParam) {
            try {
                ids.add(Integer.parseInt(idParam));
            } catch (NumberFormatException ignored) {
            }
        }

        return ids;
    }

    private void loadSafely(HttpServletRequest request, int id) {
        try {
            loadUpdateData(request, id);
        } catch (Exception ignored) {}
    }

    private void handleError(HttpServletRequest request,
                             HttpServletResponse response,
                             String message)
            throws ServletException, IOException {

        request.setAttribute("error", message);

        request.getRequestDispatcher("/WEB-INF/views/admin/findMany/teacher.jsp")
                .forward(request, response);
    }
}