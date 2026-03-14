package com.example.schoolservlet.daos;

import com.example.schoolservlet.daos.interfaces.GenericDAO;
import com.example.schoolservlet.exceptions.*;
import com.example.schoolservlet.models.SchoolClass;
import com.example.schoolservlet.utils.Constants;
import com.example.schoolservlet.utils.InputNormalizer;
import com.example.schoolservlet.utils.InputValidation;
import com.example.schoolservlet.utils.PostgreConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchoolClassDAO implements GenericDAO<SchoolClass> {

    @Override
    public void create(SchoolClass schoolClass) throws DataException, ValidationException {
        if (schoolClass.getSchoolYear() == null || schoolClass.getSchoolYear().isEmpty()) throw new RequiredFieldException("nome da turma");
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO school_class (school_year) VALUES (?)")){

            pstmt.setString(1, schoolClass.getSchoolYear());

            pstmt.executeUpdate();
        } catch(SQLException sqle){
            sqle.printStackTrace();
            if (sqle.getSQLState().equals("23505")){
                throw new ValidationException("Já existe essa turma cadastrada");
            }
            throw new DataException("Erro ao criar turma", sqle);
        }
    }

    @Override
    public Map<Integer, SchoolClass> findMany(int skip, int take) throws DataException {
        Map<Integer, SchoolClass> schoolClassMap = new HashMap<>();

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT id, school_year FROM school_class ORDER BY id LIMIT ? OFFSET ?")){
            pstmt.setInt(1, take < 0 ? 0 : (take > Constants.MAX_TAKE ? Constants.MAX_TAKE : take));
            pstmt.setInt(2, skip < 0 ? 0 : skip);

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
    public int totalCount() throws DataException {
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) AS totalCount FROM school_class");
            ResultSet rs = pstmt.executeQuery()){

            if (rs.next()){
                return rs.getInt("totalCount");
            }
            return -1;
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao contar turmas", sqle);
        }
    }

    public Map<Integer, SchoolClass> findMany(int skip, int take, String nameFilter) throws DataException, ValidationException {
        InputValidation.validateSchoolClassName(nameFilter);
        Map<Integer, SchoolClass> schoolClassMap = new HashMap<>();

        String sql = "SELECT id, school_year FROM school_class " +
                "WHERE school_year ILIKE ? " +
                "ORDER BY id LIMIT ? OFFSET ?";

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int paramIndex = 1;

            pstmt.setString(paramIndex++, "%" + nameFilter.trim() + "%");

            pstmt.setInt(paramIndex++, take < 0 ? 0 : Math.min(take, Constants.MAX_TAKE));
            pstmt.setInt(paramIndex,   skip < 0 ? 0 : skip);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                schoolClassMap.put(rs.getInt("id"), new SchoolClass(
                        rs.getInt("id"),
                        rs.getString("school_year")
                ));
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao listar turmas", sqle);
        }

        return schoolClassMap;
    }

    public int count(String nameFilter) throws DataException, ValidationException {
        InputValidation.validateSchoolClassName(nameFilter);
        String sql = "SELECT COUNT(*) FROM school_class " +
                "WHERE school_year ILIKE ?";

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + nameFilter.trim() + "%");

            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;

        } catch (SQLException sqle) {
            throw new DataException("Erro ao contar turmas", sqle);
        }
    }

    public List<SchoolClass> findAll() throws DataException {
        List<SchoolClass> schoolClasses = new ArrayList<>();

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT id, school_year FROM school_class ORDER BY id")) {

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                schoolClasses.add(new SchoolClass(
                        rs.getInt("id"),
                        rs.getString("school_year")
                ));
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao listar turmas", sqle);
        }

        return schoolClasses;
    }

    @Override
    public SchoolClass findById(int id) throws DataException, NotFoundException, ValidationException {
        InputValidation.validateId(id, "id");

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

    public List<SchoolClass> findByTeacherId(int teacherId) throws DataException {
        List<SchoolClass> classes = new ArrayList<>();
        String sql = "SELECT sc.id, sc.school_year " +
                "FROM school_class sc " +
                "JOIN school_class_teacher sct ON sc.id = sct.id_school_class " +
                "WHERE sct.id_teacher = ?";

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SchoolClass sc = new SchoolClass();
                    sc.setId(rs.getInt("id"));
                    sc.setSchoolYear(rs.getString("school_year"));
                    classes.add(sc);
                }
            }

        } catch (SQLException e) {
            throw new DataException("Erro ao buscar turmas do professor", e);
        }

        return classes;
    }

    public SchoolClass findByName(String name) throws DataException, NotFoundException {
        String sql = "SELECT id, school_year FROM school_class WHERE school_year = ?";

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                SchoolClass schoolClass = new SchoolClass();
                schoolClass.setId(rs.getInt("id"));
                schoolClass.setSchoolYear(rs.getString("school_year"));
                return schoolClass;
            }

            throw new NotFoundException("turma", "nome", name);

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao buscar turma por nome", sqle);
        }
    }

    public List<Integer> findAllIds() throws DataException {
        String sql = "SELECT id FROM school_class";
        List<Integer> ids = new ArrayList<>();

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ids.add(rs.getInt("id"));
            }

        } catch (SQLException e) {
            throw new DataException("Erro ao buscar IDs de turmas.");
        }

        return ids;
    }

    @Override
    public void update(SchoolClass schoolClass) throws DataException, NotFoundException, ValidationException{
        InputValidation.validateId(schoolClass.getId(), "id");
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
    public void delete(int id) throws DataException, NotFoundException, ValidationException {
        InputValidation.validateId(id, "id");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM school_class WHERE id = ?")){
            pstmt.setInt(1, id);

            if (pstmt.executeUpdate() <= 0) throw new NotFoundException("turma", "id", String.valueOf(id));
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao deletar turma", sqle);
        }
    }

    public boolean hasStudentsById(int schoolClassId) throws DataException, ValidationException {
        InputValidation.validateId(schoolClassId, "id da turma");

        String sql = "SELECT EXISTS (SELECT 1 FROM student WHERE id_school_class = ?) AS has_students";

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, schoolClassId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getBoolean("has_students");
            }

            return false;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao validar alunos da turma", sqle);
        }
    }
}