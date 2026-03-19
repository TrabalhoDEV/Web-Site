package com.example.schoolservlet.daos;

import com.example.schoolservlet.daos.interfaces.GenericDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.NotFoundException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.SchoolClass;
import com.example.schoolservlet.models.SchoolClassTeacher;
import com.example.schoolservlet.models.Teacher;
import com.example.schoolservlet.utils.Constants;
import com.example.schoolservlet.utils.InputValidation;
import com.example.schoolservlet.utils.PostgreConnection;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SchoolClassTeacherDAO implements GenericDAO<SchoolClassTeacher> {

    /**
     * Retrieves a SchoolClassTeacher record from the database using its unique identifier.
     * The method validates the provided id and executes a query that joins the
     * school_class_teacher, teacher, and school_class tables. The resulting data
     * is mapped into a SchoolClassTeacher object containing the associated
     * SchoolClass and Teacher entities.
     *
     * @param id the unique identifier of the SchoolClassTeacher record to be retrieved
     * @return the SchoolClassTeacher object corresponding to the provided id
     * @throws ValidationException if the provided id does not pass validation
     * @throws NotFoundException if no record is found with the specified id
     * @throws DataException if a database access error occurs during query execution
     */
    @Override
    public SchoolClassTeacher findById(int id) throws ValidationException, NotFoundException, DataException{
        InputValidation.validateId( id, "id");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT sct.*, "+
                    "t.name, " +
                    "t.email, "+
                    "sc.school_year FROM school_class_teacher sct " +
                    "JOIN teacher t ON sct.id_teacher = t.id " +
                    "JOIN school_class sc ON sc.id  = sct.id_school_class WHERE sct.id = ?")){
            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                SchoolClass schoolClass = new SchoolClass();
                schoolClass.setId(rs.getInt("id_school_class"));
                schoolClass.setSchoolYear(rs.getString("school_year"));

                Teacher teacher = new Teacher();
                teacher.setId(rs.getInt("id_teacher"));
                teacher.setName(rs.getString("name"));
                teacher.setEmail(rs.getString("email"));

                return new SchoolClassTeacher(rs.getInt("id"), schoolClass, teacher);
            } else throw new NotFoundException("school_class_teacher", "id", String.valueOf(id));
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao deletar school_class_teacher", sqle);
        }
    }

    /**
     * Retrieves a collection of teachers associated with a specific school class,
     * supporting pagination and optional filtering by name or username.
     * <p>
     * The method queries the relationship between classes and teachers and
     * returns teachers assigned to the specified class. It also calculates
     * the number of subjects associated with each teacher. Results can be
     * filtered and are returned in a paginated format.
     * </p>
     *
     * @param skip the number of records to skip before starting to collect results
     * @param take the maximum number of records to return
     * @param schoolClassId the identifier of the school class whose teachers will be retrieved
     * @param filter an optional filter used to search teachers by name or username
     * @return a map containing teacher entities indexed by their identifier
     * @throws DataException if an error occurs while accessing the database
     */
    public Map<Integer, Teacher> findManyByClass(int skip, int take, int schoolClassId, String filter) throws DataException {
        boolean hasFilter = filter != null && !filter.isBlank();

        String sql = "SELECT t.id, t.name, t.email, t.username, "
                + "COUNT(st.id_subject) AS subject_count "
                + "FROM teacher t "
                + "INNER JOIN school_class_teacher sct ON sct.id_teacher = t.id "
                + "LEFT JOIN subject_teacher st ON st.id_teacher = t.id "
                + "WHERE sct.id_school_class = ? "
                + (hasFilter ? "AND (t.name ILIKE ? OR t.username ILIKE ?) " : "")
                + "GROUP BY t.id, t.name, t.email, t.username "
                + "ORDER BY t.id "
                + "LIMIT ? OFFSET ?";

        Map<Integer, Teacher> teachers = new HashMap<>();

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int i = 1;
            pstmt.setInt(i++, schoolClassId);

            if (hasFilter) {
                String like = "%" + filter.trim() + "%";
                pstmt.setString(i++, like);
                pstmt.setString(i++, like);
            }

            pstmt.setInt(i++, Math.min(Math.max(take, 0), Constants.MAX_TAKE));
            pstmt.setInt(i,   Math.max(skip, 0));

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Teacher teacher = new Teacher(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("username")
                );
                teacher.setSubjectCount(rs.getInt("subject_count"));
                teachers.put(teacher.getId(), teacher);
            }

            return teachers;

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao listar professores da turma", sqle);
        }
    }

    /**
     * Counts the total number of teachers associated with a specific school class,
     * with optional filtering by teacher name or username.
     * <p>
     * The method performs a query on the class-teacher relationship and returns
     * the number of distinct teachers assigned to the specified class. When a
     * filter is provided, only teachers whose name or username matches the
     * filter criteria are included in the count.
     * </p>
     *
     * @param schoolClassId the identifier of the school class whose teachers will be counted
     * @param filter an optional filter used to search teachers by name or username
     * @return the total number of teachers associated with the specified class
     * @throws DataException if an error occurs while accessing the database
     */
    public int countByClass(int schoolClassId, String filter) throws DataException {
        boolean hasFilter = filter != null && !filter.isBlank();

        String sql = "SELECT COUNT(DISTINCT t.id) FROM teacher t "
                + "INNER JOIN school_class_teacher sct ON sct.id_teacher = t.id "
                + "WHERE sct.id_school_class = ? "
                + (hasFilter ? "AND (t.name ILIKE ? OR t.username ILIKE ?)" : "");

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int i = 1;
            pstmt.setInt(i++, schoolClassId);

            if (hasFilter) {
                String like = "%" + filter.trim() + "%";
                pstmt.setString(i++, like);
                pstmt.setString(i,   like);
            }

            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao contar professores da turma", sqle);
        }
    }

    /**
     * Retrieves multiple SchoolClassTeacher records from the database using pagination.
     * The method executes a query that joins the school_class_teacher, teacher,
     * and school_class tables to obtain related class and teacher information.
     * Each result is mapped to a SchoolClassTeacher object and stored in a map
     * where the key represents the record identifier.
     *
     * @param skip the number of records to skip before starting to return results
     * @param take the maximum number of records to retrieve
     * @return a map containing SchoolClassTeacher objects indexed by their unique identifier
     * @throws DataException if a database access error occurs during query execution
     */
    @Override
    public Map<Integer, SchoolClassTeacher> findMany(int skip, int take) throws DataException{
        Map<Integer, SchoolClassTeacher> schoolClassTeacherMap = new HashMap<>();

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT sct.*, "+
                    "t.name, " +
                    "t.email, "+
                    "sc.school_year FROM school_class_teacher sct " +
                    "JOIN teacher t ON sct.id_teacher = t.id " +
                    "JOIN school_class sc ON sc.id  = sct.id_school_class ORDER BY sct.id LIMIT ? OFFSET ?")){
            pstmt.setInt(1, take);
            pstmt.setInt(2, skip);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()){
                SchoolClass schoolClass = new SchoolClass();
                schoolClass.setId(rs.getInt("id_school_class"));
                schoolClass.setSchoolYear(rs.getString("school_year"));

                Teacher teacher = new Teacher();
                teacher.setId(rs.getInt("id_teacher"));
                teacher.setName(rs.getString("name"));
                teacher.setEmail(rs.getString("email"));

                SchoolClassTeacher schoolClassTeacher = new SchoolClassTeacher();
                schoolClassTeacher.setId(rs.getInt("id"));
                schoolClassTeacher.setSchoolClass(schoolClass);
                schoolClassTeacher.setTeacher(teacher);

                schoolClassTeacherMap.put(rs.getInt("id"), schoolClassTeacher);
            }

            return schoolClassTeacherMap;
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao listar", sqle);
        }
    }

    /**
     * Retrieves the total number of SchoolClassTeacher records stored in the database.
     * The method executes an aggregate COUNT query on the school_class_teacher table
     * and returns the resulting value.
     *
     * @return the total number of SchoolClassTeacher records in the database,
     *         or -1 if the query returns no result
     * @throws DataException if a database access error occurs during query execution
     */
    @Override
    public int totalCount() throws DataException{
        try(Connection conn = PostgreConnection.getConnection();
            Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS totalCount FROM school_class_teacher");

            if (rs.next()){
                return rs.getInt("totalCount");
            }
            return -1;
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao contar school_class_teacher", sqle);
        }
    }

    /**
     * Creates a new association between a Teacher and a SchoolClass in the database.
     * The method validates the identifiers of the related teacher and school class
     * and inserts a new record into the school_class_teacher table representing
     * the relationship between them.
     *
     * @param schoolClassTeacher the SchoolClassTeacher object containing the teacher
     *                           and school class identifiers to be associated
     * @throws DataException if a database access error occurs during the insert operation
     * @throws ValidationException if the provided identifiers do not pass validation
     */
    @Override
    public void create(SchoolClassTeacher schoolClassTeacher) throws DataException, ValidationException{
        InputValidation.validateId(schoolClassTeacher.getSchoolClass().getId(), "id da turma");
        InputValidation.validateId(schoolClassTeacher.getTeacher().getId(), "id do professor");

        try (Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO school_class_teacher (id_teacher, id_school_class) " +
                    "VALUES (?, ?)")){
            pstmt.setInt(1, schoolClassTeacher.getTeacher().getId());
            pstmt.setInt(2, schoolClassTeacher.getSchoolClass().getId());

            pstmt.executeUpdate();
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao contar school_class_teacher", sqle);
        }
    }

    /**
     * Creates multiple associations between SchoolClass and Teacher records in the database.
     * The method iterates through a list of SchoolClassTeacher objects and inserts each
     * relationship into the school_class_teacher table using batch processing within
     * a single transaction.
     *
     * @param scts the list of SchoolClassTeacher objects containing the associations
     *             between school classes and teachers to be persisted
     * @throws DataException if a database access error occurs during the batch insert operation
     */
    public void createMany(List<SchoolClassTeacher> scts) throws DataException {
        String sql = "INSERT INTO school_class_teacher (id_school_class, id_teacher) VALUES (?, ?)";
        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            for (SchoolClassTeacher sct : scts) {
                ps.setInt(1, sct.getSchoolClass().getId());
                ps.setInt(2, sct.getTeacher().getId());
                ps.addBatch();
            }

            ps.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataException("Erro ao inserir múltiplos registros de relacionamentos entre turmas e professores");
        }
    }

    /**
     * Updates an existing association between a Teacher and a SchoolClass in the database.
     * The method validates the identifiers of the SchoolClassTeacher record, the related
     * teacher, and the school class, then updates the corresponding record in the
     * school_class_teacher table.
     *
     * @param schoolClassTeacher the SchoolClassTeacher object containing the identifier
     *                           and updated relationship data
     * @throws DataException if a database access error occurs during the update operation
     * @throws ValidationException if any of the provided identifiers do not pass validation
     * @throws NotFoundException if no record exists with the specified SchoolClassTeacher id
     */
    @Override
    public void update(SchoolClassTeacher schoolClassTeacher) throws DataException, ValidationException, NotFoundException{
        InputValidation.validateId(schoolClassTeacher.getId(), "id");
        InputValidation.validateId(schoolClassTeacher.getSchoolClass().getId(), "id da turma");
        InputValidation.validateId(schoolClassTeacher.getTeacher().getId(), "id do professor");

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("UPDATE school_class_teacher SET id_teacher = ?, " +
                     "id_school_class = ? WHERE id = ?")){
            pstmt.setInt(1, schoolClassTeacher.getTeacher().getId());
            pstmt.setInt(2, schoolClassTeacher.getSchoolClass().getId());
            pstmt.setInt(3, schoolClassTeacher.getId());

            if (pstmt.executeUpdate() <= 0) throw new NotFoundException("school_class_teacher", "id", String.valueOf(schoolClassTeacher.getId()));
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao atualizar relação entre professor e turma", sqle);
        }
    }

    /**
     * Updates teacher associations by replacing occurrences of a specific teacher
     * identifier with a new teacher identifier in the school_class_teacher records.
     * The method validates both identifiers and performs an update operation
     * affecting all records that reference the old teacher.
     *
     * @param oldTeacherId the identifier of the teacher currently associated with the records
     * @param newTeacherId the identifier of the teacher that will replace the old one
     * @throws DataException if a database access error occurs during the update operation
     * @throws ValidationException if any of the provided identifiers do not pass validation
     * @throws NotFoundException if no records exist with the specified old teacher identifier
     */
    public void updateTeacher(int oldTeacherId, int newTeacherId) throws DataException, ValidationException, NotFoundException{
        InputValidation.validateId(oldTeacherId, "id");
        InputValidation.validateId(newTeacherId, "id");

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("UPDATE SET id_teacher = ? WHERE id_teacher = ?")){
            pstmt.setInt(1, newTeacherId);
            pstmt.setInt(2, oldTeacherId);

            if (pstmt.executeUpdate() <= 0) throw new NotFoundException("school_class_teacher", "id", String.valueOf(oldTeacherId));
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao atualizar relação entre professor e turma", sqle);
        }
    }

    /**
     * Deletes a SchoolClassTeacher record from the database using its unique identifier.
     * The method validates the provided id and executes a delete operation on the
     * school_class_teacher table. If no record is affected by the operation,
     * a not-found condition is raised.
     *
     * @param id the unique identifier of the SchoolClassTeacher record to be deleted
     * @throws NotFoundException if no record exists with the specified id
     * @throws DataException if a database access error occurs during the delete operation
     * @throws ValidationException if the provided id does not pass validation
     */
    @Override
    public void delete(int id) throws NotFoundException, DataException, ValidationException {
        InputValidation.validateId( id, "id");

        try (Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM school_class_teacher WHERE id = ?")){
            pstmt.setInt(1, id);

            if (pstmt.executeUpdate() <=0) throw new NotFoundException("school_class_teacher", "id", String.valueOf(id));
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao deletar school_class_teacher", sqle);
        }
    }

    /**
     * Removes a set of subjects from the subject list assigned to a specific teacher
     * across all related class-teacher records.
     * <p>
     * The method updates the teacher's subject list by removing each provided subject
     * identifier. After the update, any class-teacher records that no longer contain
     * subjects are deleted. All operations are executed within a database transaction
     * to ensure consistency.
     * </p>
     *
     * @param teacherId the identifier of the teacher whose subject assignments will be updated
     * @param subjectIds the set of subject identifiers to be removed from the teacher's subject list
     * @throws DataException if an error occurs while accessing or modifying the database
     */
    public void removeSubjectsFromList(int teacherId, Set<Integer> subjectIds) throws DataException {
        String sql = """
            UPDATE school_class_teacher
            SET subject_list = array_remove(subject_list, ?)
            WHERE id_teacher = ?
            """;

        String sqlDeleteEmpty = """
            DELETE FROM school_class_teacher
            WHERE id_teacher = ?
            AND subject_list = '{}'
            """;

        Connection conn = null;
        try {
            conn = PostgreConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (int subjectId : subjectIds) {
                    pstmt.setInt(1, subjectId);
                    pstmt.setInt(2, teacherId);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            // Deleta registros que ficaram sem nenhuma matéria
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDeleteEmpty)) {
                pstmt.setInt(1, teacherId);
                pstmt.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ignored) {}
            throw new DataException("Erro ao remover matérias do professor.", e);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException ignored) {}
        }
    }
}
