package com.example.schoolservlet.daos;

import com.example.schoolservlet.daos.interfaces.GenericDAO;
import com.example.schoolservlet.models.Teacher;
import com.example.schoolservlet.utils.PostgreConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class TeacherDAO implements GenericDAO<Teacher> {
    // Implement interface methods
    @Override
    public int totalCount() {
        try(Connection conn = PostgreConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(
                        "SELECT COUNT(*) AS total_count FROM teacher");){

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()){
                return rs.getInt("total_count");
            }

        } catch (SQLException sqle){
            sqle.printStackTrace();
        }
        return  -1;
    }

    @Override
    public Map<Integer, Teacher> findMany(int skip, int take) {
        Map<Integer, Teacher> teacherMap = new HashMap<>();

        try(Connection conn = PostgreConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(
                "SELECT id, name, email, username FROM teacher ORDER BY id LIMIT ? OFFSET ?")){

            pstmt.setInt(1, take);
            pstmt.setInt(2, skip);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                teacherMap.put(rs.getInt("id"), new Teacher(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("username")
                ));
            }
        } catch (SQLException sqle){
            sqle.printStackTrace();
        }
        return teacherMap;
    }

    @Override
    public Teacher findById(int id) {
        try(Connection conn = PostgreConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(
                "SELECT id, name, email, username FROM teacher WHERE id = ?")){

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()){
                return new Teacher(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("username")
                );
            }

        } catch (SQLException sqle){
            sqle.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean delete(int id) {
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(
                    "DELETE FROM teacher WHERE id = ?")){

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException sqle){
            sqle.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean create(Teacher teacher) {
        try(Connection conn = PostgreConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO teacher (name, email, username, password) values (?, ?, ?, ?)"
        )){
            pstmt.setString(1, teacher.getName());
            pstmt.setString(2, teacher.getEmail());
            pstmt.setString(3 , teacher.getUsername());
            pstmt.setString(4, teacher.getPassword());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException sqle){
            sqle.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(Teacher teacher) {
        try(Connection conn = PostgreConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(
                "UPDATE teacher SET NAME = ?, EMAIL = ?, USERNAME = ? WHERE ID = ?"
        )){
            pstmt.setString(1, teacher.getName());
            pstmt.setString(2, teacher.getName());
            pstmt.setString(3, teacher.getUsername());
            pstmt.setInt(4, teacher.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException sqle){
            sqle.printStackTrace();
        }
        return false;
    }

    // Auth Methods:
    public boolean updatePassword(int id, String newPassword){
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(
                    "UPDATE teacher SET PASSWORD = ? WHERE ID = ?"
            )){
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, id);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException sqle){
            sqle.printStackTrace();
        }
        return false;
    }

    public boolean login(String username, String password){
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(
                    "SELECT password FROM teacher WHERE username = ?"
            )){

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return BCrypt.checkpw(password, rs.getString("password"));
            }

        } catch (SQLException sqle){
            sqle.printStackTrace();
        }
        return false;
    }
}
