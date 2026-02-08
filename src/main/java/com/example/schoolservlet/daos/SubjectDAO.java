package com.example.schoolservlet.daos;

import com.example.schoolservlet.daos.interfaces.GenericDAO;
import com.example.schoolservlet.models.Subject;
import com.example.schoolservlet.utils.PostgreConnection;
import org.mindrot.jbcrypt.BCrypt;

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
                Subject subject = new Subject(rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("teacher_name"),
                        rs.getString("teacher_user")
                );
                subjects.put(rs.getInt("id"), subject);
            }

        } catch (SQLException | NullPointerException e){
            e.printStackTrace();
        }

        return subjects;
    }

    // Define CRUD methods
    @Override
    public boolean create(Subject subject){
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO subject" +
                    "(name, teacher_name, teacher_user, teacher_password) VALUES (?, ?, ?, ?)")){
            pstmt.setString(1, subject.getName());
            pstmt.setString(2, subject.getTeacherName());
            pstmt.setString(3, subject.getTeacherUser());
            pstmt.setString(4, BCrypt.hashpw(subject.getTeacherPassword(), BCrypt.gensalt()));

            if (pstmt.executeUpdate() > 0) return true;

        } catch (SQLException | NullPointerException e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
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
                        rs.getString("teacher_user")
                );
            }

        } catch (SQLException | NullPointerException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean update(Subject subject){
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("UPDATE subject " +
                    "SET name = ?, teacher_name = ?, teacher_user = ?, WHERE id = ?")){
            pstmt.setString(1, subject.getName());
            pstmt.setString(2, subject.getTeacherName());
            pstmt.setString(3, subject.getTeacherUser());
            pstmt.setInt(4, subject.getId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException | NullPointerException e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id){
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM subject WHERE id = ?")){
            pstmt.setInt(1, id);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException | NullPointerException e){
            e.printStackTrace();
            return false;
        }
    }

    // Login methods
    public boolean updatePassword(Subject subject, String newPassword){
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("UPDATE subject SET teacher_password = ? WHERE id = ?")){
            pstmt.setString(1, BCrypt.hashpw(newPassword, BCrypt.gensalt()));
            pstmt.setInt(2, subject.getId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException | NullPointerException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean login(String username, String password){
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT teacher_password FROM subject WHERE teacher_user = ?")){
            pstmt.setString(1, username);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                String hash = rs.getString("teacher_password");
                return BCrypt.checkpw(password, hash);
            }

        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        }
        return false;
    }
}