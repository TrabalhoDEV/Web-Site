package com.example.schoolservlet.daos;

import com.example.schoolservlet.daos.interfaces.GenericDAO;
import com.example.schoolservlet.daos.interfaces.IAdminDAO;
import com.example.schoolservlet.exceptions.*;
import com.example.schoolservlet.models.Admin;
import com.example.schoolservlet.utils.Constants;
import com.example.schoolservlet.utils.InputNormalizer;
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
    public Admin findById(int id) throws DataAccessException, NotFoundException{
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
            throw new DataAccessException("Erro ao buscar admin", sqle);
        }
    }

    public Optional<Admin> findByDocument(String document) throws DataAccessException, RequiredFieldException{
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
                return Optional.of(admin);
            }
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataAccessException("Erro ao buscar admin", sqle);
        }
        return Optional.empty();
    }

    @Override
    public Map<Integer, Admin> findMany(int skip, int take) throws DataAccessException{
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
            throw new DataAccessException("Erro ao listar admins", sqle);
        }

        return admins;
    }

    @Override
    public int totalCount() throws DataAccessException{
        int totalCount = -1;

        try(Connection conn = PostgreConnection.getConnection();
            Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS totalCount FROM admin");

            if (rs.next()){
                totalCount = rs.getInt("totalCount");
            }
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataAccessException("Erro ao contar admins", sqle);
        }

        return totalCount;
    }

    @Override
    public void create(Admin admin) throws DataAccessException, RequiredFieldException{
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
            throw new DataAccessException("Erro ao criar administrador: ", sqle);
        }
    }
    @Override
    public void update(Admin admin) throws NotFoundException, InvalidNumberException, DataAccessException{
        if (admin.getId() <= 0 ) throw new InvalidNumberException("id", "ID deve ser maior do que 0");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("UPDATE admin SET document = ?, email = ? WHERE id = ?")){
            pstmt.setString(1, admin.getDocument());
            pstmt.setString(2, admin.getEmail());
            pstmt.setInt(3, admin.getId());

            if (pstmt.executeUpdate() == 0) throw new NotFoundException("admin", "id", String.valueOf(admin.getId()));
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataAccessException("Erro ao atualizar admin", sqle);
        }
    }
    public void updatePassword(int id, String newPassword) throws NotFoundException, DataAccessException{
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("UPDATE admin SET password = ? WHERE id = ?")){
            pstmt.setString(1, BCrypt.hashpw(newPassword, BCrypt.gensalt(12)));
            pstmt.setInt(2, id);

            if(pstmt.executeUpdate() <= 0) throw new NotFoundException("admin", "id", String.valueOf(id));

        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataAccessException("Erro ao atualizar senha de admin", sqle);
        }
    }

    @Override
    public void delete(int id) throws ValidationException, NotFoundException, DataAccessException{
        if (id <= 0) throw new RequiredFieldException("id");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM admin WHERE id  = ?")){
            pstmt.setInt(1, id);

            if (pstmt.executeUpdate() <= 0) throw new NotFoundException("admin", "id", String.valueOf(id));
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataAccessException("Erro ao deletar administrador: ", sqle);
        }
    }

    @Override
    public boolean login(String document, String password) throws DataAccessException, RequiredFieldException{
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

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataAccessException("Erro ao logar admin", sqle);
        }

        return false;
    }
}
