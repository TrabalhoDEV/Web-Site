package com.example.schoolservlet.daos;

import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.utils.Constants;
import com.example.schoolservlet.utils.PostgreConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object responsible for retrieving dashboard statistics and
 * analytical data used to populate charts and KPI indicators.
 * <p>
 * This class executes queries on database views that aggregate information
 * about students, teachers, classes, subjects, grades, and approval rates.
 * The returned data is structured using maps and lists to facilitate
 * dynamic usage in dashboard visualizations.
 * </p>
 */
public class DashboardDAO {

    /**
     * Retrieves key performance indicators (KPIs) for the dashboard.
     * <p>
     * The data is obtained from the database view that aggregates general
     * statistics about the educational system, including totals and averages.
     * </p>
     *
     * @return a map containing dashboard KPI values such as total students,
     *         total teachers, total classes, total subjects, average students per class,
     *         the class with the most students, and the maximum number of students in a class
     * @throws DataException if an error occurs while accessing the database
     */
    public Map<String, Object> getKpis() throws DataException {
        try (Connection conn = PostgreConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(
                    "SELECT total_students, total_teachers, total_classes, total_subjects, " +
                            "avg_students_per_class, class_with_most_students, max_students_in_class " +
                            "FROM vw_dashboard_kpi"
            );

            if (rs.next()) {
                Map<String, Object> kpi = new LinkedHashMap<>();
                kpi.put("totalStudents",rs.getInt("total_students"));
                kpi.put("totalTeachers",rs.getInt("total_teachers"));
                kpi.put("totalClasses",rs.getInt("total_classes"));
                kpi.put("totalSubjects",rs.getInt("total_subjects"));
                kpi.put("avgStudentsPerClass",rs.getDouble("avg_students_per_class"));
                kpi.put("classWithMostStudents",rs.getString("class_with_most_students"));
                kpi.put("maxStudentsInClass",rs.getInt("max_students_in_class"));
                return kpi;
            }

            return new LinkedHashMap<>();

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao buscar KPIs do dashboard", sqle);
        }
    }

    /**
     * Retrieves the total number of students per class.
     * <p>
     * The data is collected from a database view and ordered by the school year,
     * returning a list of records containing the class identifier and the
     * corresponding number of enrolled students.
     * </p>
     *
     * @return a list of maps containing the school year and the total number
     *         of students for each class
     * @throws DataException if an error occurs while accessing the database
     */
    public List<Map<String, Object>> getStudentsPerClass() throws DataException {
        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT school_year, total_students FROM vw_students_per_class ORDER BY school_year"
             )) {

            ResultSet rs = pstmt.executeQuery();
            List<Map<String, Object>> list = new ArrayList<>();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("schoolYear",rs.getString("school_year"));
                row.put("totalStudents", rs.getInt("total_students"));
                list.add(row);
            }

            return list;

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao buscar alunos por turma", sqle);
        }
    }

    /**
     * Retrieves the average grade for each subject.
     * <p>
     * The data is obtained from a database view that calculates the average
     * grade across students for each subject. The results are ordered in
     * descending order based on the calculated average grade.
     * </p>
     *
     * @return a list of maps containing the subject name and its corresponding
     *         average grade
     * @throws DataException if an error occurs while accessing the database
     */
    public List<Map<String, Object>> getAvgGradesSubject() throws DataException {
        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT subject_name, avg_grade FROM vw_avg_grades_subject ORDER BY avg_grade DESC"
             )) {

            ResultSet rs = pstmt.executeQuery();
            List<Map<String, Object>> list = new ArrayList<>();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("subjectName",rs.getString("subject_name"));
                row.put("avgGrade",rs.getDouble("avg_grade"));
                list.add(row);
            }

            return list;

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao buscar média por disciplina", sqle);
        }
    }

    /**
     * Retrieves the average grade per class.
     * <p>
     * The information is collected from a database view that aggregates
     * grade averages for each class based on the school year.
     * </p>
     *
     * @return a list of maps containing the school year and the corresponding
     *         average grade for that class
     * @throws DataException if an error occurs while accessing the database
     */
    public List<Map<String, Object>> getAvgGradesClass() throws DataException {
        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT school_year, avg_grade FROM vw_avg_grades_class"
             )) {

            ResultSet rs = pstmt.executeQuery();
            List<Map<String, Object>> list = new ArrayList<>();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("schoolYear",rs.getString("school_year"));
                row.put("avgGrade",rs.getDouble("avg_grade"));
                list.add(row);
            }

            return list;

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao buscar média por turma", sqle);
        }
    }

    /**
     * Retrieves the approval rate for each class.
     * <p>
     * The approval rate represents the percentage of students who passed
     * within each class and is calculated through a database view.
     * </p>
     *
     * @return a list of maps containing the school year and the approval
     *         rate associated with that class
     * @throws DataException if an error occurs while accessing the database
     */
    public List<Map<String, Object>> getApprovalRate() throws DataException {
        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT school_year, approval_rate FROM vw_approval_rate_per_class"
             )) {

            ResultSet rs = pstmt.executeQuery();
            List<Map<String, Object>> list = new ArrayList<>();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("schoolYear",rs.getString("school_year"));
                row.put("approvalRate",rs.getDouble("approval_rate"));
                list.add(row);
            }

            return list;

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao buscar taxa de aprovação", sqle);
        }
    }

    /**
     * Retrieves the number of subjects assigned to each teacher.
     * <p>
     * The result is limited to a predefined maximum number of records
     * defined by a system constant and is ordered by the number of
     * subjects in descending order.
     * </p>
     *
     * @return a list of maps containing the teacher name and the total
     *         number of subjects assigned to that teacher
     * @throws DataException if an error occurs while accessing the database
     */
    public List<Map<String, Object>> getSubjectsPerTeacher() throws DataException {
        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT name, total_subjects FROM vw_subjects_per_teacher ORDER BY total_subjects DESC LIMIT ?"
             )) {

            pstmt.setInt(1, Constants.MAX_TAKE);
            ResultSet rs = pstmt.executeQuery();
            List<Map<String, Object>> list = new ArrayList<>();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("name",rs.getString("name"));
                row.put("totalSubjects",rs.getInt("total_subjects"));
                list.add(row);
            }

            return list;

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao buscar disciplinas por professor", sqle);
        }
    }

    /**
     * Retrieves the number of classes assigned to each teacher.
     * <p>
     * The result is limited to a predefined maximum number of records
     * defined by a system constant and is ordered by the number of
     * classes in descending order.
     * </p>
     *
     * @return a list of maps containing the teacher name and the total
     *         number of classes assigned to that teacher
     * @throws DataException if an error occurs while accessing the database
     */
    public List<Map<String, Object>> getClassesPerTeacher() throws DataException {
        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT name, total_classes FROM vw_classes_per_teacher ORDER BY total_classes DESC LIMIT ?"
             )) {

            pstmt.setInt(1, Constants.MAX_TAKE);
            ResultSet rs = pstmt.executeQuery();
            List<Map<String, Object>> list = new ArrayList<>();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("name",rs.getString("name"));
                row.put("totalClasses",rs.getInt("total_classes"));
                list.add(row);
            }

            return list;

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao buscar turmas por professor", sqle);
        }
    }
}