package com.example.schoolservlet.daos;

import com.example.schoolservlet.models.Student;
import com.example.schoolservlet.models.enums.StudentStatusEnum;
import com.example.schoolservlet.utils.PostgreConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentDAO {
    public Student findByEnrollment(int enrollment){
        Student student = null;
        String sql = "SELECT * FROM student WHERE enrollment = ?";

        try(
            Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, enrollment);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                student = new Student();
                student.setEnrollment(enrollment);
                student.setCpf(rs.getString("cpf"));
                student.setName(rs.getString("name"));
                student.setEmail(rs.getString("email"));
                student.setStatus(StudentStatusEnum.values()[rs.getInt("status")]);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return student;
    }
}
