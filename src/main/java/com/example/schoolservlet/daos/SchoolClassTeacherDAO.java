package com.example.schoolservlet.daos;

import com.example.schoolservlet.daos.interfaces.GenericDAO;
import com.example.schoolservlet.models.SchoolClass;
import com.example.schoolservlet.models.SchoolClassTeacher;
import com.example.schoolservlet.models.Teacher;
import com.example.schoolservlet.utils.PostgreConnection;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SchoolClassTeacherDAO implements GenericDAO<SchoolClassTeacher> {
    @Override
    public SchoolClassTeacher findById(int id){
        SchoolClassTeacher schoolClassTeacher = null;

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

                schoolClassTeacher = new SchoolClassTeacher();
                schoolClassTeacher.setId(rs.getInt("id"));
                schoolClassTeacher.setTeacherId(teacher.getId());
                schoolClassTeacher.setSchoolClassId(schoolClass.getId());
                schoolClassTeacher.setSchoolClass(schoolClass);
                schoolClassTeacher.setTeacher(teacher);
            }
        } catch (SQLException sqle){
            sqle.printStackTrace();
        }

        return schoolClassTeacher;
    }

    @Override
    public Map<Integer, SchoolClassTeacher> findMany(int skip, int take){
        Map<Integer, SchoolClassTeacher> schoolClassTeacherMap = new HashMap<>();
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT sct.*, "+
                    "t.name, " +
                    "t.email, "+
                    "sc.school_year FROM school_class_teacher sct " +
                    "JOIN teacher t ON sct.id_teacher = t.id " +
                    "JOIN school_class sc ON sc.id  = sct.id_school_class ORDER BY id LIMIT ? OFFSET ?")){
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
                schoolClassTeacher.setTeacherId(teacher.getId());
                schoolClassTeacher.setSchoolClassId(schoolClass.getId());
                schoolClassTeacher.setSchoolClass(schoolClass);
                schoolClassTeacher.setTeacher(teacher);

                schoolClassTeacherMap.put(rs.getInt("id"), schoolClassTeacher);
            }
        } catch (SQLException sqle){
            sqle.printStackTrace();
        }

        return schoolClassTeacherMap;
    }

    @Override
    public int totalCount(){
        int totalCount = -1;

        try(Connection conn = PostgreConnection.getConnection();
            Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS totalCount FROM school_class_teacher");

            if (rs.next()){
                totalCount = rs.getInt("totalCount");
            }
        } catch (SQLException sqle){
            sqle.printStackTrace();
        }

        return totalCount;
    }

    @Override
    public boolean create(SchoolClassTeacher schoolClassTeacher){
        try (Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO school_class_teacher (id_teacher, id_school_class) " +
                    "VALUES (?, ?)")){
            pstmt.setInt(1, schoolClassTeacher.getTeacherId());
            pstmt.setInt(2, schoolClassTeacher.getSchoolClassId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException sqle){
            sqle.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(SchoolClassTeacher schoolClassTeacher){
        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("UPDATE school_class_teacher SET id_teacher = ?, " +
                     "id_school_class = ? WHERE id = ?")){
            pstmt.setInt(1, schoolClassTeacher.getTeacherId());
            pstmt.setInt(2, schoolClassTeacher.getSchoolClassId());
            pstmt.setInt(3, schoolClassTeacher.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException sqle){
            sqle.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id){
        try (Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM school_class_teacher WHERE id = ?")){
            pstmt.setInt(1, id);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException sqle){
            sqle.printStackTrace();
            return false;
        }
    }
}
