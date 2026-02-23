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

        } catch (NumberFormatException nfe){
            request.setAttribute("error",nfe.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/findMany/teacher.jsp").forward(request, response);
            return;

        } catch (DataException de){
            request.setAttribute("error", de.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/findMany/teacher.jsp").forward(request, response);
            return;
        } catch (NotFoundException nfe){
            request.setAttribute("error", nfe.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/findMany/teacher.jsp").forward(request, response);
            return;
        } catch (ValidationException ve){
            request.setAttribute("error", ve.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/findMany/teacher.jsp").forward(request, response);
            return;
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
                FieldAlreadyUsedValidation.exists("teacher","email",email);
            }

            if(!teacher.getUsername().equals(username)){
                FieldAlreadyUsedValidation.exists("teacher","username",username);
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

            List<Integer> validClassIds = InputValidation.validateIdsExist(schoolClassIdsParam, schoolClassDAO.findAllIds());

            Set<Integer> newSchoolClassIds = new HashSet<>();
            if (validClassIds != null) {
                newSchoolClassIds.addAll(validClassIds);
            }

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
                String mensagem = "Olá " + teacher.getName() + ",<br><br>"
                        + "Seus dados já foram atualizados!.<br>"
                        + "Atenciosamente,<br>"
                        + "Secretaria Vértice";

                EmailService.sendEmail(teacher.getEmail(), assunto, mensagem);
            } catch (Exception e) {
                e.printStackTrace();
            }

            response.sendRedirect(request.getContextPath()+ "/admin/teacher/find-many");

        }catch (NumberFormatException nfe){
            request.getRequestDispatcher("/WEB-INF/views/admin/findMany/teacher.jsp")
                    .forward(request, response);
            return;
        } catch (DataException de){
            request.getRequestDispatcher("/WEB-INF/views/admin/findMany/teacher.jsp").forward(request, response);
            return;
        } catch (NotFoundException nfe){
            request.getRequestDispatcher("/WEB-INF/views/admin/findMany/teacher.jsp").forward(request, response);
            return;
        } catch (ValueAlreadyExistsException vaee){
            loadSafely(request, id);
            request.setAttribute("error", vaee.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/update/teacher.jsp").forward(request, response);
            return;
        } catch (ValidationException ve){
            loadSafely(request, id);
            request.setAttribute("error", ve.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/update/teacher.jsp").forward(request, response);
            return;
        }
    }

    private void loadUpdateData(HttpServletRequest request, int teacherId)
            throws DataException, NotFoundException, ValidationException {

        Teacher teacher = teacherDAO.findById(teacherId);
        List<Subject> allSubjects = subjectDAO.findAll();
        List<Subject> teacherSubjects =
                subjectDAO.findByTeacherId(teacherId);

        List<SchoolClass> allSchoolClasses =
                schoolClassDAO.findAll();
        List<SchoolClass> teacherSchoolClasses =
                schoolClassDAO.findByTeacherId(teacherId);

        request.setAttribute("teacher", teacher);
        request.setAttribute("subjects", allSubjects);
        request.setAttribute("teacherSubjects", teacherSubjects);
        request.setAttribute("schoolClasses", allSchoolClasses);
        request.setAttribute("teacherSchoolClasses",
                teacherSchoolClasses);
    }

    private void loadSafely(HttpServletRequest request, int id) {
        try {
            loadUpdateData(request, id);
        } catch (Exception ignored) {}
    }
}
