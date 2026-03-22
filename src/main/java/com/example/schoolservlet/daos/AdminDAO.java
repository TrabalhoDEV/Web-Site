package com.example.schoolservlet.daos;

import com.example.schoolservlet.daos.interfaces.GenericDAO;
import com.example.schoolservlet.daos.interfaces.IAdminDAO;
import com.example.schoolservlet.exceptions.*;
import com.example.schoolservlet.models.Admin;
import com.example.schoolservlet.utils.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AdminDAO implements GenericDAO<Admin>, IAdminDAO {

    /**
     * Retrieves an Admin entity from the database using its unique identifier.
     * The method performs an input validation for the provided id, executes a
     * query against the admin table, and maps the result to an Admin object.
     *
     * @param id the unique identifier of the admin to be retrieved
     * @return the Admin object corresponding to the provided id
     * @throws DataException if a database access error occurs during the query execution
     * @throws NotFoundException if no admin is found with the specified id
     * @throws ValidationException if the provided id does not pass the input validation
     */
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
            } else throw new NotFoundException("admin", "id", String.valueOf(id));
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao buscar admin", sqle);
        }
    }

    /**
     * Retrieves an Admin entity from the database using its document identifier.
     * The method validates whether the provided document is present, performs a
     * query on the admin table, and maps the returned result to an Admin object.
     *
     * @param document the unique document identifier of the admin to be retrieved
     * @return the Admin object corresponding to the provided document
     * @throws DataException if a database access error occurs during query execution
     * @throws NotFoundException if no admin is found with the specified document
     * @throws ValidationException if the provided document does not pass validation
     */
    @Override
    public Admin findByDocument(String document) throws DataException, NotFoundException, ValidationException{
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
            } else throw new NotFoundException("admin", "document", document);
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao buscar admin", sqle);
        }
    }

    /**
     * Retrieves multiple Admin entities from the database using pagination.
     * The method applies limit and offset parameters to control how many
     * records are returned and from which position the query starts.
     * Each retrieved record is mapped to an Admin object and stored in a map
     * where the key represents the admin identifier.
     *
     * @param skip the number of records to skip before starting to return results
     * @param take the maximum number of records to retrieve
     * @return a map containing Admin objects indexed by their unique identifier
     * @throws DataException if a database access error occurs during query execution
     */
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

    /**
     * Retrieves the total number of Admin records stored in the database.
     * The method executes an aggregate COUNT query on the admin table and
     * returns the resulting value.
     *
     * @return the total number of admin records in the database, or -1 if the query returns no result
     * @throws DataException if a database access error occurs during query execution
     */
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

    /**
     * Creates a new Admin record in the database.
     * The method validates the required fields of the provided Admin object
     * and inserts a new entry into the admin table. The password is hashed
     * before being stored in the database.
     *
     * @param admin the Admin object containing the data to be persisted
     * @throws DataException if a database access error occurs during the insert operation
     * @throws RequiredFieldException if any required field (document, email, or password) is missing or empty
     */
    @Override
    public void create(Admin admin) throws DataException, RequiredFieldException{
        if (admin.getDocument() == null || admin.getDocument().isEmpty()) throw new RequiredFieldException("cpf");
        if (admin.getEmail() == null || admin.getEmail().isEmpty()) throw new RequiredFieldException("email");
        if (admin.getPassword() == null || admin.getPassword().isEmpty()) throw new RequiredFieldException("senha");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO admin (document, email, password) VALUES (?, ?,?)")){
            pstmt.setString(1, admin.getDocument());
            pstmt.setString(2, admin.getEmail());
            pstmt.setString(3, Argon.hash(admin.getPassword()));

            pstmt.executeUpdate();
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao criar administrador: ", sqle);
        }
    }

    /**
     * Updates the document and email fields of an existing Admin record in the database.
     * The method validates the identifier of the provided Admin object and executes
     * an update operation in the admin table. If no record is affected, a not-found
     * condition is raised.
     *
     * @param admin the Admin object containing the identifier and updated data
     * @throws NotFoundException if no admin exists with the specified id
     * @throws DataException if a database access error occurs during the update operation
     * @throws ValidationException if the provided id does not pass validation
     */
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

    /**
     * Updates the password of an existing Admin record in the database.
     * The method validates the provided identifier and updates the password
     * field for the corresponding admin. The new password is hashed before
     * being stored in the database.
     *
     * @param id the unique identifier of the admin whose password will be updated
     * @param newPassword the new password that will replace the current one
     * @throws NotFoundException if no admin exists with the specified id
     * @throws DataException if a database access error occurs during the update operation
     * @throws ValidationException if the provided id does not pass validation
     */
    public void updatePassword(int id, String newPassword) throws NotFoundException, DataException, ValidationException {
        InputValidation.validateId(id, "id");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("UPDATE admin SET password = ? WHERE id = ?")){
            pstmt.setString(1, Argon.hash(newPassword));
            pstmt.setInt(2, id);

            if(pstmt.executeUpdate() <= 0) throw new NotFoundException("admin", "id", String.valueOf(id));
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao atualizar senha de admin", sqle);
        }
    }

    /**
     * Deletes an Admin record from the database using its unique identifier.
     * The method validates the provided id and executes a delete operation
     * in the admin table. If no record is affected, a not-found condition is raised.
     *
     * @param id the unique identifier of the admin to be deleted
     * @throws ValidationException if the provided id does not pass validation
     * @throws NotFoundException if no admin exists with the specified id
     * @throws DataException if a database access error occurs during the delete operation
     */
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

    /**
     * Authenticates an admin using the provided document and password.
     * The method validates the required fields, retrieves the stored
     * password hash associated with the document, and verifies whether
     * the provided password matches the stored hash.
     *
     * @param document the document identifier used for authentication
     * @param password the plain text password to be verified
     * @return true if the provided credentials match the stored admin credentials, false otherwise
     * @throws DataException if a database access error occurs during query execution
     * @throws RequiredFieldException if the document or password fields are null or empty
     */
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
                return Argon.verify(hash, password);
            }
            return false;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao logar admin", sqle);
        }
    }
}
