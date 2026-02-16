package com.example.schoolservlet.daos;

import com.example.schoolservlet.daos.interfaces.GenericDAO;
import com.example.schoolservlet.exceptions.*;
import com.example.schoolservlet.models.Subject;
import com.example.schoolservlet.utils.Constants;
import com.example.schoolservlet.utils.PostgreConnection;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;

public class SubjectDAO implements GenericDAO<Subject> {

    @Override
    public Map<Integer, Subject> findMany(int skip, int take) throws DataAccessException{
        Map<Integer, Subject> subjects = new HashMap<>();

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM subject ORDER BY id LIMIT ? OFFSET ?")){
            pstmt.setInt(1, take < 0 ? 0 : (take > Constants.MAX_TAKE ? Constants.MAX_TAKE : take));
            pstmt.setInt(2, skip < 0 ? 0 : skip);

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
    public Subject findById(int id) throws DataAccessException, NotFoundException, InvalidNumberException{
        if (id <= 0) throw new InvalidNumberException("id", "ID deve ser maior do que 0");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM subject WHERE id = ?")) {
            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Subject(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDate("deadline")
                );
            } else throw new NotFoundException("matéria", "id", String.valueOf(id));
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataAccessException("Erro ao busca matéria");
        }
    }

    @Override
    public int totalCount() throws DataAccessException{
        int totalCount = -1;

        try(Connection conn = PostgreConnection.getConnection();
            Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS totalCount FROM subject");

            if (rs.next()){
                totalCount = rs.getInt("totalCount");
            }
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataAccessException("Erro ao contar matérias");
        }

        return totalCount;
    }



    @Override
    public void create(Subject subject) throws DataAccessException, RequiredFieldException, InvalidDateException{
        if (subject.getName() == null || subject.getName().isEmpty()) throw new RequiredFieldException("nome");
        if (subject.getDeadline() == null) throw new RequiredFieldException("data final");
        if (subject.getDeadline().before(new Date())) throw new InvalidDateException("data final", "Data final deve ser depois da data de hoje");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO subject" +
                    "(name, deadline) VALUES (?, ?)")){
            pstmt.setString(1, subject.getName());
            pstmt.setDate(2, (java.sql.Date) subject.getDeadline());

            pstmt.executeUpdate();
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataAccessException("Erro ao criar matéria", sqle);
        }
    }



    @Override
    public void update(Subject subject) throws NotFoundException, DataAccessException, InvalidNumberException, RequiredFieldException, InvalidDateException {
        if (subject.getId() <= 0) throw new InvalidNumberException("id", "ID deve ser maior do que 0");
        if (subject.getDeadline() == null) throw new RequiredFieldException("data final");
        if (subject.getDeadline().before(new Date())) throw new InvalidDateException("data final", "Data final deve ser depois da data de hoje");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("UPDATE subject " +
                    "SET name = ?, deadline = ? WHERE id = ?")){
            pstmt.setString(1, subject.getName());
            pstmt.setDate(2, (java.sql.Date) subject.getDeadline());
            pstmt.setInt(3, subject.getId());

            if (pstmt.executeUpdate() <= 0) throw new NotFoundException("matéria", "id", String.valueOf(subject.getId()));
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataAccessException("Erro ao atualizar matéria", sqle);
        }
    }

    @Override
    public void delete(int id) throws DataAccessException, NotFoundException, InvalidNumberException {
        if (id <= 0) throw new InvalidNumberException("id", "ID deve ser maior do que 0");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM subject WHERE id = ?")){
            pstmt.setInt(1, id);

            if (pstmt.executeUpdate() <= 0) throw new NotFoundException("matéria", "id", String.valueOf(id));
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataAccessException("Erro ao deletar matéria", sqle);
        }
    }
}