package com.example.schoolservlet.daos;

import com.example.schoolservlet.models.Student;
import com.example.schoolservlet.utils.enums.StudentStatusEnum;
import com.example.schoolservlet.utils.PostgreConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentDAO {
    public Student findById(int id){
        Student student = null;
        String sql = "SELECT * FROM student WHERE id = ?";

        try(
            Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                student = new Student();
                student.setId(id);
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
