package com.example.schoolservlet.daos;

import com.example.schoolservlet.daos.interfaces.GenericDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.InvalidNumberException;
import com.example.schoolservlet.exceptions.NotFoundException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.Subject;
import com.example.schoolservlet.models.SubjectTeacher;
import com.example.schoolservlet.models.Teacher;
import com.example.schoolservlet.utils.Constants;
import com.example.schoolservlet.utils.InputValidation;
import com.example.schoolservlet.utils.PostgreConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SubjectTeacherDAO implements GenericDAO<SubjectTeacher> {

    @Override
    public void create(SubjectTeacher subjectTeacher) throws DataException, ValidationException {
        InputValidation.validateId(subjectTeacher.getTeacher().getId(), "id_teacher");
        InputValidation.validateId(subjectTeacher.getSubject().getId(), "id_subject");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT INTO subject_teacher (id_subject, id_teacher) VALUES (?, ?)"
            )){

            pstmt.setInt(1, subjectTeacher.getSubject().getId());
            pstmt.setInt(2, subjectTeacher.getTeacher().getId());

            pstmt.executeUpdate();
        } catch(SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao criar relação de matéria com professor", sqle);
        }
    }

    public void createMany(List<SubjectTeacher> subjectTeachers) throws DataException {
        String sql = "INSERT INTO subject_teacher (id_subject, id_teacher) VALUES (?, ?)";
        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            for (SubjectTeacher st : subjectTeachers) {
                ps.setInt(1, st.getSubject().getId());
                ps.setInt(2, st.getTeacher().getId());
                ps.addBatch();
            }

            ps.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataException("Erro ao inserir múltiplos registros de relacionamentos entre matéria e professor");
        }
    }
    @Override
    public Map<Integer, SubjectTeacher> findMany(int skip, int take) throws DataException {
        Map<Integer, SubjectTeacher> subjectTeacherMap = new HashMap<>();

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT st.id, " +
                             "st.id_subject, " +
                             "st.id_teacher, " +
                             "t.name AS teacher_name, " +
                             "t.email AS teacher_email, " +
                             "s.name AS subject_name, " +
                             "s.deadline " +
                             "FROM subject_teacher st " +
                             "JOIN teacher t ON t.id = st.id_teacher " +
                             "JOIN subject s ON s.id = st.id_subject " +
                             "ORDER BY st.id LIMIT ? OFFSET ?"
             )){
            pstmt.setInt(1, take < 0 ? 0 : (take > Constants.MAX_TAKE ? Constants.MAX_TAKE : take));
            pstmt.setInt(2, skip < 0 ? 0 : skip);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()){
                Teacher teacher = new Teacher();
                teacher.setId(rs.getInt("id_teacher"));
                teacher.setName(rs.getString("teacher_name"));
                teacher.setEmail(rs.getString("teacher_email"));

                Subject subject = new Subject();
                subject.setId(rs.getInt("id_subject"));
                subject.setName(rs.getString("subject_name"));
                subject.setDeadline(rs.getDate("deadline"));
                subjectTeacherMap.put(rs.getInt("id"), new SubjectTeacher(
                        rs.getInt("id"),
                        subject,
                        teacher
                ));
            }

        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao listar relações entre professores e matérias", sqle);
        }

        return subjectTeacherMap;
    }

    @Override
    public SubjectTeacher findById(int id) throws DataException, ValidationException, NotFoundException{
        InputValidation.validateId(id, "id");

        try(
                Connection conn = PostgreConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(
                        "SELECT st.id, " +
                                "st.id_subject, " +
                                "st.id_teacher, " +
                                "t.name AS teacher_name, " +
                                "t.email AS teacher_email, " +
                                "s.name AS subject_name, " +
                                "s.deadline " +
                                "FROM subject_teacher st " +
                                "JOIN teacher t ON t.id = st.id_teacher " +
                                "JOIN subject s ON s.id = st.id_subject " +
                                "WHERE st.id = ?"
                )
        ) {
            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                Teacher teacher = new Teacher();
                teacher.setId(rs.getInt("id_teacher"));
                teacher.setName(rs.getString("teacher_name"));
                teacher.setEmail(rs.getString("teacher_email"));

                Subject subject = new Subject();
                subject.setId(rs.getInt("id_subject"));
                subject.setName(rs.getString("subject_name"));
                subject.setDeadline(rs.getDate("deadline"));
                return new SubjectTeacher(
                        rs.getInt("id"),
                        subject,
                        teacher
                );
            } else throw new NotFoundException("subject_teacher", "id", String.valueOf(id));
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao buscar subject_teacher pelo id", sqle);
        }
    }

    @Override
    public void update(SubjectTeacher subjectTeacher) throws DataException, NotFoundException, ValidationException {
        InputValidation.validateId(subjectTeacher.getId(), "id");
        InputValidation.validateId(subjectTeacher.getTeacher().getId(), "id_teacher");
        InputValidation.validateId(subjectTeacher.getSubject().getId(), "id_subject");

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE subject_teacher SET id_subject = ?, id_teacher = ? WHERE id = ?"
             )) {
            pstmt.setInt(1, subjectTeacher.getSubject().getId());
            pstmt.setInt(2, subjectTeacher.getTeacher().getId());
            pstmt.setInt(3, subjectTeacher.getId());

            if (pstmt.executeUpdate() <= 0) throw new NotFoundException("subject_teacher", "id", String.valueOf(subjectTeacher.getId()));
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao atualizar subject_teacher", sqle);
        }
    }

    @Override
    public void delete(int id) throws DataException, NotFoundException, ValidationException {
        InputValidation.validateId(id, "id");

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "DELETE FROM subject_teacher WHERE id = ?"
             )) {
            pstmt.setInt(1, id);

            if (pstmt.executeUpdate() <= 0) throw new NotFoundException("subject_teacher", "id", String.valueOf(id));
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao deletar subject_teacher", sqle);
        }
    }

    public void deleteManyByTeacherAndSubjects(int teacherId, Set<Integer> subjectIds)
            throws DataException {

        if (subjectIds == null || subjectIds.isEmpty()) return;

        String sql = "DELETE FROM subject_teacher WHERE id_teacher = ? AND id_subject = ?";

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (Integer subjectId : subjectIds) {
                ps.setInt(1, teacherId);
                ps.setInt(2, subjectId);
                ps.addBatch();
            }

            ps.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataException("Erro ao remover múltiplos vínculos entre matéria e professor");
        }
    }

    @Override
    public int totalCount() throws DataException {
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) AS totalCount FROM subject_teacher");
            ResultSet rs = pstmt.executeQuery()){

            if (rs.next()){
                return rs.getInt("totalCount");
            }
            return -1;
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao contar relações entre professores e matérias", sqle);
        }
    }
}
