package com.example.schoolservlet.daos;

import com.example.schoolservlet.daos.interfaces.GenericDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.NotFoundException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.exceptions.ValueAlreadyExistsException;
import com.example.schoolservlet.models.SchoolClass;
import com.example.schoolservlet.models.SchoolClassSubject;
import com.example.schoolservlet.models.Subject;
import com.example.schoolservlet.utils.InputValidation;
import com.example.schoolservlet.utils.PostgreConnection;

import java.sql.*;
import java.util.*;

public class SchoolClassSubjectDAO implements GenericDAO<SchoolClassSubject> {

    @Override
    public SchoolClassSubject findById(int id) throws ValidationException, NotFoundException, DataException {
        InputValidation.validateId(id, "id");

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT scs.*, " +
                             "sc.school_year, " +
                             "sb.name AS subject_name, " +
                             "sb.deadline AS subject_deadline " +
                             "FROM school_class_subject scs " +
                             "JOIN school_class sc ON sc.id = scs.id_school_class " +
                             "JOIN subject sb ON sb.id = scs.id_subject " +
                             "WHERE scs.id = ?")) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                SchoolClass schoolClass = new SchoolClass();
                schoolClass.setId(rs.getInt("id_school_class"));
                schoolClass.setSchoolYear(rs.getString("school_year"));

                Subject subject = new Subject();
                subject.setId(rs.getInt("id_subject"));
                subject.setName(rs.getString("subject_name"));
                subject.setDeadline(rs.getDate("subject_deadline"));

                return new SchoolClassSubject(rs.getInt("id"), schoolClass, subject);
            } else throw new NotFoundException("school_class_subject", "id", String.valueOf(id));

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao buscar school_class_subject", sqle);
        }
    }

    @Override
    public Map<Integer, SchoolClassSubject> findMany(int skip, int take) throws DataException {
        Map<Integer, SchoolClassSubject> map = new HashMap<>();

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT scs.*, " +
                             "sc.school_year, " +
                             "sb.name AS subject_name, " +
                             "sb.deadline AS subject_deadline " +
                             "FROM school_class_subject scs " +
                             "JOIN school_class sc ON sc.id = scs.id_school_class " +
                             "JOIN subject sb ON sb.id = scs.id_subject " +
                             "ORDER BY scs.id LIMIT ? OFFSET ?")) {

            pstmt.setInt(1, take);
            pstmt.setInt(2, skip);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                SchoolClass schoolClass = new SchoolClass();
                schoolClass.setId(rs.getInt("id_school_class"));
                schoolClass.setSchoolYear(rs.getString("school_year"));

                Subject subject = new Subject();
                subject.setId(rs.getInt("id_subject"));
                subject.setName(rs.getString("subject_name"));
                subject.setDeadline(rs.getDate("subject_deadline"));

                SchoolClassSubject scs = new SchoolClassSubject(rs.getInt("id"), schoolClass, subject);
                map.put(rs.getInt("id"), scs);
            }

            return map;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao listar school_class_subject", sqle);
        }
    }

    @Override
    public int totalCount() throws DataException {
        try (Connection conn = PostgreConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS totalCount FROM school_class_subject");

            if (rs.next()) return rs.getInt("totalCount");
            return 0;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao contar school_class_subject", sqle);
        }
    }

    public Map<Integer, Subject> findManyByClass(int skip, int take, int schoolClassId, String filter) throws DataException {
        String sql = """
            SELECT s.id, s.name, s.deadline
            FROM subject s
            INNER JOIN school_class_subject scs ON scs.id_subject = s.id
            WHERE scs.id_school_class = ?
        """;

        boolean hasFilter = filter != null && !filter.isBlank();
        if (hasFilter) sql += " AND LOWER(s.name) LIKE LOWER(?) ";

        sql += " ORDER BY s.name LIMIT ? OFFSET ?";

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int idx = 1;
            stmt.setInt(idx++, schoolClassId);
            if (hasFilter) stmt.setString(idx++, "%" + filter + "%");
            stmt.setInt(idx++, take);
            stmt.setInt(idx,   skip);

            ResultSet rs = stmt.executeQuery();
            Map<Integer, Subject> map = new LinkedHashMap<>();

            while (rs.next()) {
                Subject subject = new Subject();
                subject.setId(rs.getInt("id"));
                subject.setName(rs.getString("name"));
                subject.setDeadline(rs.getDate("deadline"));
                map.put(subject.getId(), subject);
            }

            return map;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataException("Erro ao buscar matérias da turma.", e);
        }
    }

    public int countByClass(int schoolClassId, String filter) throws DataException {
        String sql = """
            SELECT COUNT(*) FROM subject s
            INNER JOIN school_class_subject scs ON scs.id_subject = s.id
            WHERE scs.id_school_class = ?
        """;

        boolean hasFilter = filter != null && !filter.isBlank();
        if (hasFilter) sql += " AND LOWER(s.name) LIKE LOWER(?)";

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, schoolClassId);
            if (hasFilter) stmt.setString(2, "%" + filter + "%");

            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataException("Erro ao contar matérias da turma.", e);
        }
    }

    @Override
    public void create(SchoolClassSubject scs) throws DataException, ValidationException {
        InputValidation.validateId(scs.getSchoolClass().getId(), "id da turma");
        InputValidation.validateId(scs.getSubject().getId(), "id da matéria");

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO school_class_subject (id_school_class, id_subject) VALUES (?, ?)")) {

            pstmt.setInt(1, scs.getSchoolClass().getId());
            pstmt.setInt(2, scs.getSubject().getId());
            pstmt.executeUpdate();

        } catch (SQLException sqle) {
            if ("23505".equals(sqle.getSQLState())) {
                throw new ValidationException("Essa matéria já está vinculada a essa turma");
            }
            sqle.printStackTrace();
            throw new DataException("Erro ao criar school_class_subject", sqle);
        }
    }

    public void createMany(List<SchoolClassSubject> schoolClassSubjects)
            throws DataException, ValidationException {

        if (schoolClassSubjects == null || schoolClassSubjects.isEmpty()) {
            throw new ValidationException("Lista de associações não pode ser vazia");
        }

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO school_class_subject (id_school_class, id_subject) VALUES (?, ?)")) {

            conn.setAutoCommit(false);

            try {
                for (SchoolClassSubject scs : schoolClassSubjects) {
                    pstmt.setInt(1, scs.getSchoolClass().getId());
                    pstmt.setInt(2, scs.getSubject().getId());
                    pstmt.addBatch();
                }

                pstmt.executeBatch();
                conn.commit();

            } catch (SQLException e) {
                conn.rollback();

                if (e.getSQLState().equals("23505")) {
                    throw new ValidationException("Uma ou mais matérias já estão associadas a esta turma");
                }
                throw e;
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao associar matérias à turma", sqle);
        }
    }

    @Override
    public void update(SchoolClassSubject scs) throws DataException, ValidationException, NotFoundException {
        InputValidation.validateId(scs.getId(), "id");
        InputValidation.validateId(scs.getSchoolClass().getId(), "id da turma");
        InputValidation.validateId(scs.getSubject().getId(), "id da matéria");

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE school_class_subject SET id_school_class = ?, id_subject = ? WHERE id = ?")) {

            pstmt.setInt(1, scs.getSchoolClass().getId());
            pstmt.setInt(2, scs.getSubject().getId());
            pstmt.setInt(3, scs.getId());

            if (pstmt.executeUpdate() <= 0)
                throw new NotFoundException("school_class_subject", "id", String.valueOf(scs.getId()));

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao atualizar school_class_subject", sqle);
        }
    }

    @Override
    public void delete(int id) throws NotFoundException, DataException, ValidationException {
        InputValidation.validateId(id, "id");

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "DELETE FROM school_class_subject WHERE id = ?")) {

            pstmt.setInt(1, id);
            if (pstmt.executeUpdate() <= 0)
                throw new NotFoundException("school_class_subject", "id", String.valueOf(id));

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao deletar school_class_subject", sqle);
        }
    }

    public void deleteManyBySchoolClassAndSubjects(int schoolClassId, Set<Integer> subjectIds)
            throws DataException {

        if (subjectIds == null || subjectIds.isEmpty()) {
            return;
        }

        String placeholders = String.join(",", Collections.nCopies(subjectIds.size(), "?"));

        String sql = "DELETE FROM school_class_subject " +
                "WHERE id_school_class = ? " +
                "AND id_subject IN (" + placeholders + ")";

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, schoolClassId);

            int paramIndex = 2;
            for (Integer subjectId : subjectIds) {
                pstmt.setInt(paramIndex++, subjectId);
            }

            pstmt.executeUpdate();

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao remover associações", sqle);
        }
    }
}