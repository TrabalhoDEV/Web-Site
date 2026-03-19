package com.example.schoolservlet.daos;

import com.example.schoolservlet.daos.interfaces.GenericDAO;
import com.example.schoolservlet.exceptions.*;
import com.example.schoolservlet.models.Subject;
import com.example.schoolservlet.utils.Constants;
import com.example.schoolservlet.utils.InputValidation;
import com.example.schoolservlet.utils.PostgreConnection;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class SubjectDAO implements GenericDAO<Subject> {

    /**
     * Retrieves a paginated map of subjects from the database.
     *
     * <p>This method fetches a limited number of subjects ordered by their ID, applying
     * pagination using the provided skip (offset) and take (limit) parameters. The result
     * is returned as a map where the key is the subject ID and the value is the corresponding
     * Subject object.</p>
     *
     * @param skip the number of records to skip (offset) for pagination
     * @param take the maximum number of records to retrieve (limit)
     * @return a map of subjects keyed by their ID
     * @throws DataException if a database access error occurs
     */
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

    /**
     * Retrieves a list of subjects from the database.
     *
     * <p>This method fetches subjects ordered by their ID and returns them as a List of
     * Subject objects. Each Subject contains the ID and name retrieved from the database.</p>
     *
     * @return a list of Subject objects
     * @throws DataException if a database access error occurs
     */
    public List<Subject> findMany() throws DataException{
        List<Subject> subjects = new ArrayList<>();
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT id, name FROM subject ORDER BY id LIMIT ? OFFSET ?")){

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()){
                Subject subject = new Subject();
                subject.setId(rs.getInt("id"));
                subject.setName(rs.getString("name"));

                subjects.add(subject);
            }

            return subjects;
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao listar matérias", sqle);
        }
    }

    /**
     * Retrieves all subjects from the database.
     *
     * <p>This method fetches every record from the 'subject' table, ordered by ID,
     * and returns them as a list of Subject objects. Each Subject includes the ID,
     * name, and deadline.</p>
     *
     * @return a list of all Subject objects
     * @throws DataException if a database access error occurs
     */
    public List<Subject> findAll() throws DataException {
        List<Subject> subjects = new ArrayList<>();

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT * FROM subject ORDER BY id")) {

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                subjects.add(new Subject(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDate("deadline")
                ));
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao listar matérias", sqle);
        }

        return subjects;
    }

    /**
     * Retrieves a Subject from the database by its ID.
     *
     * <p>This method validates the provided ID and queries the 'subject' table
     * for a matching record. If a subject with the given ID exists, it returns
     * a Subject object containing the ID, name, and deadline.</p>
     *
     * @param id the unique identifier of the subject to retrieve
     * @return the Subject object corresponding to the given ID
     * @throws ValidationException if the provided ID is invalid
     * @throws NotFoundException if no subject with the given ID exists
     * @throws DataException if a database access error occurs
     */
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

    /**
     * Retrieves a list of subjects taught by a specific teacher.
     *
     * <p>This method queries the 'subject' table joined with 'subject_teacher'
     * to find all subjects associated with the given teacher ID. Each resulting
     * row is mapped to a Subject object containing the ID, name, and deadline.</p>
     *
     * @param teacherId the unique identifier of the teacher
     * @return a list of Subject objects taught by the specified teacher
     * @throws DataException if a database access error occurs
     */
    public List<Subject> findByTeacherId(int teacherId) throws DataException {
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT s.id, s.name, s.deadline " +
                "FROM subject s " +
                "JOIN subject_teacher st ON s.id = st.id_subject " +
                "WHERE st.id_teacher = ?";

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Subject subject = new Subject(rs.getInt("id"),rs.getString("name"),rs.getDate("deadline"));
                    subjects.add(subject);
                }
            }

        } catch (SQLException e) {
            throw new DataException("Erro ao buscar matérias do professor", e);
        }

        return subjects;
    }

    /**
     * Retrieves a list of subjects assigned to a specific school class.
     *
     * <p>This method queries the 'subject' table joined with 'school_class_subject'
     * to find all subjects associated with the provided school class ID.
     * The results are ordered alphabetically by subject name and mapped to Subject objects,
     * containing the ID, name, and deadline.</p>
     *
     * @param schoolClassId the unique identifier of the school class
     * @return a list of Subject objects associated with the specified school class
     * @throws DataException if a database access error occurs
     */
    public List<Subject> findBySchoolClassId(int schoolClassId)
            throws DataException {

        String sql =
                "SELECT s.id, s.name, s.deadline " +
                        "FROM subject s " +
                        "INNER JOIN school_class_subject scs ON s.id = scs.id_subject " +
                        "WHERE scs.id_school_class = ? " +
                        "ORDER BY s.name";

        List<Subject> subjects = new ArrayList<>();

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, schoolClassId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Subject subject = new Subject();
                subject.setId(rs.getInt("id"));
                subject.setName(rs.getString("name"));
                subject.setDeadline(rs.getDate("deadline")); // Date, não LocalDate
                subjects.add(subject);
            }

            return subjects;

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao buscar matérias da turma", sqle);
        }
    }

    /**
     * Retrieves all subject IDs from the 'subject' table.
     *
     * <p>This method executes a simple query to collect every ID from the subject table
     * and returns them as a list of integers.</p>
     *
     * @return a list of integers representing the IDs of all subjects
     * @throws DataException if a database access error occurs
     */
    public List<Integer> findAllIds() throws DataException {
        String sql = "SELECT id FROM subject";
        List<Integer> ids = new ArrayList<>();

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ids.add(rs.getInt("id"));
            }

        } catch (SQLException e) {
            throw new DataException("Erro ao buscar IDs de matérias.");
        }

        return ids;
    }

    /**
     * Retrieves a paginated collection of subjects filtered by name.
     * <p>
     * The method performs a case-insensitive search on the subject name
     * and returns the matching records ordered by their identifier.
     * Pagination is supported through the skip and take parameters.
     * </p>
     *
     * @param skip the number of records to skip before collecting the results
     * @param take the maximum number of records to return
     * @param nameFilter the filter used to search subjects by name
     * @return a map containing subject entities indexed by their identifier
     * @throws DataException if an error occurs while accessing the database
     * @throws ValidationException if the provided subject name filter is invalid
     */
    public Map<Integer, Subject> findMany(int skip, int take, String nameFilter) throws DataException, ValidationException {
        InputValidation.validateSubjectName(nameFilter);
        Map<Integer, Subject> subjects = new HashMap<>();

        String sql = "SELECT * FROM subject " +
                "WHERE name ILIKE ? " +
                "ORDER BY id LIMIT ? OFFSET ?";

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int paramIndex = 1;

            pstmt.setString(paramIndex++, "%" + nameFilter.trim() + "%");
            pstmt.setInt(paramIndex++, take < 0 ? 0 : Math.min(take, Constants.MAX_TAKE));
            pstmt.setInt(paramIndex,   skip < 0 ? 0 : skip);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                subjects.put(rs.getInt("id"), new Subject(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDate("deadline")
                ));
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao listar matérias", sqle);
        }

        return subjects;
    }

    /**
     * Counts the total number of subjects that match the provided name filter.
     * <p>
     * The method performs a case-insensitive search on the subject name and
     * returns the number of records that satisfy the filter criteria.
     * </p>
     *
     * @param nameFilter the filter used to search subjects by name
     * @return the total number of subjects that match the filter
     * @throws DataException if an error occurs while accessing the database
     * @throws ValidationException if the provided subject name filter is invalid
     */
    public int count(String nameFilter) throws DataException, ValidationException {
        InputValidation.validateSubjectName(nameFilter);

        String sql = "SELECT COUNT(*) FROM subject " +
                "WHERE name ILIKE ?";

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + nameFilter.trim() + "%");

            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao contar matérias", sqle);
        }
    }

    /**
     * Retrieves the total number of subjects in the 'subject' table.
     *
     * <p>This method executes a COUNT query on the subject table and returns
     * the total number of records found. If no records exist, it returns -1.</p>
     *
     * @return the total number of subjects, or -1 if none are found
     * @throws DataException if a database access error occurs
     */
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

    /**
     * Inserts a new subject into the 'subject' table.
     *
     * <p>Before insertion, this method validates that the subject's name and deadline
     * are not null and that the deadline is after the current date. If any validation
     * fails, the corresponding exception is thrown.</p>
     *
     * @param subject the Subject object to be created
     * @throws DataException if a database access error occurs
     * @throws RequiredFieldException if the subject's name or deadline is null/empty
     * @throws InvalidDateException if the subject's deadline is before the current date
     */
    @Override
    public void create(Subject subject) throws DataException, RequiredFieldException, InvalidDateException{
        if (subject.getName() == null || subject.getName().isEmpty()) throw new RequiredFieldException("nome");
        if (subject.getDeadline() == null) throw new RequiredFieldException("data final");
        if (subject.getDeadline().before(new java.util.Date())) throw new InvalidDateException("data final", "Data final deve ser depois da data de hoje");

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

    /**
     * Updates an existing subject in the 'subject' table.
     *
     * <p>Validations performed before updating:</p>
     * <ul>
     *     <li>Subject name must not be null or empty.</li>
     *     <li>Subject ID must be a valid positive integer.</li>
     *     <li>Deadline must not be null and must be after the current date.</li>
     * </ul>
     *
     * <p>If the subject with the given ID does not exist, a NotFoundException is thrown.</p>
     *
     * @param subject the Subject object containing updated information
     * @throws NotFoundException if no subject with the given ID exists
     * @throws DataException if a database access error occurs
     * @throws ValidationException if the subject ID is invalid
     * @throws RequiredFieldException if the name or deadline is missing
     * @throws InvalidDateException if the deadline is before the current date
     */
    @Override
    public void update(Subject subject) throws NotFoundException, DataException, ValidationException {
        if (subject.getName() == null || subject.getName().isEmpty()) throw new RequiredFieldException("nome");
        InputValidation.validateId(subject.getId(), "id");
        if (subject.getDeadline() == null) throw new RequiredFieldException("data final");
        if (subject.getDeadline().before(new java.util.Date())) throw new InvalidDateException("data final", "Data final deve ser depois da data de hoje");

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

    /**
     * Deletes a subject from the database after performing validation checks.
     *
     * <p>The deletion is conditional based on the following rules:
     * - The subject has no enrolled students.
     * - All enrolled students have no grades.
     * - The subject's deadline has passed and all enrolled students have grades.
     *
     * If these conditions are not met, a ValidationException is thrown with
     * an appropriate message explaining why the subject cannot be deleted.</p>
     *
     * <p>The method also retrieves basic subject information and enrollment statistics
     * before attempting deletion to determine eligibility.</p>
     *
     * @param subjectId the unique identifier of the subject to be deleted
     * @throws DataException if a database access error occurs during retrieval or deletion
     * @throws ValidationException if the subject cannot be deleted due to enrollment or grading constraints
     * @throws NotFoundException if no subject exists with the provided ID
     */
    @Override
    public void delete(int subjectId) throws DataException, ValidationException, NotFoundException {
        InputValidation.validateId(subjectId, "id da matéria");

        try (Connection conn = PostgreConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement("""
            SELECT 
                s.id,
                s.name,
                s.deadline,
                COUNT(ss.id) as total_enrollments,
                COUNT(CASE WHEN ss.grade1 IS NULL AND ss.grade2 IS NULL THEN 1 END) as students_without_grades,
                COUNT(CASE WHEN ss.grade1 IS NOT NULL OR ss.grade2 IS NOT NULL THEN 1 END) as students_with_grades
            FROM subject s
            LEFT JOIN student_subject ss ON s.id = ss.id_subject
            WHERE s.id = ?
            GROUP BY s.id, s.name, s.deadline
            """)) {
            pstmt.setInt(1, subjectId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    throw new NotFoundException("matéria", "id", String.valueOf(subjectId));
                }

                String name = rs.getString("name");
                Date deadline = rs.getDate("deadline");
                int totalEnrollments = rs.getInt("total_enrollments");
                int studentsWithoutGrades = rs.getInt("students_without_grades");
                int studentsWithGrades = rs.getInt("students_with_grades");

                boolean canDelete = false;
                String reason = "";

                if (totalEnrollments == 0) {
                    canDelete = true;
                } else if (studentsWithoutGrades == totalEnrollments) {
                    canDelete = true;
                } else if (deadline.before(new java.util.Date()) && studentsWithGrades == totalEnrollments) {
                    canDelete = true;
                } else {
                    int studentsWithPartialGrades = totalEnrollments - studentsWithoutGrades - studentsWithGrades;

                    if (deadline.after(new java.util.Date()) || deadline.equals(new java.util.Date())) {
                        reason = String.format(
                                "Não é possível deletar a matéria '%s'. " +
                                        "O prazo (%s) ainda não expirou e existem alunos com notas.",
                                name, deadline
                        );
                    } else {
                        reason = String.format(
                                "Não é possível deletar a matéria '%s'. " +
                                        "Existem %d alunos sem nenhuma nota registrada.",
                                name, studentsWithoutGrades
                        );
                    }
                }

                if (!canDelete) {
                    throw new ValidationException(reason);
                }

                String deleteSql = "DELETE FROM subject WHERE id = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                    deleteStmt.setInt(1, subjectId);
                    deleteStmt.executeUpdate();
                }
            }
        } catch (SQLException sqle){
            throw new DataException("Erro ao deletar matéria", sqle);
        }
    }

    /**
     * Checks if there are any students enrolled in a specific subject.
     *
     * <p>This method validates the subject ID and queries the database to determine
     * whether at least one student is associated with the given subject.</p>
     *
     * @param subjectId the unique identifier of the subject to check for student enrollment
     * @return true if there is at least one student enrolled in the subject; false otherwise
     * @throws DataException if a database access error occurs during the check
     * @throws ValidationException if the subjectId fails validation
     */
    public boolean hasStudentsById(int subjectId) throws DataException, ValidationException {
        InputValidation.validateId(subjectId, "id da matéria");

        String sql = "SELECT EXISTS (SELECT 1 FROM student_subject WHERE id_subject = ?) AS has_students";

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, subjectId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getBoolean("has_students");
            }

            return false;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao validar alunos da matéria", sqle);
        }
    }
}