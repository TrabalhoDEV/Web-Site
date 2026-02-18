package com.example.schoolservlet.utils;

import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.ValueAlreadyExistsException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FieldAlreadyUsedValidation {
    public static void exists(String table, String field, String value) throws DataException, ValueAlreadyExistsException{
        try (Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) AS field_count FROM " + table+ " WHERE " + field + " = ?")) {
            pstmt.setString(1, value);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt("field_count") > 0) throw new ValueAlreadyExistsException(field, value);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException(" Erro ao verificar se valor existe ", sqle);
        }
    }
}
