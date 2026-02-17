package com.example.schoolservlet.daos;

import com.example.schoolservlet.daos.interfaces.GenericDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.InvalidNumberException;
import com.example.schoolservlet.exceptions.NotFoundException;
import com.example.schoolservlet.models.SubjectTeacher;
import com.example.schoolservlet.utils.PostgreConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SubjectTeacherDAO implements GenericDAO<SubjectTeacher> {

    @Override
    public void create(SubjectTeacher subjectTeacher) throws DataException, InvalidNumberException {
        if (subjectTeacher.getIdSubject() <= 0) throw new InvalidNumberException("id da matéria", "ID da matéria deve ser maior do que 0");
        if (subjectTeacher.getIdTeacher() <= 0) throw new InvalidNumberException("id do professor", "ID do professor deve ser maior do que 0");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT INTO subject_teacher (id_subject, id_teacher) VALUES (?, ?)"
            )){

            pstmt.setInt(1, subjectTeacher.getIdSubject());
            pstmt.setInt(2, subjectTeacher.getIdTeacher());

            pstmt.executeUpdate();
        } catch(SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao criar relação de matéria com professor", sqle);
        }
    }

    @Override
    public Map<Integer, SubjectTeacher> findMany(int skip, int take) throws DataException {
        Map<Integer, SubjectTeacher> subjectTeacherMap = new HashMap<>();

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT id, id_subject, id_teacher FROM subject_teacher ORDER BY id LIMIT ? OFFSET ?"
             )){
            pstmt.setInt(1, take);
            pstmt.setInt(2, skip);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()){
                subjectTeacherMap.put(rs.getInt("id"), new SubjectTeacher(
                        rs.getInt("id"),
                        rs.getInt("id_subject"),
                        rs.getInt("id_teacher")
                ));
            }

        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao listar relações entre professores e matérias", sqle);
        }

        return subjectTeacherMap;
    }

    @Override
    public SubjectTeacher findById(int id){
        try(
                Connection conn = PostgreConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(
                        "SELECT id, id_subject, id_teacher FROM subject_teacher WHERE id = ?"
                )
        ) {
            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                return new SubjectTeacher(
                        rs.getInt("id"),
                        rs.getInt("id_subject"),
                        rs.getInt("id_teacher")
                );
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

        return null;
    }

    @Override
    public void update(SubjectTeacher subjectTeacher) throws DataException, NotFoundException, InvalidNumberException {
        if (subjectTeacher.getId() <= 0) throw new InvalidNumberException("id", "ID deve ser maior do que 0");
        if (subjectTeacher.getIdSubject() <= 0) throw new InvalidNumberException("id_subject", "ID da matéria deve ser maior do que 0");
        if (subjectTeacher.getIdTeacher() <= 0) throw new InvalidNumberException("id_teacher", "ID do professor deve ser maior do que 0");

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE subject_teacher SET id_subject = ?, id_teacher = ? WHERE id = ?"
             )) {
            pstmt.setInt(1, subjectTeacher.getIdSubject());
            pstmt.setInt(2, subjectTeacher.getIdTeacher());
            pstmt.setInt(3, subjectTeacher.getId());

            if (pstmt.executeUpdate() <= 0) throw new NotFoundException("subject_teacher", "id", String.valueOf(subjectTeacher.getId()));
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao atualizar subject_teacher", sqle);
        }
    }

    @Override
    public void delete(int id) throws DataException, NotFoundException, InvalidNumberException {
        if (id <= 0) throw new InvalidNumberException("id", "ID deve ser maior do que 0");

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

    @Override
    public int totalCount() throws DataException {
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) AS totalCount FROM subject_teacher");
            ResultSet rs = pstmt.executeQuery()){

            if (rs.next()){
                return rs.getInt("totalCount");
            }
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao contar relações entre professores e matérias", sqle);
        }

        return -1;
    }
}
