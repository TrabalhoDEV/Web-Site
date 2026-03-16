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

public class DashboardDAO {
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