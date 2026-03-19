package com.example.schoolservlet.daos;

import com.example.schoolservlet.daos.interfaces.GenericDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.NotFoundException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.Subject;
import com.example.schoolservlet.models.SubjectTeacher;
import com.example.schoolservlet.models.Teacher;
import com.example.schoolservlet.utils.Constants;
import com.example.schoolservlet.utils.InputValidation;
import com.example.schoolservlet.utils.PostgreConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SubjectTeacherDAO implements GenericDAO<SubjectTeacher> {

    /**
     * Retrieves a paginated list of subject-teacher associations from the database.
     *
     * <p>The method returns a map where the key is the association ID and the value
     * is the corresponding SubjectTeacher object containing both subject and teacher details.
     * Pagination is controlled by the `skip` (offset) and `take` (limit) parameters,
     * with `take` constrained by a maximum constant.</p>
     *
     * @param skip the number of records to skip for pagination
     * @param take the maximum number of records to retrieve
     * @return a map of association IDs to SubjectTeacher objects
     * @throws DataException if a database access error occurs during retrieval
     */
    @Override
    public Map<Integer, SubjectTeacher> findMany(int skip, int take) throws DataException {
        Map<Integer, SubjectTeacher> subjectTeacherMap = new HashMap<>();

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT st.id, " +
                             "st.id_subject, " +
                             "st.id_teacher, " +
                             "t.name AS teacher_name, " +
                             "t.email AS teacher_email, " +
                             "s.name AS subject_name, " +
                             "s.deadline " +
                             "FROM subject_teacher st " +
                             "JOIN teacher t ON t.id = st.id_teacher " +
                             "JOIN subject s ON s.id = st.id_subject " +
                             "ORDER BY st.id LIMIT ? OFFSET ?"
             )){
            pstmt.setInt(1, take < 0 ? 0 : (take > Constants.MAX_TAKE ? Constants.MAX_TAKE : take));
            pstmt.setInt(2, skip < 0 ? 0 : skip);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()){
                Teacher teacher = new Teacher();
                teacher.setId(rs.getInt("id_teacher"));
                teacher.setName(rs.getString("teacher_name"));
                teacher.setEmail(rs.getString("teacher_email"));

                Subject subject = new Subject();
                subject.setId(rs.getInt("id_subject"));
                subject.setName(rs.getString("subject_name"));
                subject.setDeadline(rs.getDate("deadline"));
                subjectTeacherMap.put(rs.getInt("id"), new SubjectTeacher(
                        rs.getInt("id"),
                        subject,
                        teacher
                ));
            }

        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao listar relações entre professores e matérias", sqle);
        }

        return subjectTeacherMap;
    }

    /**
     * Retrieves a specific subject-teacher association by its unique ID.
     *
     * <p>This method validates the provided ID and queries the database to fetch
     * the corresponding SubjectTeacher object, which includes both the subject
     * and teacher details. If no record is found, a NotFoundException is thrown.</p>
     *
     * @param id the unique identifier of the subject-teacher association
     * @return the SubjectTeacher object corresponding to the given ID
     * @throws DataException if a database access error occurs during retrieval
     * @throws ValidationException if the provided ID fails validation
     * @throws NotFoundException if no association exists with the given ID
     */
    @Override
    public SubjectTeacher findById(int id) throws DataException, ValidationException, NotFoundException{
        InputValidation.validateId(id, "id");

        try(
                Connection conn = PostgreConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(
                        "SELECT st.id, " +
                                "st.id_subject, " +
                                "st.id_teacher, " +
                                "t.name AS teacher_name, " +
                                "t.email AS teacher_email, " +
                                "s.name AS subject_name, " +
                                "s.deadline " +
                                "FROM subject_teacher st " +
                                "JOIN teacher t ON t.id = st.id_teacher " +
                                "JOIN subject s ON s.id = st.id_subject " +
                                "WHERE st.id = ?"
                )
        ) {
            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                Teacher teacher = new Teacher();
                teacher.setId(rs.getInt("id_teacher"));
                teacher.setName(rs.getString("teacher_name"));
                teacher.setEmail(rs.getString("teacher_email"));

                Subject subject = new Subject();
                subject.setId(rs.getInt("id_subject"));
                subject.setName(rs.getString("subject_name"));
                subject.setDeadline(rs.getDate("deadline"));
                return new SubjectTeacher(
                        rs.getInt("id"),
                        subject,
                        teacher
                );
            } else throw new NotFoundException("subject_teacher", "id", String.valueOf(id));
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao buscar subject_teacher pelo id", sqle);
        }
    }

    /**
     * Retrieves the list of teachers associated with a specific subject.
     * <p>
     * The method queries the subject-teacher relationship table and returns
     * all teachers linked to the specified subject. Results are ordered
     * alphabetically by the teacher's name.
     * </p>
     *
     * @param subjectId the identifier of the subject whose teachers will be retrieved
     * @return a list of teacher entities associated with the specified subject
     * @throws DataException if an error occurs while accessing the database
     */
    public List<Teacher> findBySubject(int subjectId) throws DataException {
        String sql = """
            SELECT t.id, t.name FROM teacher t
            INNER JOIN subject_teacher st ON st.id_teacher = t.id
            WHERE st.id_subject = ?
            ORDER BY t.name
            """;

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, subjectId);
            ResultSet rs = stmt.executeQuery();

            List<Teacher> list = new ArrayList<>();
            while (rs.next()) {
                Teacher t = new Teacher();
                t.setId(rs.getInt("id"));
                t.setName(rs.getString("name"));
                list.add(t);
            }
            return list;

        } catch (SQLException e) {
            throw new DataException("Erro ao buscar professores da matéria.", e);
        }
    }

    /**
     * Creates an association between a subject and a teacher in the database.
     *
     * <p>This method validates both the teacher ID and subject ID before inserting
     * a new record into the subject_teacher table to establish the relationship.</p>
     *
     * @param subjectTeacher an object containing the subject and teacher to be associated
     * @throws DataException if a database access error occurs during insertion
     * @throws ValidationException if either the teacher ID or subject ID fails validation
     */
    @Override
    public void create(SubjectTeacher subjectTeacher) throws DataException, ValidationException {
        InputValidation.validateId(subjectTeacher.getTeacher().getId(), "id_teacher");
        InputValidation.validateId(subjectTeacher.getSubject().getId(), "id_subject");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT INTO subject_teacher (id_subject, id_teacher) VALUES (?, ?)"
            )){

            pstmt.setInt(1, subjectTeacher.getSubject().getId());
            pstmt.setInt(2, subjectTeacher.getTeacher().getId());

            pstmt.executeUpdate();
        } catch(SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao criar relação de matéria com professor", sqle);
        }
    }

    /**
     * Creates multiple associations between subjects and teachers in the database.
     *
     * <p>This method performs batch insertion for a list of SubjectTeacher objects
     * within a single transaction. Auto-commit is disabled to ensure that either
     * all associations are inserted successfully or none in case of an error.</p>
     *
     * @param subjectTeachers a list of SubjectTeacher objects representing the subject-teacher associations to be created
     * @throws DataException if a database access error occurs during the batch insertion
     */
    public void createMany(List<SubjectTeacher> subjectTeachers) throws DataException {
        String sql = "INSERT INTO subject_teacher (id_subject, id_teacher) VALUES (?, ?)";
        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            for (SubjectTeacher st : subjectTeachers) {
                ps.setInt(1, st.getSubject().getId());
                ps.setInt(2, st.getTeacher().getId());
                ps.addBatch();
            }

            ps.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataException("Erro ao inserir múltiplos registros de relacionamentos entre matéria e professor");
        }
    }

    /**
     * Updates an existing subject-teacher association in the database.
     *
     * <p>This method validates the IDs of the association, subject, and teacher
     * before executing the update. If no record is affected, a NotFoundException
     * is thrown indicating that the specified association does not exist.</p>
     *
     * @param subjectTeacher the SubjectTeacher object containing updated subject and teacher information
     * @throws DataException if a database access error occurs during the update
     * @throws ValidationException if any of the IDs fail validation
     * @throws NotFoundException if the association with the given ID does not exist
     */
    @Override
    public void update(SubjectTeacher subjectTeacher) throws DataException, NotFoundException, ValidationException {
        InputValidation.validateId(subjectTeacher.getId(), "id");
        InputValidation.validateId(subjectTeacher.getTeacher().getId(), "id_teacher");
        InputValidation.validateId(subjectTeacher.getSubject().getId(), "id_subject");

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE subject_teacher SET id_subject = ?, id_teacher = ? WHERE id = ?"
             )) {
            pstmt.setInt(1, subjectTeacher.getSubject().getId());
            pstmt.setInt(2, subjectTeacher.getTeacher().getId());
            pstmt.setInt(3, subjectTeacher.getId());

            if (pstmt.executeUpdate() <= 0) throw new NotFoundException("subject_teacher", "id", String.valueOf(subjectTeacher.getId()));
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao atualizar subject_teacher", sqle);
        }
    }

    /**
     * Updates all subject-teacher associations for a specific teacher.
     *
     * <p>This method replaces the old teacher ID with a new teacher ID in all
     * associations within the subject_teacher table. Validation is performed
     * on both IDs, and if no records are affected, a NotFoundException is thrown.</p>
     *
     * @param oldTeacherId the current teacher ID to be replaced
     * @param newTeacherId the new teacher ID to assign to the associations
     * @throws DataException if a database access error occurs during the update
     * @throws ValidationException if either teacher ID fails validation
     * @throws NotFoundException if no associations exist for the old teacher ID
     */
    public void updateTeacher(int oldTeacherId, int newTeacherId) throws DataException, NotFoundException, ValidationException {
        InputValidation.validateId(oldTeacherId, "id");
        InputValidation.validateId(newTeacherId, "id");

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE subject_teacher SET id_teacher = ? WHERE id_teacher = ?"
             )) {
            pstmt.setInt(1, newTeacherId);
            pstmt.setInt(2, oldTeacherId);

            if (pstmt.executeUpdate() <= 0) throw new NotFoundException("subject_teacher", "id", String.valueOf(oldTeacherId));
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao atualizar relações entre matéria e professor", sqle);
        }
    }

    /**
     * Deletes a specific subject-teacher association from the database by its ID.
     *
     * <p>This method validates the provided ID and attempts to remove the corresponding
     * record from the subject_teacher table. If no record is deleted, a NotFoundException
     * is thrown to indicate that the specified association does not exist.</p>
     *
     * @param id the unique identifier of the subject-teacher association to delete
     * @throws DataException if a database access error occurs during deletion
     * @throws ValidationException if the provided ID fails validation
     * @throws NotFoundException if no association exists with the given ID
     */
    @Override
    public void delete(int id) throws DataException, NotFoundException, ValidationException {
        InputValidation.validateId(id, "id");

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "DELETE FROM subject_teacher WHERE id = ?"
             )) {
            pstmt.setInt(1, id);

            if (pstmt.executeUpdate() <= 0) throw new NotFoundException("subject_teacher", "id", String.valueOf(id));
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao deletar subject_teacher", sqle);
        }
    }

    /**
     * Deletes multiple subject-teacher associations for a specific teacher.
     *
     * <p>This method removes all associations between the given teacher ID and a set
     * of subject IDs using batch execution. If the subject ID set is null or empty,
     * the method exits without performing any deletion.</p>
     *
     * @param teacherId the ID of the teacher whose associations are to be deleted
     * @param subjectIds a set of subject IDs to remove from the teacher's associations
     * @throws DataException if a database access error occurs during deletion
     */
    public void deleteManyByTeacherAndSubjects(int teacherId, Set<Integer> subjectIds)
            throws DataException {

        if (subjectIds == null || subjectIds.isEmpty()) return;

        String sql = "DELETE FROM subject_teacher WHERE id_teacher = ? AND id_subject = ?";

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (Integer subjectId : subjectIds) {
                ps.setInt(1, teacherId);
                ps.setInt(2, subjectId);
                ps.addBatch();
            }

            ps.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataException("Erro ao remover múltiplos vínculos entre matéria e professor");
        }
    }

    /**
     * Retrieves the total number of subject-teacher associations in the database.
     *
     * <p>This method executes a COUNT query on the subject_teacher table and returns
     * the total number of records. If an unexpected result occurs, it returns -1.</p>
     *
     * @return the total count of subject-teacher associations, or -1 if unavailable
     * @throws DataException if a database access error occurs during the count query
     */
    @Override
    public int totalCount() throws DataException {
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) AS totalCount FROM subject_teacher");
            ResultSet rs = pstmt.executeQuery()){

            if (rs.next()){
                return rs.getInt("totalCount");
            }
            return -1;
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao contar relações entre professores e matérias", sqle);
        }
    }
}
