package com.example.schoolservlet.daos;

import com.example.schoolservlet.daos.interfaces.GenericDAO;
import com.example.schoolservlet.models.Subject;
import com.example.schoolservlet.utils.PostgreConnection;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SubjectDAO implements GenericDAO<Subject> {
    // Implement interface methods
    @Override
    public int totalCount(){
        int totalCount = -1;

        try(Connection conn = PostgreConnection.getConnection();
            Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS totalCount FROM subject");

            if (rs.next()){
                totalCount = rs.getInt("totalCount");
            }
        } catch (SQLException | NullPointerException e){
            e.printStackTrace();
        }

        return totalCount;
    }

    @Override
    public Map<Integer, Subject> findMany(int skip, int take){
        Map<Integer, Subject> subjects = new HashMap<>();

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM subject ORDER BY id LIMIT ? OFFSET ?")){
            pstmt.setInt(1, take);
            pstmt.setInt(2, skip);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()){
                Subject subject = new Subject(rs.getInt("id"));
                subject.setName(rs.getString("name"));
                subject.setTeacherName(rs.getString("teacher_name"));
                subject.setTeacherUser(rs.getString("teacher_user"));

                subjects.put(rs.getInt("id"), subject);
            }

        } catch (SQLException | NullPointerException e){
            e.printStackTrace();
        }

        return subjects;
    }

    // Define CRUD methods
    public boolean create(Subject subject){
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO subject" +
                    "(name, teacher_name, teacher_user, teacher_password) VALUES (?, ?, ?, ?)")){
            pstmt.setString(1, subject.getName());
            pstmt.setString(2, subject.getTeacherName());
            pstmt.setString(3, subject.getTeacherUser());
            pstmt.setString(4, subject.getTeacherPassword());

            if (pstmt.executeUpdate() > 0) return true;

        } catch (SQLException | NullPointerException e){
            e.printStackTrace();
        }
        return false;
    }

    public Subject findById(int id){
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM subject WHERE id = ?")){
            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()){
                return new Subject(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("teacher_name"),
                        rs.getString("teacher_user"),
                        rs.getString("teacher_password")
                );
            }

        } catch (SQLException | NullPointerException e){
            e.printStackTrace();
        }
        return null;
    }

    public boolean update(Subject subject){
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("UPDATE subject " +
                    "SET name = ?, teacher_name = ?, teacher_user = ?, teacher_password = ? WHERE id = ?")){
            pstmt.setString(1, subject.getName());
            pstmt.setString(2, subject.getTeacherName());
            pstmt.setString(3, subject.getTeacherUser());
            pstmt.setString(4, subject.getTeacherPassword());
            pstmt.setInt(5, subject.getId());

            if (pstmt.executeUpdate() > 0) return true;

        } catch (SQLException | NullPointerException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(Subject subject){
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM subject WHERE id = ?")){
            pstmt.setInt(1, subject.getId());

            if (pstmt.executeUpdate() > 0) return true;

        } catch (SQLException | NullPointerException e){
            e.printStackTrace();
        }
        return false;
    }
}