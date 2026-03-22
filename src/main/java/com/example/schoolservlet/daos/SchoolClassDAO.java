package com.example.schoolservlet.daos;

import com.example.schoolservlet.daos.interfaces.GenericDAO;
import com.example.schoolservlet.exceptions.*;
import com.example.schoolservlet.models.SchoolClass;
import com.example.schoolservlet.utils.Constants;
import com.example.schoolservlet.utils.InputNormalizer;
import com.example.schoolservlet.utils.InputValidation;
import com.example.schoolservlet.utils.PostgreConnection;

import java.sql.*;
import java.util.*;

public class SchoolClassDAO implements GenericDAO<SchoolClass> {

    /**
     * Creates a new SchoolClass record in the database.
     * The method validates whether the school year value is present
     * and inserts a new entry into the school_class table.
     *
     * @param schoolClass the SchoolClass object containing the data to be persisted
     * @throws DataException if a database access error occurs during the insert operation
     * @throws ValidationException if the provided data violates validation rules
     * @throws RequiredFieldException if the school year field is null or empty
     */
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

    /**
     * Retrieves multiple SchoolClass records from the database using pagination.
     * The method applies limit and offset parameters to control how many records
     * are returned and from which position the query starts. Each retrieved record
     * is mapped to a SchoolClass object and stored in a map where the key
     * represents the class identifier.
     *
     * @param skip the number of records to skip before starting to return results
     * @param take the maximum number of records to retrieve
     * @return a map containing SchoolClass objects indexed by their unique identifier
     * @throws DataException if a database access error occurs during query execution
     */
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

    /**
     * Retrieves the total number of SchoolClass records stored in the database.
     * The method executes an aggregate COUNT query on the school_class table
     * and returns the resulting value.
     *
     * @return the total number of school class records in the database,
     *         or -1 if the query returns no result
     * @throws DataException if a database access error occurs during query execution
     */
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

    /**
     * Retrieves a collection of school classes from the database with support for
     * pagination and name-based filtering.
     * <p>
     * The method performs a search using a case-insensitive filter on the school
     * year field and returns the results ordered by the class identifier.
     * The number of returned records can be controlled using skip and take
     * parameters to support pagination.
     * </p>
     *
     * @param skip the number of records to skip before starting to collect the result set
     * @param take the maximum number of records to return
     * @param nameFilter the filter used to search classes by school year
     * @return a map containing school class entities indexed by their identifier
     * @throws DataException if an error occurs while accessing the database
     * @throws ValidationException if the provided class name filter is invalid
     */
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

    /**
     * Counts the total number of school classes that match a given filter.
     * <p>
     * The filtering is performed using a case-insensitive comparison on the
     * school year field. The result represents the number of records that
     * satisfy the provided filter criteria.
     * </p>
     *
     * @param nameFilter the filter used to search classes by school year
     * @return the total number of classes that match the filter, or -1 if
     *         no result is obtained
     * @throws DataException if an error occurs while accessing the database
     * @throws ValidationException if the provided class name filter is invalid
     */
    public int count(String nameFilter) throws DataException, ValidationException {
        InputValidation.validateSchoolClassName(nameFilter);
        String sql = "SELECT COUNT(*) FROM school_class " +
                "WHERE school_year ILIKE ?";

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + nameFilter.trim() + "%");

            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getInt(1) : -1;

        } catch (SQLException sqle) {
            throw new DataException("Erro ao contar turmas", sqle);
        }
    }

    /**
     * Retrieves all SchoolClass records from the database.
     * The method executes a query that returns every entry from the
     * school_class table ordered by its identifier and maps each
     * result to a SchoolClass object stored in a list.
     *
     * @return a list containing all SchoolClass objects retrieved from the database
     * @throws DataException if a database access error occurs during query execution
     */
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

    /**
     * Retrieves a SchoolClass record from the database using its unique identifier.
     * The method validates the provided id, executes a query on the school_class
     * table, and maps the result to a SchoolClass object.
     *
     * @param id the unique identifier of the school class to be retrieved
     * @return the SchoolClass object corresponding to the provided id
     * @throws DataException if a database access error occurs during query execution
     * @throws NotFoundException if no school class is found with the specified id
     * @throws ValidationException if the provided id does not pass validation
     */
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

    /**
     * Retrieves all SchoolClass records associated with a specific teacher.
     * The method executes a query that joins the school_class and
     * school_class_teacher tables to obtain the classes linked to the
     * provided teacher identifier. Each result is mapped to a SchoolClass
     * object and stored in a list.
     *
     * @param teacherId the unique identifier of the teacher whose classes will be retrieved
     * @return a list containing the SchoolClass objects associated with the specified teacher
     * @throws DataException if a database access error occurs during query execution
     */
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

    /**
     * Retrieves a SchoolClass record from the database using its school year name.
     * The method executes a query on the school_class table filtering by the
     * provided name and maps the result to a SchoolClass object.
     *
     * @param name the school year name used to search for the class
     * @return the SchoolClass object corresponding to the provided name
     * @throws DataException if a database access error occurs during query execution
     * @throws NotFoundException if no school class is found with the specified name
     */
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

    /**
     * Retrieves the identifiers of all SchoolClass records stored in the database.
     * The method executes a query on the school_class table and collects the id
     * of each record into a list.
     *
     * @return a list containing the identifiers of all school classes
     * @throws DataException if a database access error occurs during query execution
     */
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

    /**
     * Returns the set of subject IDs linked to a given school class.
     *
     * SELECT id_subject FROM school_class_subject WHERE id_school_class = ?
     *
     * @param classId the school class ID
     * @return a Set of subject IDs; empty if the class has no subjects
     * @throws DataException if a database error occurs
     */
    public Set<Integer> findSubjectIdsByClass(int classId) throws DataException {
        String sql = "SELECT id_subject FROM school_class_subject WHERE id_school_class = ?";

        Set<Integer> subjectIds = new java.util.HashSet<>();

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, classId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    subjectIds.add(rs.getInt("id_subject"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataException("Erro ao buscar disciplinas da turma: " + e.getMessage());
        }

        return subjectIds;
    }

    /**
     * Updates the school year information of an existing SchoolClass record in the database.
     * The method validates the identifier and the required school year field of the
     * provided SchoolClass object, then performs an update operation in the
     * school_class table.
     *
     * @param schoolClass the SchoolClass object containing the identifier and updated data
     * @throws DataException if a database access error occurs during the update operation
     * @throws NotFoundException if no school class exists with the specified id
     * @throws ValidationException if the provided id does not pass validation
     * @throws RequiredFieldException if the school year field is null or empty
     */
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

    /**
     * Deletes a SchoolClass record from the database using its unique identifier.
     * The method validates the provided id and performs a delete operation
     * in the school_class table. If no record is affected, a not-found
     * condition is raised.
     *
     * @param id the unique identifier of the school class to be deleted
     * @throws DataException if a database access error occurs during the delete operation
     * @throws NotFoundException if no school class exists with the specified id
     * @throws ValidationException if the provided id does not pass validation
     */
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

    /**
     * Checks whether a specific SchoolClass has any associated students.
     * The method validates the provided school class identifier and executes
     * a query that verifies the existence of at least one student linked to
     * the given class.
     *
     * @param schoolClassId the unique identifier of the school class to be checked
     * @return true if the class has at least one associated student, false otherwise
     * @throws DataException if a database access error occurs during query execution
     * @throws ValidationException if the provided school class id does not pass validation
     */
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