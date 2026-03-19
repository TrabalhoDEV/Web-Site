package com.example.schoolservlet.daos;

import com.example.schoolservlet.daos.interfaces.GenericDAO;
import com.example.schoolservlet.exceptions.*;
import com.example.schoolservlet.models.Teacher;
import com.example.schoolservlet.utils.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class TeacherDAO implements GenericDAO<Teacher> {

    /**
     * Retrieves a paginated list of teachers from the database.
     *
     * <p>The method returns a map where the key is the teacher ID and the value
     * is the corresponding Teacher object containing the teacher's details.
     * Pagination is controlled by the `skip` (offset) and `take` (limit) parameters,
     * with `take` constrained by a maximum constant.</p>
     *
     * @param skip the number of records to skip for pagination
     * @param take the maximum number of records to retrieve
     * @return a map of teacher IDs to Teacher objects
     * @throws DataException if a database access error occurs during retrieval
     */
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

    /**
     * Retrieves the total number of teachers in the database.
     *
     * <p>This method executes a COUNT query on the teacher table and returns the
     * total number of teacher records. If the query does not return a result,
     * the method returns -1.</p>
     *
     * @return the total count of teachers, or -1 if unavailable
     * @throws DataException if a database access error occurs during the count query
     */
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

    /**
     * Retrieves a specific teacher from the database by their unique ID.
     *
     * <p>This method validates the provided ID and queries the database to fetch
     * the corresponding Teacher object. If no record is found, a NotFoundException
     * is thrown.</p>
     *
     * @param id the unique identifier of the teacher to retrieve
     * @return the Teacher object corresponding to the given ID
     * @throws DataException if a database access error occurs during retrieval
     * @throws ValidationException if the provided ID fails validation
     * @throws NotFoundException if no teacher exists with the given ID
     */
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

    /**
     * Retrieves a teacher from the database by their username.
     *
     * <p>This method validates that the username is provided and normalized before
     * querying the database. If a matching record is found, a Teacher object is
     * returned; otherwise, a NotFoundException is thrown.</p>
     *
     * @param username the username of the teacher to retrieve
     * @return the Teacher object corresponding to the given username
     * @throws DataException if a database access error occurs during retrieval
     * @throws RequiredFieldException if the username is null or empty
     * @throws NotFoundException if no teacher exists with the provided username
     */
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

    /**
     * Deletes a teacher from the database by their unique ID.
     *
     * <p>This method validates the provided ID and attempts to remove the corresponding
     * record from the teacher table. If no record is deleted, a NotFoundException
     * is thrown to indicate that the specified teacher does not exist.</p>
     *
     * @param id the unique identifier of the teacher to delete
     * @throws DataException if a database access error occurs during deletion
     * @throws ValidationException if the provided ID fails validation
     * @throws NotFoundException if no teacher exists with the given ID
     */
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

    /**
     * Retrieves a paginated collection of teachers filtered by name or username.
     * <p>
     * The method performs a case-insensitive search using the provided filter
     * and returns matching teachers ordered by their identifier. If the filter
     * is empty or null, the default retrieval method without filtering is used.
     * </p>
     *
     * @param skip the number of records to skip before collecting the results
     * @param take the maximum number of records to return
     * @param filter the value used to search teachers by name or username
     * @return a map containing teacher entities indexed by their identifier
     * @throws DataException if an error occurs while accessing the database
     */
    public Map<Integer, Teacher> findMany(int skip, int take, String filter) throws DataException {
        boolean hasFilter = filter != null && !filter.isBlank();

        if (!hasFilter) return findMany(skip, take);

        String sql = "SELECT id, name, email, username "
                + "FROM teacher "
                + "WHERE name ILIKE ? OR username ILIKE ? "
                + "ORDER BY id "
                + "LIMIT ? OFFSET ?";

        Map<Integer, Teacher> teachers = new HashMap<>();

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String like = "%" + filter.trim() + "%";
            pstmt.setString(1, like);
            pstmt.setString(2, like);
            pstmt.setInt(3, Math.min(Math.max(take, 0), Constants.MAX_TAKE));
            pstmt.setInt(4, Math.max(skip, 0));

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                teachers.put(rs.getInt("id"), new Teacher(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("username")
                ));
            }

            return teachers;

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao listar professores", sqle);
        }
    }

    /**
     * Counts the total number of teachers that match the provided filter.
     * <p>
     * The method performs a case-insensitive search on the teacher name and
     * username fields. If the filter is null or blank, the total number of
     * teachers is returned using the default counting method.
     * </p>
     *
     * @param filter the value used to filter teachers by name or username
     * @return the total number of teachers that match the filter criteria
     * @throws DataException if an error occurs while accessing the database
     */
    public int count(String filter) throws DataException {
        boolean hasFilter = filter != null && !filter.isBlank();

        if (!hasFilter) return totalCount();

        String sql = "SELECT COUNT(*) FROM teacher "
                + "WHERE name ILIKE ? OR username ILIKE ?";

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String like = "%" + filter.trim() + "%";
            pstmt.setString(1, like);
            pstmt.setString(2, like);

            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao contar professores", sqle);
        }
    }

    /**
     * Creates a new teacher in the database.
     *
     * <p>This method validates that all required fields (name, email, username, and password)
     * are provided before inserting a new record into the teacher table. The password
     * is hashed before storage.</p>
     *
     * @param teacher the Teacher object containing the details to be inserted
     * @throws RequiredFieldException if any required field is missing or empty
     * @throws DataException if a database access error occurs during insertion
     */
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
            pstmt.setString(4, Argon.hash(teacher.getPassword()));

            pstmt.executeUpdate();
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao criar professor", sqle);
        }
    }

    /**
     * Updates an existing teacher's information in the database.
     *
     * <p>This method validates the teacher's ID and required fields (name, email, username)
     * before executing the update. If no record is affected, a NotFoundException is thrown
     * indicating that the teacher does not exist.</p>
     *
     * @param teacher the Teacher object containing updated information
     * @throws DataException if a database access error occurs during the update
     * @throws ValidationException if the teacher ID is invalid
     * @throws NotFoundException if no teacher exists with the given ID
     * @throws RequiredFieldException if any required field is missing or empty
     */
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

    /**
     * Updates the password of a specific teacher in the database.
     *
     * <p>This method validates the teacher's ID and hashes the new password
     * before updating the record. If no record is affected, a NotFoundException
     * is thrown indicating that the teacher does not exist.</p>
     *
     * @param id the unique identifier of the teacher whose password will be updated
     * @param newPassword the new password to set for the teacher
     * @throws DataException if a database access error occurs during the update
     * @throws ValidationException if the provided ID fails validation
     * @throws NotFoundException if no teacher exists with the given ID
     */
    public void updatePassword(int id, String newPassword) throws DataException, NotFoundException, ValidationException{
        InputValidation.validateId(id, "id");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(
                    "UPDATE teacher SET password = ? WHERE id = ?"
            )){
            pstmt.setString(1, Argon.hash(newPassword));
            pstmt.setInt(2, id);

            if (pstmt.executeUpdate() <= 0) throw new NotFoundException("professor", "id", String.valueOf(id));
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao atualizar senha", sqle);
        }
    }

    /**
     * Authenticates a teacher using their username and password.
     *
     * <p>This method validates that both username and password are provided, then
     * queries the database for the teacher's hashed password. The provided password
     * is verified against the stored hash using Argon2. If the username does not
     * exist, a NotFoundException is thrown.</p>
     *
     * @param username the username of the teacher attempting to log in
     * @param password the plain-text password to verify
     * @return true if the credentials are valid, false otherwise
     * @throws DataException if a database access error occurs during authentication
     * @throws ValidationException if required fields are missing or invalid
     * @throws NotFoundException if no teacher exists with the provided username
     * @throws RequiredFieldException if username or password is null or blank
     */
    public boolean login(String username, String password) throws DataException, NotFoundException, ValidationException {
        if (username == null || username.isBlank()) throw new RequiredFieldException("usuário");
        if (password == null || password.isBlank()) throw new RequiredFieldException("senha");

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT password FROM teacher WHERE username = ?"
             )) {
            pstmt.setString(1, username);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Argon.verify(rs.getString("password"), password);
            } else throw new NotFoundException("professor", "usuário", username);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao logar", sqle);
        }
    }
}
