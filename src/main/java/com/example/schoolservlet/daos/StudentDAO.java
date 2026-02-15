package com.example.schoolservlet.daos;

import com.example.schoolservlet.daos.interfaces.GenericDAO;
import com.example.schoolservlet.daos.interfaces.IStudentDAO;
import com.example.schoolservlet.models.Student;
import com.example.schoolservlet.utils.InputNormalizer;
import com.example.schoolservlet.utils.OutputFormatService;
import com.example.schoolservlet.utils.enums.StudentStatusEnum;
import com.example.schoolservlet.utils.PostgreConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class StudentDAO implements GenericDAO<Student>, IStudentDAO {
    @Override
    public Student findById(int id){
        Student student = null;

        try(
            Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM student WHERE id = ?")
        ) {
            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                student = new Student();
                student.setId(id);
                student.setIdSchoolClass(rs.getInt("id_school_class"));
                student.setCpf(rs.getString("cpf"));
                student.setName(rs.getString("name"));
                student.setEmail(rs.getString("email"));
                student.setStatus(StudentStatusEnum.values()[rs.getInt("status") - 1]);
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

        return student;
    }

    @Override
    public Map<Integer, Student> findMany(int skip, int take) {
        Map<Integer, Student> students = new HashMap<>();

        try (Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM student ORDER BY id LIMIT ? OFFSET ?")){
            pstmt.setInt(1, take);
            pstmt.setInt(2, skip);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()){
                Student student = new Student();
                student.setId(rs.getInt("id"));
                student.setIdSchoolClass(rs.getInt("id_school_class"));
                student.setCpf(rs.getString("cpf"));
                student.setName(rs.getString("name"));
                student.setEmail(rs.getString("email"));
                student.setStatus(StudentStatusEnum.values()[rs.getInt("status") - 1]);

                students.put(rs.getInt("id"), student);
            }

        } catch (SQLException sqle){
            sqle.printStackTrace();;
        }

        return students;
    }

    @Override
    public int totalCount(){
        int totalCount = -1;

        try(Connection conn = PostgreConnection.getConnection();
            Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS totalCount FROM student");

            if (rs.next()){
                totalCount = rs.getInt("totalCount");
            }
        } catch (SQLException sqle){
            sqle.printStackTrace();
        }

        return totalCount;
    }

    @Override
    public boolean create(Student student) {
        try (Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO student " +
                    "(status, cpf, id_school_class) " +
                    "VALUES (?, ?, ?)")){
            pstmt.setInt(1, StudentStatusEnum.INACTIVE.ordinal());
            pstmt.setString(2, InputNormalizer.normalizeCpf(student.getCpf()));
            pstmt.setInt(3, student.getIdSchoolClass());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException sqle){
            sqle.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Student student) {
        try (Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("UPDATE student SET name = ?" +
                    "email = ? WHERE id = ?")){
            pstmt.setString(1, InputNormalizer.normalizeName(student.getName()));
            pstmt.setString(2, InputNormalizer.normalizeEmail(student.getEmail()));
            pstmt.setInt(3, student.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException sqle){
            sqle.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateIdSchoolClass(int id, int idSchoolClass) {
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("UPDATE admin SET id_school_class = ? WHERE id = ?")){
            pstmt.setInt(1, idSchoolClass);
            pstmt.setInt(2, id);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException sqle){
            sqle.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updatePassword(int id, String password) {
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("UPDATE student SET password = ? WHERE id = ?")){
            pstmt.setString(1, BCrypt.hashpw(password, BCrypt.gensalt(12)));
            pstmt.setInt(2, id);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException sqle){
            sqle.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean enrollIn(Student student) {
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("UPDATE admin SET name = ?, email = ?, password = ?, status = ? WHERE id = ?")){
            pstmt.setString(1, InputNormalizer.normalizeName(student.getName()));
            pstmt.setString(2, InputNormalizer.normalizeEmail(student.getEmail()));
            pstmt.setString(3, BCrypt.hashpw(student.getPassword(), BCrypt.gensalt(12)));
            pstmt.setInt(4, StudentStatusEnum.ACTIVE.ordinal());
            pstmt.setInt(5, student.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException sqle){
            sqle.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM student WHERE id = ?")){
            pstmt.setInt(1, id);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException sqle){
            sqle.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean login(String enrollment, String password) {
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT password FROM student WHERE id = ?")){
            pstmt.setInt(1, InputNormalizer.normalizeEnrollment(enrollment));

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                String hash = rs.getString("password");
                return BCrypt.checkpw(password, hash);
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return false;
    }
}
