package com.example.schoolservlet.daos;

import com.example.schoolservlet.daos.interfaces.GenericDAO;
import com.example.schoolservlet.exceptions.*;
import com.example.schoolservlet.models.Teacher;
import com.example.schoolservlet.utils.Constants;
import com.example.schoolservlet.utils.InputNormalizer;
import com.example.schoolservlet.utils.InputValidation;
import com.example.schoolservlet.utils.PostgreConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class TeacherDAO implements GenericDAO<Teacher> {
    // Implement interface methods
    @Override
    public int totalCount() throws DataException {
        try(Connection conn = PostgreConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(
                        "SELECT COUNT(*) AS total_count FROM teacher");){

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()){
                return rs.getInt("total_count");
            }

        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao contar professores", sqle);
        }
        return  -1;
    }

    @Override
    public Map<Integer, Teacher> findMany(int skip, int take) throws DataException {
        Map<Integer, Teacher> teacherMap = new HashMap<>();

        try(Connection conn = PostgreConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(
                "SELECT id, name, email, username FROM teacher ORDER BY id LIMIT ? OFFSET ?")){

            pstmt.setInt(1, take < 0 ? 0 : (take > Constants.MAX_TAKE ? Constants.MAX_TAKE : take));
            pstmt.setInt(2, skip < 0 ? 0 : skip);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                teacherMap.put(rs.getInt("id"), new Teacher(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("username")
                ));
            }
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao listar professores", sqle);
        }
        return teacherMap;
    }

    @Override
    public Teacher findById(int id) throws DataException, NotFoundException, ValidationException{
        InputValidation.validateId(id, "id");
        try(Connection conn = PostgreConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(
                "SELECT id, name, email, username FROM teacher WHERE id = ?")){

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()){
                return new Teacher(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("username")
                );
            } else throw new NotFoundException("professor", "id", String.valueOf(id));
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao buscar professor", sqle);
        }
    }

    public Teacher findByUserName(String username) throws DataException, NotFoundException, RequiredFieldException{
        if (username == null || username.isEmpty()) throw new RequiredFieldException("usuário");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(
                    "SELECT id, name, email, username FROM teacher WHERE username = ?")){

            pstmt.setString(1, InputNormalizer.normalizeUserName(username));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()){
                return new Teacher(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("username")
                );
            } else throw new NotFoundException("professor", "usuário", username);
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao buscar professor pelo usuário", sqle);
        }
    }

    @Override
    public void delete(int id) throws DataException, NotFoundException, ValidationException{
        InputValidation.validateId(id, "id");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(
                    "DELETE FROM teacher WHERE id = ?")){

            pstmt.setInt(1, id);

            if (pstmt.executeUpdate() <= 0) throw new NotFoundException("professor", "id", String.valueOf(id));
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao deletar professor", sqle);
        }
    }

    @Override
    public void create(Teacher teacher) throws DataException, RequiredFieldException {
        if (teacher.getName() == null || teacher.getName().isEmpty()) throw new RequiredFieldException("nome");
        if (teacher.getEmail() == null || teacher.getEmail().isEmpty()) throw new RequiredFieldException("email");
        if (teacher.getUsername() == null || teacher.getUsername().isEmpty()) throw new RequiredFieldException("usuário");
        if (teacher.getPassword() == null || teacher.getPassword().isEmpty()) throw new RequiredFieldException("senha");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO teacher (name, email, username, password) values (?, ?, ?, ?)"
        )){
            pstmt.setString(1, teacher.getName());
            pstmt.setString(2, teacher.getEmail());
            pstmt.setString(3 , teacher.getUsername());
            pstmt.setString(4, BCrypt.hashpw(teacher.getPassword(), BCrypt.gensalt(12)));

            pstmt.executeUpdate();
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao criar professor", sqle);
        }
    }

    @Override
    public void update(Teacher teacher) throws DataException, NotFoundException, ValidationException {
        InputValidation.validateId(teacher.getId(), "id");
        if (teacher.getName() == null || teacher.getName().isEmpty()) throw new RequiredFieldException("nome");
        if (teacher.getEmail() == null || teacher.getEmail().isEmpty()) throw new RequiredFieldException("email");
        if (teacher.getUsername() == null || teacher.getUsername().isEmpty()) throw new RequiredFieldException("usuário");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(
                "UPDATE teacher SET name = ?, email = ?, username = ? WHERE id = ?"
        )){
            pstmt.setString(1, teacher.getName());
            pstmt.setString(2, teacher.getEmail());
            pstmt.setString(3, teacher.getUsername());
            pstmt.setInt(4, teacher.getId());

            if (pstmt.executeUpdate() <= 0) throw new NotFoundException("professor", "id", String.valueOf(teacher.getId()));
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao atualizar professor", sqle);
        }
    }

    // Auth Methods:
    public void updatePassword(int id, String newPassword) throws DataException, NotFoundException, ValidationException{
        InputValidation.validateId(id, "id");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(
                    "UPDATE teacher SET password = ? WHERE id = ?"
            )){
            pstmt.setString(1, BCrypt.hashpw(newPassword, BCrypt.gensalt(12)));
            pstmt.setInt(2, id);

            if (pstmt.executeUpdate() <= 0) throw new NotFoundException("professor", "id", String.valueOf(id));
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao atualizar senha", sqle);
        }
    }

    public boolean login(String username, String password) throws DataException, NotFoundException, ValidationException{
        if (username == null || username.isBlank()) throw new RequiredFieldException("usuário");
        if (password == null || password.isBlank()) throw new RequiredFieldException("senha");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(
                    "SELECT password FROM teacher WHERE username = ?"
            )){
            pstmt.setString(1, username);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return BCrypt.checkpw(password, rs.getString("password"));
            } else throw new NotFoundException("professor", "usuário", username);
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao logar", sqle);
        }
    }
}
