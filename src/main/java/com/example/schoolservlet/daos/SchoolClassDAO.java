package com.example.schoolservlet.daos;

import com.example.schoolservlet.daos.interfaces.GenericDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.InvalidNumberException;
import com.example.schoolservlet.exceptions.NotFoundException;
import com.example.schoolservlet.exceptions.RequiredFieldException;
import com.example.schoolservlet.models.SchoolClass;
import com.example.schoolservlet.utils.PostgreConnection;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SchoolClassDAO implements GenericDAO<SchoolClass> {

    @Override
    public void create(SchoolClass schoolClass) throws DataException, RequiredFieldException {
        if (schoolClass.getSchoolYear() == null || schoolClass.getSchoolYear().isEmpty()) throw new RequiredFieldException("nome da turma");
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO school_class (school_year) VALUES (?)")){

            pstmt.setString(1, schoolClass.getSchoolYear());

            pstmt.executeUpdate();
        } catch(SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao criar turma", sqle);
        }
    }

    @Override
    public Map<Integer, SchoolClass> findMany(int skip, int take) throws DataException {
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
            throw new DataException("Erro ao listar turmas", sqle);
        }

        return schoolClassMap;
    }

    @Override
    public SchoolClass findById(int id) throws InvalidNumberException, DataException, NotFoundException {
        if (id <= 0) throw new InvalidNumberException("id", "ID deve ser maior do que 0");
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
            } else throw new NotFoundException("turma", "id", String.valueOf(id));
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao buscar turma");
        }
    }

    @Override
    public void update(SchoolClass schoolClass) throws DataException, InvalidNumberException, NotFoundException, RequiredFieldException{
        if (schoolClass.getId() <= 0) throw new InvalidNumberException("id", "ID deve ser maior do que 0");
        if (schoolClass.getSchoolYear() == null || schoolClass.getSchoolYear().isEmpty()) throw new RequiredFieldException("nome da turma");

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("UPDATE school_class SET school_year = ? " +
                     "WHERE id = ?")){
            pstmt.setString(1, schoolClass.getSchoolYear());
            pstmt.setInt(2, schoolClass.getId());

            if (pstmt.executeUpdate() <= 0) throw new NotFoundException("turma", "id", String.valueOf(schoolClass.getId()));
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao atualizar turma");
        }
    }

    @Override
    public void delete(int id) throws InvalidNumberException, DataException, NotFoundException {
        if (id <= 0) throw new InvalidNumberException("id", "ID deve ser maior do que 0");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM school_class WHERE id = ?")){
            pstmt.setInt(1, id);

            if (pstmt.executeUpdate() <= 0) throw new NotFoundException("turma", "id", String.valueOf(id));
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao deletar turma", sqle);
        }
    }

    @Override
    public int totalCount() throws DataException {
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) AS totalCount FROM school_class");
            ResultSet rs = pstmt.executeQuery()){

            if (rs.next()){
                return rs.getInt("totalCount");
            }
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao contar turmas", sqle);
        }

        return -1;
    }
}
