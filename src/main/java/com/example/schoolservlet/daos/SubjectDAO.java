package com.example.schoolservlet.daos;

import com.example.schoolservlet.daos.interfaces.GenericDAO;
import com.example.schoolservlet.models.Subject;
import com.example.schoolservlet.utils.PostgreConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SubjectDAO implements GenericDAO<Subject> {

    @Override
    public Map<Integer, Subject> findMany(int skip, int take){
        Map<Integer, Subject> subjects = new HashMap<>();

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM subject ORDER BY id LIMIT ? OFFSET ?")){
            pstmt.setInt(1, take);
            pstmt.setInt(2, skip);
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
        }

        return subjects;
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
                        rs.getDate("deadline")
                    );
                }

        } catch (SQLException sqle){
            sqle.printStackTrace();
        }
        return null;
    }

    @Override
    public int totalCount(){
        int totalCount = -1;

        try(Connection conn = PostgreConnection.getConnection();
            Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS totalCount FROM subject");

            if (rs.next()){
                totalCount = rs.getInt("totalCount");
            }
        } catch (SQLException sqle){
            sqle.printStackTrace();
        }

        return totalCount;
    }



    @Override
    public boolean create(Subject subject){
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO subject" +
                    "(name, deadline) VALUES (?, ?)")){
            pstmt.setString(1, subject.getName());
            pstmt.setDate(2, (Date) subject.getDeadline());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException sqle){
            sqle.printStackTrace();
        }
        return false;
    }



    @Override
    public boolean update(Subject subject){
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("UPDATE subject " +
                    "SET name = ?, deadline = ? WHERE id = ?")){
            pstmt.setString(1, subject.getName());
            pstmt.setDate(2, (Date) subject.getDeadline());
            pstmt.setInt(3, subject.getId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException sqle){
            sqle.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id){
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM subject WHERE id = ?")){
            pstmt.setInt(1, id);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException sqle){
            sqle.printStackTrace();
            return false;
        }
    }
}