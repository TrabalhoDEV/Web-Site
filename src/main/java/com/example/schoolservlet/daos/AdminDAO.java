package com.example.schoolservlet.daos;

import com.example.schoolservlet.daos.interfaces.GenericDAO;
import com.example.schoolservlet.daos.interfaces.IAdminDAO;
import com.example.schoolservlet.exceptions.*;
import com.example.schoolservlet.models.Admin;
import com.example.schoolservlet.utils.Constants;
import com.example.schoolservlet.utils.InputNormalizer;
import com.example.schoolservlet.utils.InputValidation;
import com.example.schoolservlet.utils.PostgreConnection;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AdminDAO implements GenericDAO<Admin>, IAdminDAO {
    @Override
    public Admin findById(int id) throws DataException, NotFoundException, ValidationException{
        InputValidation.validateId(id, "id");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM admin WHERE id = ?")){
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                Admin admin = new Admin();
                admin.setId(rs.getInt("id"));
                admin.setEmail(rs.getString("email"));
                admin.setDocument(rs.getString("document"));
                return admin;
            } else {
                throw new NotFoundException("admin", "id", String.valueOf(id));
            }
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao buscar admin", sqle);
        }
    }

    public Admin findByDocument(String document) throws DataException, RequiredFieldException, NotFoundException{
        if (document == null || document.isEmpty()) throw new RequiredFieldException("cpf");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM admin WHERE document = ?")){
            pstmt.setString(1, document);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                Admin admin = new Admin();
                admin.setId(rs.getInt("id"));
                admin.setDocument(rs.getString("document"));
                admin.setEmail(rs.getString("email"));
                return admin;
            } else throw new NotFoundException("admin", "id", document);
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao buscar admin", sqle);
        }
    }

    @Override
    public Map<Integer, Admin> findMany(int skip, int take) throws DataException {
        Map<Integer, Admin> admins = new HashMap<>();

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM admin ORDER BY id LIMIT ? OFFSET ?")){
            pstmt.setInt(1, take < 0 ? 0 : (take > Constants.MAX_TAKE ? Constants.MAX_TAKE : take));
            pstmt.setInt(2, skip < 0 ? 0 : skip);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()){
                Admin admin = new Admin();
                admin.setId(rs.getInt("id"));
                admin.setEmail(rs.getString("email"));
                admin.setDocument(rs.getString("document"));

                admins.put(rs.getInt("id"), admin);
            }

        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao listar admins", sqle);
        }

        return admins;
    }

    @Override
    public int totalCount() throws DataException {
        try(Connection conn = PostgreConnection.getConnection();
            Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS totalCount FROM admin");

            if (rs.next()){
                return rs.getInt("totalCount");
            }
            return -1;
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao contar admins", sqle);
        }
    }

    @Override
    public void create(Admin admin) throws DataException, RequiredFieldException{
        if (admin.getDocument() == null || admin.getDocument().isEmpty()) throw new RequiredFieldException("cpf");
        if (admin.getEmail() == null || admin.getEmail().isEmpty()) throw new RequiredFieldException("email");
        if (admin.getPassword() == null || admin.getPassword().isEmpty()) throw new RequiredFieldException("senha");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO admin (document, email, password) VALUES (?, ?,?)")){
            pstmt.setString(1, admin.getDocument());
            pstmt.setString(2, admin.getEmail());
            pstmt.setString(3, BCrypt.hashpw(admin.getPassword(), BCrypt.gensalt(12)));

            pstmt.executeUpdate();
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao criar administrador: ", sqle);
        }
    }
    @Override
    public void update(Admin admin) throws NotFoundException, DataException, ValidationException {
        InputValidation.validateId(admin.getId(), "id");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("UPDATE admin SET document = ?, email = ? WHERE id = ?")){
            pstmt.setString(1, admin.getDocument());
            pstmt.setString(2, admin.getEmail());
            pstmt.setInt(3, admin.getId());

            if (pstmt.executeUpdate() == 0) throw new NotFoundException("admin", "id", String.valueOf(admin.getId()));
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao atualizar admin", sqle);
        }
    }
    public void updatePassword(int id, String newPassword) throws NotFoundException, DataException, ValidationException {
        InputValidation.validateId(id, "id");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("UPDATE admin SET password = ? WHERE id = ?")){
            pstmt.setString(1, BCrypt.hashpw(newPassword, BCrypt.gensalt(12)));
            pstmt.setInt(2, id);

            if(pstmt.executeUpdate() <= 0) throw new NotFoundException("admin", "id", String.valueOf(id));
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao atualizar senha de admin", sqle);
        }
    }

    @Override
    public void delete(int id) throws ValidationException, NotFoundException, DataException {
        InputValidation.validateId(id, "id");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM admin WHERE id  = ?")){
            pstmt.setInt(1, id);

            if (pstmt.executeUpdate() <= 0) throw new NotFoundException("admin", "id", String.valueOf(id));
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao deletar administrador: ", sqle);
        }
    }

    @Override
    public boolean login(String document, String password) throws DataException, RequiredFieldException{
         if (document == null || document.isEmpty()) throw new RequiredFieldException("cpf");
         if (password == null || password.isEmpty()) throw new RequiredFieldException("senha");

         try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT document, password FROM admin WHERE document = ?")){
            pstmt.setString(1, InputNormalizer.normalizeCpf(document));

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                String hash = rs.getString("password");
                return BCrypt.checkpw(password, hash);
            }
            return false;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao logar admin", sqle);
        }
    }
}
