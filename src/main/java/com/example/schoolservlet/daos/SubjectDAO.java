package com.example.schoolservlet.daos;

import com.example.schoolservlet.daos.interfaces.GenericDAO;
import com.example.schoolservlet.exceptions.*;
import com.example.schoolservlet.models.StudentSubject;
import com.example.schoolservlet.models.Subject;
import com.example.schoolservlet.utils.Constants;
import com.example.schoolservlet.utils.InputValidation;
import com.example.schoolservlet.utils.PostgreConnection;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;

public class SubjectDAO implements GenericDAO<Subject> {

    @Override
    public Map<Integer, Subject> findMany(int skip, int take) throws DataException {
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
            throw new DataException("Erro ao listar matérias", sqle);
        }

        return subjects;
    }

    @Override
    public Subject findById(int id) throws DataException, NotFoundException, ValidationException{
        InputValidation.validateId(id, "id");

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
            throw new DataException("Erro ao buscar matéria", sqle);
        }
    }

    @Override
    public int totalCount() throws DataException {
        try(Connection conn = PostgreConnection.getConnection();
            Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS totalCount FROM subject");

            if (rs.next()){
                return rs.getInt("totalCount");
            }
            return -1;
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao contar matérias");
        }
    }

    public Subject findByName(String subjectName) throws DataException, NotFoundException {
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM subject WHERE name = ?")) {
            pstmt.setString(1, subjectName);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Subject(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDate("deadline")
                );
            } else throw new NotFoundException("matéria", "nome", subjectName);
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao buscar matéria", sqle);
        }
    }



    @Override
    public void create(Subject subject) throws DataException, RequiredFieldException, InvalidDateException{
        if (subject.getName() == null || subject.getName().isEmpty()) throw new RequiredFieldException("nome");
        if (subject.getDeadline() == null) throw new RequiredFieldException("data final");
        if (subject.getDeadline().before(new Date())) throw new InvalidDateException("data final", "Data final deve ser depois da data de hoje");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO subject" +
                    "(name, deadline) VALUES (?, ?)")){
            pstmt.setString(1, subject.getName());
            pstmt.setDate(2, new java.sql.Date(subject.getDeadline().getTime()));

            pstmt.executeUpdate();
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao criar matéria", sqle);
        }
    }



    @Override
    public void update(Subject subject) throws NotFoundException, DataException, ValidationException {
        if (subject.getName() == null || subject.getName().isEmpty()) throw new RequiredFieldException("nome");
        InputValidation.validateId(subject.getId(), "id");
        if (subject.getDeadline() == null) throw new RequiredFieldException("data final");
        if (subject.getDeadline().before(new Date())) throw new InvalidDateException("data final", "Data final deve ser depois da data de hoje");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("UPDATE subject " +
                    "SET name = ?, deadline = ? WHERE id = ?")){
            pstmt.setString(1, subject.getName());
            pstmt.setDate(2, new java.sql.Date(subject.getDeadline().getTime()));
            pstmt.setInt(3, subject.getId());

            if (pstmt.executeUpdate() <= 0) throw new NotFoundException("matéria", "id", String.valueOf(subject.getId()));
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao atualizar matéria", sqle);
        }
    }

    @Override
    public void delete(int id) throws DataException, NotFoundException, ValidationException {
        InputValidation.validateId(id, "id");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM subject WHERE id = ?")){
            pstmt.setInt(1, id);

            if (pstmt.executeUpdate() <= 0) throw new NotFoundException("matéria", "id", String.valueOf(id));
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao deletar matéria", sqle);
        }
    }


}