package com.example.schoolservlet.utils;

import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.ValueAlreadyExistsException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FieldAlreadyUsedValidation {

    /**
     * Checks if a specific value already exists in a given table and field in the database.
     *
     * <p>Executes a SELECT COUNT(*) query to determine if the value is present.
     * If the value exists, a {@link ValueAlreadyExistsException} is thrown.
     *
     * @param table       the name of the database table to check
     * @param field       the column name to check for the value
     * @param outputField the display name of the field for exception messages
     * @param value       the value to check for existence
     * @throws DataException              if a SQL or database error occurs
     * @throws ValueAlreadyExistsException if the value already exists in the database
     */
    public static void exists(String table, String field, String outputField, String value) throws DataException, ValueAlreadyExistsException{
        try (Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) AS field_count FROM " + table+ " WHERE " + field + " = ?")) {
            pstmt.setString(1, value);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt("field_count") > 0) throw new ValueAlreadyExistsException(outputField, value);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException(" Erro ao verificar se valor existe ", sqle);
        }
    }
}
