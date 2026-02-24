package com.example.schoolservlet.servlets.teacher;

import com.example.schoolservlet.daos.StudentSubjectDAO;
import com.example.schoolservlet.daos.SubjectDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.NotFoundException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.StudentSubject;
import com.example.schoolservlet.models.Subject;
import com.example.schoolservlet.utils.InputNormalizer;
import com.example.schoolservlet.utils.InputValidation;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/teacher/students/grades/release")
public class ReleaseGradeServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get parameters:
        String studentIdParam = request.getParameter("studentId");
        String subjectNameParam = request.getParameter("subjectName");

        // Validate parameters:
        if (studentIdParam == null || subjectNameParam == null) {
            request.setAttribute("error", "Missing studentId or courseId parameter.");
            request.getRequestDispatcher("/WEB-INF/views/teacher/student/release_grade.jsp").forward(request, response);
            return;
        }

        // get subject id
        int subjectId;
        try{
            subjectId = new SubjectDAO().findByName(subjectNameParam).getId();
        } catch (DataException de){
            request.setAttribute("error", "Error retrieving subject: " + de.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/teacher/student/release_grade.jsp").forward(request, response);
            return;
        } catch (NotFoundException nfe){
            request.setAttribute("error", "Subject not found: " + subjectNameParam);
            request.getRequestDispatcher("/WEB-INF/views/teacher/student/release_grade.jsp").forward(request, response);
            return;
        }

        int studentId;
        try {
            studentIdParam = String.valueOf(InputNormalizer.normalizeEnrollment(studentIdParam));

            InputValidation.validateEnrollment(String.format("%06d", Integer.parseInt(studentIdParam)));
            studentId = Integer.parseInt(studentIdParam);
            
            // load objects:
            StudentSubject studentSubject = new StudentSubjectDAO().findByStudentIdAndCourseId(studentId, subjectId);
            if (studentSubject != null) {
                request.setAttribute("studentSubject", studentSubject);
                request.getRequestDispatcher("/WEB-INF/views/teacher/student/release_grade.jsp").forward(request, response);
            }
            request.setAttribute("error", "No enrollment found for the given student and course.");


        } catch (ValidationException ve) {
            request.setAttribute("error", ve.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/teacher/student/release_grade.jsp").forward(request, response);
        }

    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get parameters:
        String studentIdParam = request.getParameter("studentId");
        String courseIdParam = request.getParameter("courseId");
        String grade1Param = request.getParameter("grade1");
        String grade2Param = request.getParameter("grade2");
        String obs = request.getParameter("obs");

        try {
            int studentId = Integer.parseInt(studentIdParam);
            int courseId = Integer.parseInt(courseIdParam);

            StudentSubjectDAO dao = new StudentSubjectDAO();
            StudentSubject studentSubject = dao.findByStudentIdAndCourseId(studentId, courseId);

            if (studentSubject == null) {
                request.setAttribute("error", "Enrollment not found.");
                request.getRequestDispatcher("/teacher/students").forward(request, response);
                return;
            }

            // Update grades
            if (grade1Param != null && !grade1Param.trim().isEmpty() ) {
                studentSubject.setGrade1(Double.parseDouble(grade1Param));
            }
            if (grade2Param != null && !grade2Param.trim().isEmpty()) {
                studentSubject.setGrade2(Double.parseDouble(grade2Param));
            }
            if (obs != null) {
                studentSubject.setObs(obs);
            }

            dao.update(studentSubject);
            response.sendRedirect(request.getContextPath() + "/teacher/students");

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid grade format.");
            doGet(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Error updating grades: " + e.getMessage());
            doGet(request, response);
        }
    }
}