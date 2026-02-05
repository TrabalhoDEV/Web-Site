package com.example.schoolservlet.daos;

import com.example.schoolservlet.models.User;
import com.example.schoolservlet.models.enums.UserStatusEnum;
import com.example.schoolservlet.utils.PostgreConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    public User findByEnrollment(int enrollment){
        User user = null;
        String sql = "SELECT * FROM user WHERE enrollment = ?";

        try(
            Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, enrollment);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                user.setEnrollment(enrollment);
                user.setCpf(rs.getString("cpf"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setStatus(UserStatusEnum.values()[rs.getInt("status")]);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }
}
