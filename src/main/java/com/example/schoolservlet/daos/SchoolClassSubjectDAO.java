package com.example.schoolservlet.daos;

import com.example.schoolservlet.daos.interfaces.GenericDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.NotFoundException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.exceptions.ValueAlreadyExistsException;
import com.example.schoolservlet.models.SchoolClass;
import com.example.schoolservlet.models.SchoolClassSubject;
import com.example.schoolservlet.models.Subject;
import com.example.schoolservlet.models.Teacher;
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
            stmt.setInt(idx, skip);

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

    public List<Subject> findAvailable(int classId) throws DataException {
        String sql = """
                SELECT id, name, deadline FROM subject
                WHERE id NOT IN (
                    SELECT id_subject FROM school_class_subject
                    WHERE id_school_class = ?
                )
                ORDER BY name
                """;

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, classId);
            ResultSet rs = stmt.executeQuery();

            List<Subject> list = new ArrayList<>();
            while (rs.next()) {
                Subject s = new Subject();
                s.setId(rs.getInt("id"));
                s.setName(rs.getString("name"));
                s.setDeadline(rs.getDate("deadline"));
                list.add(s);
            }
            return list;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataException("Erro ao buscar matérias disponíveis.", e);
        }
    }

    public List<Teacher> findTeachersBySubject(int subjectId) throws DataException {
        String sql = """
                SELECT t.id, t.name FROM teacher t
                INNER JOIN subject_teacher st ON st.id_teacher = t.id
                WHERE st.id_subject = ?
                ORDER BY t.name
                """;

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, subjectId);
            ResultSet rs = stmt.executeQuery();

            List<Teacher> list = new ArrayList<>();
            while (rs.next()) {
                Teacher t = new Teacher();
                t.setId(rs.getInt("id"));
                t.setName(rs.getString("name"));
                list.add(t);
            }
            return list;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataException("Erro ao buscar professores da matéria.", e);
        }
    }

    public List<Integer> findAssignedTeacherIds(int subjectId, int classId) throws DataException {
        String sql = """
                SELECT id_teacher FROM school_class_teacher
                WHERE id_school_class = ?
                AND ? = ANY(subject_list)
                """;

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, classId);
            stmt.setInt(2, subjectId);
            ResultSet rs = stmt.executeQuery();

            List<Integer> ids = new ArrayList<>();
            while (rs.next()) ids.add(rs.getInt("id_teacher"));
            return ids;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataException("Erro ao buscar professores atribuídos.", e);
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

    public void createWithRelations(int classId, int subjectId, String[] teacherIds) throws DataException, ValidationException {
        InputValidation.validateId(classId, "id da turma");
        InputValidation.validateId(subjectId, "id da matéria");
        for (String id : teacherIds) InputValidation.validateId(Integer.parseInt(id), "id do professor");

        Connection conn = null;
        try {
            conn = PostgreConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO school_class_subject (id_school_class, id_subject) VALUES (?, ?)")) {
                stmt.setInt(1, classId);
                stmt.setInt(2, subjectId);
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement("""
                    INSERT INTO student_subject (id_student, id_subject)
                    SELECT id, ? FROM student
                    WHERE id_school_class = ?
                    """)) {
                stmt.setInt(1, subjectId);
                stmt.setInt(2, classId);
                stmt.executeUpdate();
            }

            if (teacherIds != null && teacherIds.length > 0) {
                try (PreparedStatement pstmtSCT = conn.prepareStatement("INSERT INTO school_class_teacher (id_school_class, id_teacher, subject_list) " +
                        "VALUES (?, ?, ?) " +
                        "ON CONFLICT (id_school_class, id_teacher) " +
                        "DO UPDATE SET subject_list = array_append(school_class_teacher.subject_list, ?) "
                )) {

                    for (String teacherIdStr : teacherIds) {
                        int teacherId = Integer.parseInt(teacherIdStr);

                        Array subjectArray = conn.createArrayOf("integer", new Integer[]{subjectId});
                        pstmtSCT.setInt(1, classId);
                        pstmtSCT.setInt(2, teacherId);
                        pstmtSCT.setArray(3, subjectArray);
                        pstmtSCT.setInt(4, subjectId);
                        pstmtSCT.addBatch();
                    }

                    pstmtSCT.executeBatch();
                }
            }

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ignored) {
            }

            if (e.getSQLState().startsWith("23"))
                throw new DataException("Essa matéria já está vinculada a esta turma.", e);
            throw new DataException("Erro ao vincular matéria à turma.", e);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException ignored) {
            }
        }
    }

    public void updateTeacherRelations(int classId, int subjectId, String[] newTeacherIds) throws DataException, ValidationException{
        InputValidation.validateId(classId, "id da turma");
        InputValidation.validateId(subjectId, "id da matéria");
        for (String id : newTeacherIds) InputValidation.validateId(Integer.parseInt(id), "id do professor");

        String sqlUpdateClassTeacher = """
                UPDATE school_class_teacher
                SET subject_list = array_remove(subject_list, ?)
                WHERE id_school_class = ?
                AND id_teacher = ?
                """;

        String sqlDeleteClassTeacher = """
                DELETE FROM school_class_teacher
                WHERE id_school_class = ?
                AND id_teacher = ?
                AND subject_list = '{}'
                """;

        String sqlInsertClassTeacher = """
                INSERT INTO school_class_teacher (id_school_class, id_teacher, subject_list)
                VALUES (?, ?, ?)
                ON CONFLICT (id_school_class, id_teacher)
                DO UPDATE SET subject_list = 
                    CASE WHEN ? = ANY(school_class_teacher.subject_list)
                        THEN school_class_teacher.subject_list
                        ELSE array_append(school_class_teacher.subject_list, ?)
                    END
                """;

        String sqlInsertSubjectTeacher = """
                INSERT INTO subject_teacher (id_subject, id_teacher)
                VALUES (?, ?)
                ON CONFLICT (id_subject, id_teacher) DO NOTHING
                """;

        Connection conn = null;
        try {
            conn = PostgreConnection.getConnection();
            conn.setAutoCommit(false);

            List<Integer> currentIds = findAssignedTeacherIds(subjectId, classId);

            Set<Integer> newIds = new HashSet<>();
            if (newTeacherIds != null) {
                for (String id : newTeacherIds) newIds.add(Integer.parseInt(id));
            }

            // Remove professores que saíram
            for (int teacherId : currentIds) {
                if (!newIds.contains(teacherId)) {
                    try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdateClassTeacher);
                         PreparedStatement pstmtDelete = conn.prepareStatement(sqlDeleteClassTeacher)) {

                        pstmtUpdate.setInt(1, subjectId);
                        pstmtUpdate.setInt(2, classId);
                        pstmtUpdate.setInt(3, teacherId);
                        pstmtUpdate.executeUpdate();

                        pstmtDelete.setInt(1, classId);
                        pstmtDelete.setInt(2, teacherId);
                        pstmtDelete.executeUpdate();
                    }
                }
            }

            // Adiciona professores novos
            if (newTeacherIds != null && newTeacherIds.length > 0) {
                try (PreparedStatement pstmtSCT = conn.prepareStatement(sqlInsertClassTeacher);
                     PreparedStatement pstmtST = conn.prepareStatement(sqlInsertSubjectTeacher)) {

                    for (String teacherIdStr : newTeacherIds) {
                        int teacherId = Integer.parseInt(teacherIdStr);

                        Array subjectArray = conn.createArrayOf("integer", new Integer[]{subjectId});
                        pstmtSCT.setInt(1, classId);
                        pstmtSCT.setInt(2, teacherId);
                        pstmtSCT.setArray(3, subjectArray);
                        pstmtSCT.setInt(4, subjectId);
                        pstmtSCT.setInt(5, subjectId);
                        pstmtSCT.addBatch();

                        pstmtST.setInt(1, subjectId);
                        pstmtST.setInt(2, teacherId);
                        pstmtST.addBatch();
                    }

                    pstmtSCT.executeBatch();
                    pstmtST.executeBatch();
                }
            }

            conn.commit();

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ignored) {
            }
            throw new DataException("Erro ao atualizar professores da matéria.", e);

        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException ignored) {
            }
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

    public void deleteSubjectFromClassAndStudents(int classId, int subjectId) throws DataException, ValidationException {
        InputValidation.validateId(classId, "id da turma");
        InputValidation.validateId(subjectId, "id da matéria");

        Connection conn = null;
        try {
            conn = PostgreConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement("""
                DELETE FROM student_subject
                WHERE id_subject = ?
                AND id_student IN (
                    SELECT id FROM student
                    WHERE id_school_class = ?
                )
                """)) {
                pstmt.setInt(1, subjectId);
                pstmt.setInt(2, classId);
                pstmt.executeUpdate();
            }

            try (PreparedStatement pstmt = conn.prepareStatement("""
                UPDATE school_class_teacher
                SET subject_list = array_remove(subject_list, ?)
                WHERE id_school_class = ?
                """)) {
                pstmt.setInt(1, subjectId);
                pstmt.setInt(2, classId);
                pstmt.executeUpdate();
            }

            try (PreparedStatement pstmt = conn.prepareStatement("""
                DELETE FROM school_class_teacher
                WHERE id_school_class = ?
                AND subject_list = '{}'
                """)) {
                pstmt.setInt(1, classId);
                pstmt.executeUpdate();
            }

            try (PreparedStatement pstmt = conn.prepareStatement("""
                DELETE FROM school_class_subject
                WHERE id_school_class = ?
                AND id_subject = ?
                """)) {
                pstmt.setInt(1, classId);
                pstmt.setInt(2, subjectId);
                pstmt.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ignored) {
            }
            throw new DataException("Erro ao remover matéria da turma.", e);

        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException ignored) {
            }
        }
    }
}