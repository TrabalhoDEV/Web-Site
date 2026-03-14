package com.example.schoolservlet.daos;

import com.example.schoolservlet.daos.interfaces.GenericDAO;
import com.example.schoolservlet.exceptions.*;
import com.example.schoolservlet.models.Subject;
import com.example.schoolservlet.utils.Constants;
import com.example.schoolservlet.utils.InputValidation;
import com.example.schoolservlet.utils.PostgreConnection;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class SubjectDAO implements GenericDAO<Subject> {

    @Override
    public Map<Integer, Subject> findMany(int skip, int take) throws DataException {
        Map<Integer, Subject> subjects = new HashMap<>();

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM subject ORDER BY id LIMIT ? OFFSET ?")){
            pstmt.setInt(1, take < 0 ? 0 : (take > Constants.MAX_TAKE ? Constants.MAX_TAKE : take));
            pstmt.setInt(2, skip < 0 ? 0 : skip);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()){
                Subject subject = new Subject(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDate("deadline")
                    );

                subjects.put(rs.getInt("id"), subject);
            }

        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao listar matérias", sqle);
        }

        return subjects;
    }

    public List<Subject> findMany() throws DataException{
        List<Subject> subjects = new ArrayList<>();
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT id, name FROM subject ORDER BY id LIMIT ? OFFSET ?")){

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()){
                Subject subject = new Subject();
                subject.setId(rs.getInt("id"));
                subject.setName(rs.getString("name"));

                subjects.add(subject);
            }

            return subjects;
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao listar matérias", sqle);
        }
    }

    public List<Subject> findAll() throws DataException {
        List<Subject> subjects = new ArrayList<>();

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT * FROM subject ORDER BY id")) {

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                subjects.add(new Subject(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDate("deadline")
                ));
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao listar matérias", sqle);
        }

        return subjects;
    }
    @Override
    public Subject findById(int id) throws DataException, NotFoundException, ValidationException{
        InputValidation.validateId(id, "id");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM subject WHERE id = ?")) {
            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Subject(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDate("deadline")
                );
            } else throw new NotFoundException("matéria", "id", String.valueOf(id));
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao buscar matéria", sqle);
        }
    }

    public List<Subject> findByTeacherId(int teacherId) throws DataException {
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT s.id, s.name, s.deadline " +
                "FROM subject s " +
                "JOIN subject_teacher st ON s.id = st.id_subject " +
                "WHERE st.id_teacher = ?";

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Subject subject = new Subject(rs.getInt("id"),rs.getString("name"),rs.getDate("deadline"));
                    subjects.add(subject);
                }
            }

        } catch (SQLException e) {
            throw new DataException("Erro ao buscar matérias do professor", e);
        }

        return subjects;
    }

    public List<Subject> findBySchoolClassId(int schoolClassId)
            throws DataException {

        String sql =
                "SELECT s.id, s.name, s.deadline " +
                        "FROM subject s " +
                        "INNER JOIN school_class_subject scs ON s.id = scs.id_subject " +
                        "WHERE scs.id_school_class = ? " +
                        "ORDER BY s.name";

        List<Subject> subjects = new ArrayList<>();

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, schoolClassId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Subject subject = new Subject();
                subject.setId(rs.getInt("id"));
                subject.setName(rs.getString("name"));
                subject.setDeadline(rs.getDate("deadline")); // Date, não LocalDate
                subjects.add(subject);
            }

            return subjects;

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao buscar matérias da turma", sqle);
        }
    }

    public List<Integer> findAllIds() throws DataException {
        String sql = "SELECT id FROM subject";
        List<Integer> ids = new ArrayList<>();

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ids.add(rs.getInt("id"));
            }

        } catch (SQLException e) {
            throw new DataException("Erro ao buscar IDs de matérias.");
        }

        return ids;
    }

    public Map<Integer, Subject> findMany(int skip, int take, String nameFilter) throws DataException, ValidationException {
        InputValidation.validateSubjectName(nameFilter);
        Map<Integer, Subject> subjects = new HashMap<>();

        String sql = "SELECT * FROM subject " +
                "WHERE name ILIKE ? " +
                "ORDER BY id LIMIT ? OFFSET ?";

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int paramIndex = 1;

            pstmt.setString(paramIndex++, "%" + nameFilter.trim() + "%");
            pstmt.setInt(paramIndex++, take < 0 ? 0 : Math.min(take, Constants.MAX_TAKE));
            pstmt.setInt(paramIndex,   skip < 0 ? 0 : skip);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                subjects.put(rs.getInt("id"), new Subject(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDate("deadline")
                ));
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao listar matérias", sqle);
        }

        return subjects;
    }

    public int count(String nameFilter) throws DataException, ValidationException {
        InputValidation.validateSubjectName(nameFilter);

        String sql = "SELECT COUNT(*) FROM subject " +
                "WHERE name ILIKE ?";

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + nameFilter.trim() + "%");

            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao contar matérias", sqle);
        }
    }

    @Override
    public int totalCount() throws DataException {
        try(Connection conn = PostgreConnection.getConnection();
            Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS totalCount FROM subject");

            if (rs.next()){
                return rs.getInt("totalCount");
            }
            return -1;
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao contar matérias");
        }
    }

    @Override
    public void create(Subject subject) throws DataException, RequiredFieldException, InvalidDateException{
        if (subject.getName() == null || subject.getName().isEmpty()) throw new RequiredFieldException("nome");
        if (subject.getDeadline() == null) throw new RequiredFieldException("data final");
        if (subject.getDeadline().before(new java.util.Date())) throw new InvalidDateException("data final", "Data final deve ser depois da data de hoje");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO subject" +
                    "(name, deadline) VALUES (?, ?)")){
            pstmt.setString(1, subject.getName());
            pstmt.setDate(2, new java.sql.Date(subject.getDeadline().getTime()));

            pstmt.executeUpdate();
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao criar matéria", sqle);
        }
    }



    @Override
    public void update(Subject subject) throws NotFoundException, DataException, ValidationException {
        if (subject.getName() == null || subject.getName().isEmpty()) throw new RequiredFieldException("nome");
        InputValidation.validateId(subject.getId(), "id");
        if (subject.getDeadline() == null) throw new RequiredFieldException("data final");
        if (subject.getDeadline().before(new java.util.Date())) throw new InvalidDateException("data final", "Data final deve ser depois da data de hoje");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("UPDATE subject " +
                    "SET name = ?, deadline = ? WHERE id = ?")){
            pstmt.setString(1, subject.getName());
            pstmt.setDate(2, new java.sql.Date(subject.getDeadline().getTime()));
            pstmt.setInt(3, subject.getId());

            if (pstmt.executeUpdate() <= 0) throw new NotFoundException("matéria", "id", String.valueOf(subject.getId()));
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao atualizar matéria", sqle);
        }
    }

    @Override
    public void delete(int subjectId) throws DataException, ValidationException, NotFoundException {
        InputValidation.validateId(subjectId, "id da matéria");

        try (Connection conn = PostgreConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement("""
            SELECT 
                s.id,
                s.name,
                s.deadline,
                COUNT(ss.id) as total_enrollments,
                COUNT(CASE WHEN ss.grade1 IS NULL AND ss.grade2 IS NULL THEN 1 END) as students_without_grades,
                COUNT(CASE WHEN ss.grade1 IS NOT NULL OR ss.grade2 IS NOT NULL THEN 1 END) as students_with_grades
            FROM subject s
            LEFT JOIN student_subject ss ON s.id = ss.id_subject
            WHERE s.id = ?
            GROUP BY s.id, s.name, s.deadline
            """)) {
            pstmt.setInt(1, subjectId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    throw new NotFoundException("matéria", "id", String.valueOf(subjectId));
                }

                String name = rs.getString("name");
                Date deadline = rs.getDate("deadline");
                int totalEnrollments = rs.getInt("total_enrollments");
                int studentsWithoutGrades = rs.getInt("students_without_grades");
                int studentsWithGrades = rs.getInt("students_with_grades");

                boolean canDelete = false;
                String reason = "";

                if (totalEnrollments == 0) {
                    canDelete = true;
                } else if (studentsWithoutGrades == totalEnrollments) {
                    canDelete = true;
                } else if (deadline.before(new java.util.Date()) && studentsWithGrades == totalEnrollments) {
                    canDelete = true;
                } else {
                    int studentsWithPartialGrades = totalEnrollments - studentsWithoutGrades - studentsWithGrades;

                    if (deadline.after(new java.util.Date()) || deadline.equals(new java.util.Date())) {
                        reason = String.format(
                                "Não é possível deletar a matéria '%s'. " +
                                        "O prazo (%s) ainda não expirou e existem alunos com notas.",
                                name, deadline
                        );
                    } else {
                        reason = String.format(
                                "Não é possível deletar a matéria '%s'. " +
                                        "Existem %d alunos sem nenhuma nota registrada.",
                                name, studentsWithoutGrades
                        );
                    }
                }

                if (!canDelete) {
                    throw new ValidationException(reason);
                }

                String deleteSql = "DELETE FROM subject WHERE id = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                    deleteStmt.setInt(1, subjectId);
                    deleteStmt.executeUpdate();
                }
            }
        } catch (SQLException sqle){
            throw new DataException("Erro ao deletar matéria", sqle);
        }
    }

    public boolean hasStudentsById(int subjectId) throws DataException, ValidationException {
        InputValidation.validateId(subjectId, "id da matéria");

        String sql = "SELECT EXISTS (SELECT 1 FROM student_subject WHERE id_subject = ?) AS has_students";

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, subjectId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getBoolean("has_students");
            }

            return false;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao validar alunos da matéria", sqle);
        }
    }
}