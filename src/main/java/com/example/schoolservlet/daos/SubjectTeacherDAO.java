package com.example.schoolservlet.daos;

import com.example.schoolservlet.daos.interfaces.GenericDAO;
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
    public boolean create(SubjectTeacher subjectTeacher) {
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT INTO subject_teacher (id_subject, id_teacher) VALUES (?, ?)"
            )){

            pstmt.setInt(1, subjectTeacher.getIdSubject());
            pstmt.setInt(2, subjectTeacher.getIdTeacher());

            return pstmt.executeUpdate() > 0;

        } catch(SQLException sqle){
            sqle.printStackTrace();
            return false;
        }
    }

    @Override
    public Map<Integer, SubjectTeacher> findMany(int skip, int take) {
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
    public boolean update(SubjectTeacher subjectTeacher) {
        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE subject_teacher SET id_subject = ?, id_teacher = ? WHERE id = ?"
             )){
            pstmt.setInt(1, subjectTeacher.getIdSubject());
            pstmt.setInt(2, subjectTeacher.getIdTeacher());
            pstmt.setInt(3, subjectTeacher.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException sqle){
            sqle.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM subject_teacher WHERE id = ?")){
            pstmt.setInt(1, id);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException sqle){
            sqle.printStackTrace();
            return false;
        }
    }

    @Override
    public int totalCount(){
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) AS totalCount FROM subject_teacher");
            ResultSet rs = pstmt.executeQuery()){

            if (rs.next()){
                return rs.getInt("totalCount");
            }
        } catch (SQLException sqle){
            sqle.printStackTrace();
        }

        return -1;
    }
}
