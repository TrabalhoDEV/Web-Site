package com.example.schoolservlet.daos;

import com.example.schoolservlet.daos.interfaces.GenericDAO;
import com.example.schoolservlet.models.SchoolClass;
import com.example.schoolservlet.utils.PostgreConnection;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SchoolClassDAO implements GenericDAO<SchoolClass> {

    @Override
    public boolean create(SchoolClass schoolClass) {
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO school_class (school_year) VALUES (?)")){

            pstmt.setString(1, schoolClass.getSchoolYear());

            return pstmt.executeUpdate() > 0;

        } catch(SQLException sqle){
            sqle.printStackTrace();
            return false;
        }
    }

    @Override
    public Map<Integer, SchoolClass> findMany(int skip, int take) {
        Map<Integer, SchoolClass> schoolClassMap = new HashMap<>();

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT id, school_year FROM school_class ORDER BY id LIMIT ? OFFSET ?")){
            pstmt.setInt(1, take);
            pstmt.setInt(2, skip);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()){
                schoolClassMap.put(rs.getInt("id"), new SchoolClass(
                        rs.getInt("id"),
                        rs.getString("school_year")
                ));
            }

        } catch (SQLException sqle){
            sqle.printStackTrace();
        }

        return schoolClassMap;
    }

    @Override
    public SchoolClass findById(int id){
        try(
                Connection conn = PostgreConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement("SELECT id, school_year FROM school_class WHERE id = ?")
        ) {
            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                return new SchoolClass(
                        rs.getInt("id"),
                        rs.getString("school_year")
                );
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean update(SchoolClass schoolClass) {
        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("UPDATE school_class SET school_year = ? " +
                     "WHERE id = ?")){
            pstmt.setString(1, schoolClass.getSchoolYear());
            pstmt.setInt(2, schoolClass.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException sqle){
            sqle.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM school_class WHERE id = ?")){
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
            PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) AS totalCount FROM school_class");
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
