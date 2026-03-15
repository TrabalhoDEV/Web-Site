package com.example.schoolservlet.daos;

import com.example.schoolservlet.daos.interfaces.GenericDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.NotFoundException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.SchoolClass;
import com.example.schoolservlet.models.SchoolClassTeacher;
import com.example.schoolservlet.models.Teacher;
import com.example.schoolservlet.utils.Constants;
import com.example.schoolservlet.utils.InputValidation;
import com.example.schoolservlet.utils.PostgreConnection;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SchoolClassTeacherDAO implements GenericDAO<SchoolClassTeacher> {
    @Override
    public SchoolClassTeacher findById(int id) throws ValidationException, NotFoundException, DataException{
        InputValidation.validateId( id, "id");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT sct.*, "+
                    "t.name, " +
                    "t.email, "+
                    "sc.school_year FROM school_class_teacher sct " +
                    "JOIN teacher t ON sct.id_teacher = t.id " +
                    "JOIN school_class sc ON sc.id  = sct.id_school_class WHERE sct.id = ?")){
            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                SchoolClass schoolClass = new SchoolClass();
                schoolClass.setId(rs.getInt("id_school_class"));
                schoolClass.setSchoolYear(rs.getString("school_year"));

                Teacher teacher = new Teacher();
                teacher.setId(rs.getInt("id_teacher"));
                teacher.setName(rs.getString("name"));
                teacher.setEmail(rs.getString("email"));

                return new SchoolClassTeacher(rs.getInt("id"), schoolClass, teacher);
            } else throw new NotFoundException("school_class_teacher", "id", String.valueOf(id));
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao deletar school_class_teacher", sqle);
        }
    }

    public Map<Integer, Teacher> findManyByClass(int skip, int take, int schoolClassId, String filter) throws DataException {
        boolean hasFilter = filter != null && !filter.isBlank();

        String sql = "SELECT t.id, t.name, t.email, t.username, "
                + "COUNT(st.id_subject) AS subject_count "
                + "FROM teacher t "
                + "INNER JOIN school_class_teacher sct ON sct.id_teacher = t.id "
                + "LEFT JOIN subject_teacher st ON st.id_teacher = t.id "
                + "WHERE sct.id_school_class = ? "
                + (hasFilter ? "AND (t.name ILIKE ? OR t.username ILIKE ?) " : "")
                + "GROUP BY t.id, t.name, t.email, t.username "
                + "ORDER BY t.id "
                + "LIMIT ? OFFSET ?";

        Map<Integer, Teacher> teachers = new HashMap<>();

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int i = 1;
            pstmt.setInt(i++, schoolClassId);

            if (hasFilter) {
                String like = "%" + filter.trim() + "%";
                pstmt.setString(i++, like);
                pstmt.setString(i++, like);
            }

            pstmt.setInt(i++, Math.min(Math.max(take, 0), Constants.MAX_TAKE));
            pstmt.setInt(i,   Math.max(skip, 0));

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Teacher teacher = new Teacher(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("username")
                );
                teacher.setSubjectCount(rs.getInt("subject_count"));
                teachers.put(teacher.getId(), teacher);
            }

            return teachers;

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao listar professores da turma", sqle);
        }
    }

    public int countByClass(int schoolClassId, String filter) throws DataException {
        boolean hasFilter = filter != null && !filter.isBlank();

        String sql = "SELECT COUNT(DISTINCT t.id) FROM teacher t "
                + "INNER JOIN school_class_teacher sct ON sct.id_teacher = t.id "
                + "WHERE sct.id_school_class = ? "
                + (hasFilter ? "AND (t.name ILIKE ? OR t.username ILIKE ?)" : "");

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int i = 1;
            pstmt.setInt(i++, schoolClassId);

            if (hasFilter) {
                String like = "%" + filter.trim() + "%";
                pstmt.setString(i++, like);
                pstmt.setString(i,   like);
            }

            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao contar professores da turma", sqle);
        }
    }

    @Override
    public Map<Integer, SchoolClassTeacher> findMany(int skip, int take) throws DataException{
        Map<Integer, SchoolClassTeacher> schoolClassTeacherMap = new HashMap<>();

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT sct.*, "+
                    "t.name, " +
                    "t.email, "+
                    "sc.school_year FROM school_class_teacher sct " +
                    "JOIN teacher t ON sct.id_teacher = t.id " +
                    "JOIN school_class sc ON sc.id  = sct.id_school_class ORDER BY sct.id LIMIT ? OFFSET ?")){
            pstmt.setInt(1, take);
            pstmt.setInt(2, skip);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()){
                SchoolClass schoolClass = new SchoolClass();
                schoolClass.setId(rs.getInt("id_school_class"));
                schoolClass.setSchoolYear(rs.getString("school_year"));

                Teacher teacher = new Teacher();
                teacher.setId(rs.getInt("id_teacher"));
                teacher.setName(rs.getString("name"));
                teacher.setEmail(rs.getString("email"));

                SchoolClassTeacher schoolClassTeacher = new SchoolClassTeacher();
                schoolClassTeacher.setId(rs.getInt("id"));
                schoolClassTeacher.setSchoolClass(schoolClass);
                schoolClassTeacher.setTeacher(teacher);

                schoolClassTeacherMap.put(rs.getInt("id"), schoolClassTeacher);
            }

            return schoolClassTeacherMap;
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao listar", sqle);
        }
    }

    @Override
    public int totalCount() throws DataException{
        try(Connection conn = PostgreConnection.getConnection();
            Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS totalCount FROM school_class_teacher");

            if (rs.next()){
                return rs.getInt("totalCount");
            }
            return -1;
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao contar school_class_teacher", sqle);
        }
    }

    @Override
    public void create(SchoolClassTeacher schoolClassTeacher) throws DataException, ValidationException{
        InputValidation.validateId(schoolClassTeacher.getSchoolClass().getId(), "id da turma");
        InputValidation.validateId(schoolClassTeacher.getTeacher().getId(), "id do professor");

        try (Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO school_class_teacher (id_teacher, id_school_class) " +
                    "VALUES (?, ?)")){
            pstmt.setInt(1, schoolClassTeacher.getTeacher().getId());
            pstmt.setInt(2, schoolClassTeacher.getSchoolClass().getId());

            pstmt.executeUpdate();
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao contar school_class_teacher", sqle);
        }
    }
    public void createMany(List<SchoolClassTeacher> scts) throws DataException {
        String sql = "INSERT INTO school_class_teacher (id_school_class, id_teacher) VALUES (?, ?)";
        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            for (SchoolClassTeacher sct : scts) {
                ps.setInt(1, sct.getSchoolClass().getId());
                ps.setInt(2, sct.getTeacher().getId());
                ps.addBatch();
            }

            ps.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataException("Erro ao inserir múltiplos registros de relacionamentos entre turmas e professores");
        }
    }

    @Override
    public void update(SchoolClassTeacher schoolClassTeacher) throws DataException, ValidationException, NotFoundException{
        InputValidation.validateId(schoolClassTeacher.getId(), "id");
        InputValidation.validateId(schoolClassTeacher.getSchoolClass().getId(), "id da turma");
        InputValidation.validateId(schoolClassTeacher.getTeacher().getId(), "id do professor");

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("UPDATE school_class_teacher SET id_teacher = ?, " +
                     "id_school_class = ? WHERE id = ?")){
            pstmt.setInt(1, schoolClassTeacher.getTeacher().getId());
            pstmt.setInt(2, schoolClassTeacher.getSchoolClass().getId());
            pstmt.setInt(3, schoolClassTeacher.getId());

            if (pstmt.executeUpdate() <= 0) throw new NotFoundException("school_class_teacher", "id", String.valueOf(schoolClassTeacher.getId()));
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao atualizar relação entre professor e turma", sqle);
        }
    }

    @Override
    public void delete(int id) throws NotFoundException, DataException, ValidationException {
        InputValidation.validateId( id, "id");

        try (Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM school_class_teacher WHERE id = ?")){
            pstmt.setInt(1, id);

            if (pstmt.executeUpdate() <=0) throw new NotFoundException("school_class_teacher", "id", String.valueOf(id));
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao deletar school_class_teacher", sqle);
        }
    }

    public void removeSubjectsFromList(int teacherId, Set<Integer> subjectIds) throws DataException {
        String sql = """
            UPDATE school_class_teacher
            SET subject_list = array_remove(subject_list, ?)
            WHERE id_teacher = ?
            """;

        String sqlDeleteEmpty = """
            DELETE FROM school_class_teacher
            WHERE id_teacher = ?
            AND subject_list = '{}'
            """;

        Connection conn = null;
        try {
            conn = PostgreConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (int subjectId : subjectIds) {
                    pstmt.setInt(1, subjectId);
                    pstmt.setInt(2, teacherId);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            // Deleta registros que ficaram sem nenhuma matéria
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDeleteEmpty)) {
                pstmt.setInt(1, teacherId);
                pstmt.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ignored) {}
            throw new DataException("Erro ao remover matérias do professor.", e);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException ignored) {}
        }
    }
}
