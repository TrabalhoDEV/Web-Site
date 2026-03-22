package com.example.schoolservlet.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.*;

import com.example.schoolservlet.daos.interfaces.GenericDAO;
import com.example.schoolservlet.daos.interfaces.IStudentSubjectDAO;
import com.example.schoolservlet.exceptions.*;
import com.example.schoolservlet.models.Student;
import com.example.schoolservlet.models.StudentSubject;
import com.example.schoolservlet.models.Subject;
import com.example.schoolservlet.utils.Constants;
import com.example.schoolservlet.utils.InputValidation;
import com.example.schoolservlet.utils.PostgreConnection;
import com.example.schoolservlet.utils.enums.StudentStatusEnum;
import com.example.schoolservlet.utils.records.StudentsPerformance;
import com.example.schoolservlet.utils.records.StudentsPerformanceCount;
import com.example.schoolservlet.utils.records.TeacherPendency;


public class StudentSubjectDAO implements GenericDAO<StudentSubject>, IStudentSubjectDAO {

    /**
     * Retrieves a paginated collection of StudentSubject entries from the database.
     * Each entry includes its associated Student and Subject objects with relevant details.
     *
     * @param skip the number of records to skip (used as the OFFSET in the SQL query)
     * @param take the maximum number of records to retrieve (used as the LIMIT in the SQL query)
     * @return a map where the key is the StudentSubject ID and the value is the corresponding StudentSubject object
     * @throws DataException if any SQL exception occurs while accessing the database
     */
    @Override
    public Map<Integer, StudentSubject> findMany(int skip, int take) throws DataException {
        Map<Integer, StudentSubject> studentsSubjects = new HashMap<>();

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT " +
                     "ss.id, " +
                     "ss.obs, " +
                     "ss.grade1, " +
                     "ss.grade2, " +
                     "st.id AS id_student, " +
                     "st.name AS student_name, " +
                     "st.cpf AS student_cpf, " +
                     "st.email AS student_email, " +
                     "sb.id AS id_subject, " +
                     "sb.name AS subject_name, " +
                     "sb.deadline AS subject_deadline " +
                     "FROM student_subject ss JOIN student st ON st.id = ss.id_student JOIN subject sb " +
                     "ON sb.id = ss.id_subject ORDER BY ss.id LIMIT ? OFFSET ?")
        ) {
            pstmt.setInt(1, take < 0 ? 0 : (take > Constants.MAX_TAKE ? Constants.MAX_TAKE : take));
            pstmt.setInt(2, skip < 0 ? 0 : skip);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Student student = new Student();
                student.setId(rs.getInt("id_student"));
                student.setName(rs.getString("student_name"));
                student.setCpf(rs.getString("student_cpf"));
                student.setEmail(rs.getString("student_email"));
                student.setStatus(StudentStatusEnum.ACTIVE);

                Subject subject = new Subject();
                subject.setId(rs.getInt("id_subject"));
                subject.setName(rs.getString("subject_name"));
                subject.setDeadline(rs.getDate("subject_deadline"));

                studentsSubjects.put(rs.getInt("id"), new StudentSubject(
                        rs.getInt("id"),
                        rs.getString("obs"),
                        rs.getBigDecimal("grade1") != null ? rs.getDouble("grade1") : null,
                        rs.getBigDecimal("grade2") != null ? rs.getDouble("grade2") : null,
                        student,
                        subject
                ));
            }

            return studentsSubjects;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao listar student_subject", sqle);
        }
    }

    /**
     * Retrieves a paginated mapping of students and their associated subjects for a specific teacher.
     * Each key in the returned map corresponds to a student ID, and the value is a list of StudentSubject objects.
     * If a student has no subjects assigned under the teacher, a placeholder StudentSubject with null subject is included.
     *
     * @param skip the number of student records to skip (used as the OFFSET in the SQL query)
     * @param take the maximum number of student records to retrieve (used as the LIMIT in the SQL query)
     * @param teacherId the ID of the teacher for which to fetch students and subjects
     * @return a map where each key is a student ID and the value is a list of StudentSubject objects for that student
     * @throws ValidationException if the teacherId is invalid
     * @throws DataException if any SQL exception occurs while accessing the database
     */
    public Map<Integer, List<StudentSubject>> findManyByTeacherId(int skip, int take, int teacherId) throws DataException, ValidationException {
        InputValidation.validateId(teacherId, "id do professor");
        Map<Integer, List<StudentSubject>> studentsMap = new HashMap<>();

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("""
                     SELECT
                         ss.id, ss.obs, ss.grade1, ss.grade2,
                         st.id AS id_student, st.name AS student_name, st.cpf AS student_cpf, st.email AS student_email,
                         sb.id AS id_subject, sb.name AS subject_name, sb.deadline AS subject_deadline
                     FROM student st
                     JOIN school_class sc ON sc.id = st.id_school_class
                     JOIN student_subject ss ON ss.id_student = st.id
                     JOIN subject sb ON sb.id = ss.id_subject
                     WHERE st.status = ?
                     AND EXISTS (
                         SELECT 1 FROM school_class_teacher sct
                         WHERE sct.id_school_class = sc.id
                         AND sct.id_teacher = ?
                         AND sb.id = ANY(sct.subject_list)
                     )
                     ORDER BY st.id
                     LIMIT ? OFFSET ?
                     """)) {

            pstmt.setInt(1, StudentStatusEnum.ACTIVE.ordinal() + 1);
            pstmt.setInt(2, teacherId);
            pstmt.setInt(3, take < 0 ? 0 : (take > Constants.MAX_TAKE ? Constants.MAX_TAKE : take));
            pstmt.setInt(4, skip < 0 ? 0 : skip);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int studentId = rs.getInt("id_student");

                Student student = new Student();
                student.setId(studentId);
                student.setName(rs.getString("student_name"));
                student.setCpf(rs.getString("student_cpf"));
                student.setEmail(rs.getString("student_email"));
                student.setStatus(StudentStatusEnum.ACTIVE);

                StudentSubject studentSubject = null;
                if (rs.getString("subject_name") != null) {
                    Subject subject = new Subject();
                    subject.setId(rs.getInt("id_subject"));
                    subject.setName(rs.getString("subject_name"));
                    subject.setDeadline(rs.getDate("subject_deadline"));

                    studentSubject = new StudentSubject(
                            rs.getInt("id"),
                            rs.getString("obs"),
                            rs.getBigDecimal("grade1") != null ? rs.getDouble("grade1") : null,
                            rs.getBigDecimal("grade2") != null ? rs.getDouble("grade2") : null,
                            student,
                            subject
                    );
                }

                List<StudentSubject> list = studentsMap.computeIfAbsent(studentId, k -> new ArrayList<>());
                if (studentSubject != null) {
                    list.add(studentSubject);
                } else if (list.isEmpty()) {
                    list.add(new StudentSubject(0, null, null, null, student, null));
                }
            }

            return studentsMap;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao listar student_subject", sqle);
        }
    }

    /**
     * Counts the number of active students associated with a specific teacher
     * through class and subject relationships.
     * <p>
     * The method verifies whether the teacher is assigned to teach subjects
     * within a class and counts distinct active students enrolled in those
     * subjects. Only students with an active status are considered in the result.
     * </p>
     *
     * @param teacherId the identifier of the teacher whose related students will be counted
     * @return the total number of distinct active students associated with the teacher
     * @throws DataException if an error occurs while accessing the database
     * @throws ValidationException if the provided teacher identifier is invalid
     */
    public int countByTeacherId(int teacherId) throws DataException, ValidationException {
        InputValidation.validateId(teacherId, "id do professor");
        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("""
                     SELECT COUNT(DISTINCT st.id)
                     FROM student st
                     JOIN school_class sc ON sc.id = st.id_school_class
                     JOIN student_subject ss ON ss.id_student = st.id
                     JOIN subject sb ON sb.id = ss.id_subject
                     WHERE st.status = ?
                     AND EXISTS (
                         SELECT 1 FROM school_class_teacher sct
                         WHERE sct.id_school_class = sc.id
                         AND sct.id_teacher = ?
                         AND sb.id = ANY(sct.subject_list)
                     )
                     """)) {

            pstmt.setInt(1, StudentStatusEnum.ACTIVE.ordinal() + 1);
            pstmt.setInt(2, teacherId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) return rs.getInt(1);
            return 0;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao contar relações do professor", sqle);
        }
    }

        /**
     * Counts the number of subject relationships between a specific student
     * and a teacher within the context of their shared class.
     * <p>
     * The method verifies whether the teacher is assigned to teach subjects
     * in the student's class and counts how many of those subjects are
     * associated with the student. Only valid class-subject-teacher
     * relationships are considered in the result.
     * </p>
     *
     * @param studentId the identifier of the student whose relationships will be counted
     * @param teacherId the identifier of the teacher whose relationships with the student will be evaluated
     * @return the total number of subject relationships between the student and the teacher
     * @throws DataException if an error occurs while accessing the database
     * @throws ValidationException if any of the provided identifiers are invalid
     */
    public int countByStudentIdAndTeacherId(int studentId, int teacherId) throws DataException, ValidationException {
        InputValidation.validateId(studentId, "id do aluno");
        InputValidation.validateId(teacherId, "id do professor");
        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("""
                     SELECT COUNT(ss.id)
                     FROM student st
                     JOIN school_class sc ON sc.id = st.id_school_class
                     JOIN student_subject ss ON ss.id_student = st.id
                     JOIN school_class_subject scs
                         ON scs.id_school_class = sc.id
                         AND scs.id_subject = ss.id_subject
                     WHERE st.id = ?
                     AND EXISTS (
                         SELECT 1
                         FROM school_class_teacher sct
                         WHERE sct.id_school_class = sc.id
                         AND sct.id_teacher = ?
                         AND ss.id_subject = ANY (sct.subject_list)
                     )
                     """)) {

            pstmt.setInt(1, studentId);
            pstmt.setInt(2, teacherId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) return rs.getInt(1);
            return 0;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao contar relações do professor", sqle);
        }
    }

    /**
     * Retrieves a paginated map of subjects associated with a specific student.
     * Each key in the returned map corresponds to a student_subject ID, and the value is the StudentSubject object.
     *
     * @param skip the number of records to skip (used as OFFSET in the SQL query)
     * @param take the maximum number of records to retrieve (used as LIMIT in the SQL query)
     * @param studentId the ID of the student for whom the subjects are fetched
     * @return a map where each key is a student_subject ID and the value is a StudentSubject object
     * @throws ValidationException if the provided studentId is invalid
     * @throws DataException if a database access error occurs
     */
    @Override
    public Map<Integer, StudentSubject> findMany(int skip, int take, int studentId) throws DataException, ValidationException {
        InputValidation.validateId(studentId, "id do aluno");
        Map<Integer, StudentSubject> studentsSubjects = new HashMap<>();

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT " +
                     "ss.id, " +
                     "ss.obs, " +
                     "ss.grade1, " +
                     "ss.grade2, " +
                     "st.id AS student_id, " +
                     "st.name AS student_name, " +
                     "st.cpf AS student_cpf, " +
                     "st.email AS student_email, " +
                     "sb.id AS subject_id, " +
                     "sb.name AS subject_name, " +
                     "sb.deadline AS subject_deadline " +
                     "FROM student_subject ss JOIN student st ON st.id = ss.id_student JOIN subject sb " +
                     "ON sb.id = ss.id_subject " +
                     "WHERE st.id = ? " +
                     "ORDER BY ss.id LIMIT ? OFFSET ?")
        ) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, take < 0 ? 0 : (take > Constants.MAX_TAKE ? Constants.MAX_TAKE : take));
            pstmt.setInt(3, skip < 0 ? 0 : skip);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Student student = new Student();
                student.setId(rs.getInt("student_id"));
                student.setName(rs.getString("student_name"));
                student.setCpf(rs.getString("student_cpf"));
                student.setEmail(rs.getString("student_email"));
                student.setStatus(StudentStatusEnum.ACTIVE);

                Subject subject = new Subject();
                subject.setId(rs.getInt("subject_id"));
                subject.setName(rs.getString("subject_name"));
                subject.setDeadline(rs.getDate("subject_deadline"));

                studentsSubjects.put(rs.getInt("id"),
                        new StudentSubject(
                                rs.getInt("id"),
                                rs.getString("obs"),
                                rs.getObject("grade1") != null ? rs.getDouble("grade1") : null,
                                rs.getObject("grade2") != null ? rs.getDouble("grade2") : null,
                                student,
                                subject
                        )
                );
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao listar matérias por aluno", sqle);
        }

        return studentsSubjects;
    }

    /**
     * Retrieves a paginated map of StudentSubject entries for a specific student that have feedback notes.
     * Only entries where the 'obs' field is not null are included.
     *
     * @param skip the number of records to skip (used as OFFSET in the SQL query)
     * @param take the maximum number of records to retrieve (used as LIMIT in the SQL query)
     * @param studentId the ID of the student whose subjects with feedback are fetched
     * @return a map where each key is a student_subject ID and the value is the corresponding StudentSubject object
     * @throws ValidationException if the provided studentId is invalid
     * @throws DataException if a database access error occurs
     */
    public Map<Integer, StudentSubject> findManyThatHasFeedbacks(int skip, int take, int studentId) throws DataException, ValidationException {
        InputValidation.validateId(studentId, "id do aluno");
        Map<Integer, StudentSubject> studentsSubjects = new HashMap<>();

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT " +
                     "ss.id, " +
                     "ss.obs, " +
                     "ss.grade1, " +
                     "ss.grade2, " +
                     "st.id AS student_id, " +
                     "st.name AS student_name, " +
                     "st.cpf AS student_cpf, " +
                     "st.email AS student_email, " +
                     "sb.id AS subject_id, " +
                     "sb.name AS subject_name, " +
                     "sb.deadline AS subject_deadline " +
                     "FROM student_subject ss JOIN student st ON st.id = ss.id_student JOIN subject sb " +
                     "ON sb.id = ss.id_subject " +
                     "WHERE st.ID = ? AND ss.obs IS NOT NULL " +
                     "ORDER BY ss.id LIMIT ? OFFSET ?")
        ) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, take < 0 ? 0 : (take > Constants.MAX_TAKE ? Constants.MAX_TAKE : take));
            pstmt.setInt(3, skip < 0 ? 0 : skip);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Student student = new Student();
                student.setId(rs.getInt("student_id"));
                student.setName(rs.getString("student_name"));
                student.setCpf(rs.getString("student_cpf"));
                student.setEmail(rs.getString("student_email"));
                student.setStatus(StudentStatusEnum.ACTIVE);

                Subject subject = new Subject();
                subject.setId(rs.getInt("subject_id"));
                subject.setName(rs.getString("subject_name"));
                subject.setDeadline(rs.getDate("subject_deadline"));

                studentsSubjects.put(rs.getInt("id"),
                        new StudentSubject(
                                rs.getInt("id"),
                                rs.getString("obs"),
                                rs.getObject("grade1") != null ? rs.getDouble("grade1") : null,
                                rs.getObject("grade2") != null ? rs.getDouble("grade2") : null,
                                student,
                                subject
                        )
                );
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao listar matérias por aluno", sqle);
        }

        return studentsSubjects;
    }

    /**
     * Retrieves a paginated mapping of a student's subjects with their associated StudentSubject records.
     * Each student ID maps to a list of StudentSubject objects containing the subject details, grades, and observations.
     *
     * @param skip the number of records to skip (used for pagination OFFSET)
     * @param take the maximum number of records to retrieve (used for pagination LIMIT)
     * @param studentId the ID of the student whose subjects are being retrieved
     * @return a map where the key is the student ID and the value is a list of StudentSubject objects for that student
     * @throws ValidationException if the provided studentId is invalid
     * @throws DataException if a database access error occurs while fetching the data
     */
    public Map<Integer, List<StudentSubject>> findManyByStudentId(int skip, int take, int studentId, Integer teacherId) throws DataException, ValidationException {
        InputValidation.validateId(studentId, "id do aluno");
        Map<Integer, List<StudentSubject>> studentsMap = new HashMap<>();

        if (teacherId != null) {
            InputValidation.validateId(teacherId, "id do professor");
            try (Connection conn = PostgreConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("""
                         SELECT
                             ss.id, ss.obs, ss.grade1, ss.grade2,
                             st.id AS id_student, st.name AS student_name, st.cpf AS student_cpf, st.email AS student_email,
                             sb.id AS id_subject, sb.name AS subject_name, sb.deadline AS subject_deadline
                         FROM student st
                         JOIN school_class sc ON sc.id = st.id_school_class
                         JOIN student_subject ss ON ss.id_student = st.id
                         JOIN subject sb ON sb.id = ss.id_subject
                         WHERE st.id = ?
                         AND st.status = ?
                         AND EXISTS (
                             SELECT 1 FROM school_class_teacher sct
                             WHERE sct.id_school_class = sc.id
                             AND sct.id_teacher = ?
                             AND ss.id_subject = ANY(sct.subject_list)
                         )
                         ORDER BY ss.id
                         LIMIT ? OFFSET ?
                         """)) {

                pstmt.setInt(1, studentId);
                pstmt.setInt(2, StudentStatusEnum.ACTIVE.ordinal() + 1);
                pstmt.setInt(3, teacherId);
                pstmt.setInt(4, take < 0 ? 0 : (take > Constants.MAX_TAKE ? Constants.MAX_TAKE : take));
                pstmt.setInt(5, skip < 0 ? 0 : skip);

                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    Student student = new Student();
                    student.setId(rs.getInt("id_student"));
                    student.setName(rs.getString("student_name"));
                    student.setCpf(rs.getString("student_cpf"));
                    student.setEmail(rs.getString("student_email"));
                    student.setStatus(StudentStatusEnum.ACTIVE);

                    Subject subject = new Subject();
                    subject.setId(rs.getInt("id_subject"));
                    subject.setName(rs.getString("subject_name"));
                    subject.setDeadline(rs.getDate("subject_deadline"));

                    StudentSubject studentSubject = new StudentSubject(
                            rs.getInt("id"),
                            rs.getString("obs"),
                            rs.getBigDecimal("grade1") != null ? rs.getDouble("grade1") : null,
                            rs.getBigDecimal("grade2") != null ? rs.getDouble("grade2") : null,
                            student,
                            subject
                    );

                    studentsMap.computeIfAbsent(studentId, k -> new ArrayList<>()).add(studentSubject);
                }

                return studentsMap;
            } catch (SQLException sqle) {
                sqle.printStackTrace();
                throw new DataException("Erro ao listar matérias do aluno", sqle);
            }
        }

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT " +
                             "ss.id, ss.obs, ss.grade1, ss.grade2, " +
                             "st.id AS id_student, st.name AS student_name, st.cpf AS student_cpf, st.email AS student_email, " +
                             "sb.id AS id_subject, sb.name AS subject_name, sb.deadline AS subject_deadline " +
                             "FROM student st " +
                             "JOIN school_class sc ON sc.id = st.id_school_class " +
                             "JOIN student_subject ss ON ss.id_student = st.id " +
                             "JOIN school_class_subject scs ON scs.id_school_class = sc.id AND scs.id_subject = ss.id_subject " +
                             "JOIN subject sb ON sb.id = ss.id_subject " +
                             "WHERE st.id = ? " +
                             "ORDER BY ss.id LIMIT ? OFFSET ?")) {

            pstmt.setInt(1, studentId);
            pstmt.setInt(2, take < 0 ? 0 : take);
            pstmt.setInt(3, skip < 0 ? 0 : skip);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Student student = new Student();
                student.setId(rs.getInt("id_student"));
                student.setName(rs.getString("student_name"));
                student.setCpf(rs.getString("student_cpf"));
                student.setEmail(rs.getString("student_email"));
                student.setStatus(StudentStatusEnum.ACTIVE);

                Subject subject = new Subject();
                subject.setId(rs.getInt("id_subject"));
                subject.setName(rs.getString("subject_name"));
                subject.setDeadline(rs.getDate("subject_deadline"));

                StudentSubject studentSubject = new StudentSubject(
                        rs.getInt("id"),
                        rs.getString("obs"),
                        rs.getBigDecimal("grade1") != null ? rs.getDouble("grade1") : null,
                        rs.getBigDecimal("grade2") != null ? rs.getDouble("grade2") : null,
                        student,
                        subject
                );

                studentsMap.computeIfAbsent(studentId, k -> new ArrayList<>()).add(studentSubject);
            }

            return studentsMap;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao listar matérias do aluno", sqle);
        }
    }

    /**
     * Retrieves a specific StudentSubject record by its unique ID, including the associated Student and Subject details.
     *
     * @param id the unique identifier of the StudentSubject record
     * @return the StudentSubject object containing the student's information, the subject's information, grades, and observations
     * @throws ValidationException if the provided ID is invalid
     * @throws NotFoundException if no StudentSubject record exists for the given ID
     * @throws DataException if a database access error occurs while fetching the data
     */
    @Override
    public StudentSubject findById(int id) throws NotFoundException, DataException, RequiredFieldException, InvalidNumberException{
        InputValidation.validateId(id, "id");

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT " +
                     "ss.id, " +
                     "ss.obs, " +
                     "ss.grade1, " +
                     "ss.grade2, " +
                     "st.id AS id_student, " +
                     "st.name AS student_name, " +
                     "st.cpf AS student_cpf, " +
                     "st.email AS student_email, " +
                     "sb.id AS id_subject, " +
                     "sb.name AS subject_name, " +
                     "sb.deadline AS subject_deadline " +
                     "FROM student_subject ss JOIN student st ON st.id = ss.id_student JOIN subject sb " +
                     "ON sb.id = ss.id_subject WHERE ss.id = ?")) {
            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Student student = new Student();
                student.setId(rs.getInt("id_student"));
                student.setName(rs.getString("student_name"));
                student.setCpf(rs.getString("student_cpf"));
                student.setEmail(rs.getString("student_email"));
                student.setStatus(StudentStatusEnum.ACTIVE);

                Subject subject = new Subject();
                subject.setId(rs.getInt("id_subject"));
                subject.setName(rs.getString("subject_name"));
                subject.setDeadline(rs.getDate("subject_deadline"));

                return new StudentSubject(
                        rs.getInt("id"),
                        rs.getString("obs"),
                        rs.getObject("grade1") != null ? rs.getDouble("grade1") : null,
                        rs.getObject("grade2") != null ? rs.getDouble("grade2") : null,
                        student,
                        subject
                );
            } else throw new NotFoundException("student_subject", "id", String.valueOf(id));
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao buscar relação entre aluno e matéria", sqle);
        }
    }

    /**
     * Retrieves a student-subject relationship by its identifier,
     * ensuring that the specified teacher has access to the record.
     * <p>
     * The method validates the provided identifiers and performs a query
     * that checks whether the teacher is assigned to the subject within
     * the student's class. If the relationship exists and the teacher has
     * permission, the corresponding entity is returned.
     * </p>
     * <p>
     * If the relationship exists but the teacher is not authorized to access it,
     * an {@code UnauthorizedException} is thrown. If the relationship does not
     * exist, a {@code NotFoundException} is thrown.
     * </p>
     *
     * @param id the identifier of the student-subject relationship to be retrieved
     * @param teacherId the identifier of the teacher requesting access to the relationship
     * @return the {@code StudentSubject} entity associated with the given identifier
     * @throws NotFoundException if the relationship does not exist
     * @throws DataException if an error occurs while accessing the database
     * @throws UnauthorizedException if the teacher does not have permission to access the relationship
     * @throws InvalidNumberException if any of the provided identifiers are invalid
     * @throws RequiredFieldException if any required parameter is missing
     */
    public StudentSubject findById(int id, int teacherId) throws NotFoundException, DataException, UnauthorizedException, InvalidNumberException, RequiredFieldException {
        InputValidation.validateId(id, "id");
        InputValidation.validateId(teacherId, "id do professor");

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("""
                 SELECT
                     ss.id,
                     ss.obs,
                     ss.grade1,
                     ss.grade2,
                     st.id    AS id_student,
                     st.name  AS student_name,
                     st.cpf   AS student_cpf,
                     st.email AS student_email,
                     sb.id    AS id_subject,
                     sb.name  AS subject_name,
                     sb.deadline AS subject_deadline
                 FROM student_subject ss
                 JOIN student              st  ON st.id               = ss.id_student
                 JOIN subject              sb  ON sb.id               = ss.id_subject
                 JOIN school_class         sc  ON sc.id               = st.id_school_class
                 JOIN school_class_teacher sct ON sct.id_school_class = sc.id
                                              AND sct.id_teacher      = ?
                                              AND sb.id               = ANY(sct.subject_list)
                 WHERE ss.id = ?
                 """)) {

            pstmt.setInt(1, teacherId);
            pstmt.setInt(2, id);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Student student = new Student();
                student.setId(rs.getInt("id_student"));
                student.setName(rs.getString("student_name"));
                student.setCpf(rs.getString("student_cpf"));
                student.setEmail(rs.getString("student_email"));
                student.setStatus(StudentStatusEnum.ACTIVE);

                Subject subject = new Subject();
                subject.setId(rs.getInt("id_subject"));
                subject.setName(rs.getString("subject_name"));
                subject.setDeadline(rs.getDate("subject_deadline"));

                return new StudentSubject(
                        rs.getInt("id"),
                        rs.getString("obs"),
                        rs.getObject("grade1") != null ? rs.getDouble("grade1") : null,
                        rs.getObject("grade2") != null ? rs.getDouble("grade2") : null,
                        student,
                        subject
                );
            }

            if (findById(id) != null) {
                throw new UnauthorizedException("Professor não tem acesso a essa relação aluno-matéria");
            } else {
                throw new NotFoundException("student_subject", "id", String.valueOf(id));
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao buscar relação entre aluno e matéria", sqle);
        }
    }

    /**
     * Calculates the performance summary of students for a given teacher.
     * The summary includes counts of approved, failed, and pending students based on their grades.
     *
     * @param teacherId the unique identifier of the teacher whose students' performance is being evaluated
     * @return a StudentsPerformanceCount object containing counts of approved, pending, and failed students
     * @throws ValidationException if the provided teacher ID is invalid
     * @throws DataException if a database access error occurs while fetching the performance data
     */
    public StudentsPerformanceCount studentsPerformanceCount(int teacherId) throws DataException, ValidationException {
        InputValidation.validateId(teacherId, "id do professor");

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT " +
                     "SUM(CASE WHEN deadline <= CURRENT_DATE AND grade1 IS NOT NULL AND grade2 IS NOT NULL AND media >= ? THEN 1 ELSE 0 END) AS approved, " +
                     "SUM(CASE WHEN deadline <= CURRENT_DATE AND grade1 IS NOT NULL AND grade2 IS NOT NULL AND media < ? THEN 1 ELSE 0 END) AS failed, " +
                     "SUM(CASE WHEN deadline > CURRENT_DATE OR grade1 IS NULL OR grade2 IS NULL THEN 1 ELSE 0 END) AS pending " +
                     "FROM (" +
                     "    SELECT " +
                     "        sb.deadline, " +
                     "        ss.grade1, " +
                     "        ss.grade2, " +
                     "        CASE " +
                     "            WHEN ss.grade1 IS NOT NULL AND ss.grade2 IS NOT NULL THEN (ss.grade1 + ss.grade2) / 2.0 " +
                     "            WHEN ss.grade1 IS NOT NULL THEN ss.grade1 " +
                     "            WHEN ss.grade2 IS NOT NULL THEN ss.grade2 " +
                     "            ELSE NULL " +
                     "        END AS media " +
                     "    FROM student st " +
                     "    JOIN school_class sc ON sc.id = st.id_school_class " +
                     "    JOIN school_class_teacher sct ON sct.id_school_class = sc.id " +
                     "    JOIN student_subject ss ON ss.id_student = st.id " +
                     "    JOIN school_class_subject scs ON scs.id_school_class = sc.id AND scs.id_subject = ss.id_subject " +
                     "    JOIN subject sb ON sb.id = ss.id_subject " +
                     "    WHERE sct.id_teacher = ? AND st.status = ? " +
                     ") AS sub")) {
            pstmt.setDouble(1, Constants.MIN_GRADE_TO_BE_APPROVAL);
            pstmt.setDouble(2, Constants.MIN_GRADE_TO_BE_APPROVAL);
            pstmt.setInt(3, teacherId);
            pstmt.setInt(4, StudentStatusEnum.ACTIVE.ordinal() + 1);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new StudentsPerformanceCount(rs.getInt("approved"), rs.getInt("pending"), rs.getInt("failed"));
            }
            return new StudentsPerformanceCount(0, 0, 0);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao buscar alunos", sqle);
        }
    }

    /**
     * Retrieves the performance summary of a specific student across all their subjects.
     * The summary includes the number of approved, failed, and pending subjects based on the student's grades.
     *
     * @param studentId the unique identifier of the student whose performance is being evaluated
     * @return a StudentsPerformanceCount object containing counts of approved, pending, and failed subjects
     * @throws ValidationException if the provided student ID is invalid
     * @throws DataException if a database access error occurs while fetching the performance data
     */
    public StudentsPerformanceCount studentPerformanceCount(int studentId) throws DataException, ValidationException {
        InputValidation.validateId(studentId, "id do aluno");

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT " +
                             "SUM(CASE WHEN deadline <= CURRENT_DATE AND grade1 IS NOT NULL AND grade2 IS NOT NULL AND media >= ? THEN 1 ELSE 0 END) AS approved, " +
                             "SUM(CASE WHEN deadline <= CURRENT_DATE AND grade1 IS NOT NULL AND grade2 IS NOT NULL AND media < ? THEN 1 ELSE 0 END) AS failed, " +
                             "SUM(CASE WHEN deadline > CURRENT_DATE OR grade1 IS NULL OR grade2 IS NULL THEN 1 ELSE 0 END) AS pending " +
                             "FROM (" +
                             "    SELECT " +
                             "        sb.deadline, " +
                             "        ss.grade1, " +
                             "        ss.grade2, " +
                             "        CASE " +
                             "            WHEN ss.grade1 IS NOT NULL AND ss.grade2 IS NOT NULL THEN (ss.grade1 + ss.grade2) / 2.0 " +
                             "            WHEN ss.grade1 IS NOT NULL THEN ss.grade1 " +
                             "            WHEN ss.grade2 IS NOT NULL THEN ss.grade2 " +
                             "            ELSE NULL " +
                             "        END AS media " +
                             "    FROM student_subject ss " +
                             "    JOIN student st ON st.id = ss.id_student " +
                             "    JOIN subject sb ON sb.id = ss.id_subject " +
                             "    WHERE ss.id_student = ? AND st.status = ? " +
                             ") AS sub"
             )) {

            pstmt.setDouble(1, Constants.MIN_GRADE_TO_BE_APPROVAL);
            pstmt.setDouble(2, Constants.MIN_GRADE_TO_BE_APPROVAL);
            pstmt.setInt(3, studentId);
            pstmt.setInt(4, StudentStatusEnum.ACTIVE.ordinal() + 1);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new StudentsPerformanceCount(
                        rs.getInt("approved"),
                        rs.getInt("pending"),
                        rs.getInt("failed")
                );
            }

            return new StudentsPerformanceCount(0, 0, 0);

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao buscar desempenho do aluno", sqle);
        }
    }

    /**
     * Retrieves a map of students along with their subjects where the student requires attention from a specific teacher.
     * A student requires attention if their average grade (computed from grade1 and grade2) is below a defined threshold.
     * Only active students are considered, and results are limited by a maximum count constant.
     *
     * @param teacherId the unique identifier of the teacher for whom students requiring attention are being queried
     * @return a map where each key is the StudentSubject ID and the value is the corresponding StudentSubject object
     * @throws ValidationException if the provided teacher ID is invalid
     * @throws DataException if a database access error occurs while fetching the student-subject data
     */
    @Override
    public Map<Integer, StudentSubject> findStudentsThatRequireTeacher(int teacherId) throws DataException, ValidationException {
        InputValidation.validateId(teacherId, "id do professor");
        Map<Integer, StudentSubject> studentsSubjects = new HashMap<>();

        String sql = """
                SELECT sub.*
                FROM (
                    SELECT
                        ss.id,
                        ss.obs,
                        ss.grade1,
                        ss.grade2,
                        st.id           AS id_student,
                        st.name         AS student_name,
                        st.cpf          AS student_cpf,
                        st.email        AS student_email,
                        sb.id           AS id_subject,
                        sb.name         AS subject_name,
                        sb.deadline     AS subject_deadline,
                        CASE
                            WHEN ss.grade1 IS NOT NULL AND ss.grade2 IS NOT NULL
                                THEN (ss.grade1 + ss.grade2) / 2.0
                            WHEN ss.grade1 IS NOT NULL THEN ss.grade1
                            ELSE ss.grade2
                        END AS media
                    FROM student_subject ss
                    JOIN student              st  ON st.id          = ss.id_student
                    JOIN subject              sb  ON sb.id          = ss.id_subject
                    JOIN school_class_teacher sct ON sct.id_school_class = st.id_school_class
                                                 AND sct.id_teacher      = ?
                    JOIN subject_teacher      stt ON stt.id_subject = sb.id
                                                 AND stt.id_teacher  = ?
                    JOIN school_class sc ON sc.id = st.id_school_class
                         WHERE (ss.grade1 IS NOT NULL OR ss.grade2 IS NOT NULL)
                            AND st.status = ?
                    		AND EXISTS (
                    		    SELECT 1 FROM school_class_teacher sct\s
                    			    WHERE sct.id_school_class = sc.id
                    			        AND sct.id_teacher = ?
                    				    AND sb.id = ANY(sct.subject_list)
                    			)
                ) sub
                WHERE sub.media < ?
                ORDER BY sub.media ASC
                LIMIT ?
                """;


        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, teacherId);
            pstmt.setInt(2, teacherId);
            pstmt.setInt(3, StudentStatusEnum.ACTIVE.ordinal() + 1);
            pstmt.setInt(4, teacherId);
            pstmt.setInt(5, Constants.MAX_GRADE_TO_HELP);
            pstmt.setInt(6, Constants.STUDENTS_HELP_TAKE);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Student student = new Student();
                student.setId(rs.getInt("id_student"));
                student.setName(rs.getString("student_name"));
                student.setCpf(rs.getString("student_cpf"));
                student.setEmail(rs.getString("student_email"));
                student.setStatus(StudentStatusEnum.ACTIVE);

                Subject subject = new Subject();
                subject.setId(rs.getInt("id_subject"));
                subject.setName(rs.getString("subject_name"));
                subject.setDeadline(rs.getDate("subject_deadline"));

                studentsSubjects.put(rs.getInt("id"),
                        new StudentSubject(
                                rs.getInt("id"),
                                rs.getString("obs"),
                                rs.getObject("grade1") != null ? rs.getDouble("grade1") : null,
                                rs.getObject("grade2") != null ? rs.getDouble("grade2") : null,
                                student,
                                subject
                        )
                );
            }

            return studentsSubjects;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao buscar alunos que precisam de atenção", sqle);
        }
    }

    /**
     * Calculates the overall performance percentages of students for a specific teacher.
     * The performance is categorized into approved, failed, and pending based on students' average grades.
     * Only students with active status are included in the calculation.
     *
     * @param idTeacher the unique identifier of the teacher for whom student performance is being calculated
     * @return a StudentsPerformance object containing the percentage of approved, pending, and failed students
     * @throws ValidationException if the provided teacher ID is invalid
     * @throws DataException if a database access error occurs while computing student performance
     */
    @Override
    public StudentsPerformance studentsPerformance(int idTeacher) throws ValidationException, DataException {
        InputValidation.validateId(idTeacher, "id do professor");

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("""
                     SELECT
                     COALESCE(ROUND(100.0 * SUM(CASE WHEN deadline <= CURRENT_DATE AND grade1 IS NOT NULL AND grade2 IS NOT NULL AND media >= ? THEN 1 ELSE 0 END) / NULLIF(COUNT(*),0), 0), 0) AS approved,
                     COALESCE(ROUND(100.0 * SUM(CASE WHEN deadline <= CURRENT_DATE AND grade1 IS NOT NULL AND grade2 IS NOT NULL AND media < ? THEN 1 ELSE 0 END) / NULLIF(COUNT(*),0), 0), 0) AS failed,
                     COALESCE(ROUND(100.0 * SUM(CASE WHEN deadline > CURRENT_DATE OR grade1 IS NULL OR grade2 IS NULL THEN 1 ELSE 0 END) / NULLIF(COUNT(*),0), 0), 0) AS pending
                     FROM (
                         SELECT
                             ss.id,
                             sb.deadline,
                             ss.grade1,
                             ss.grade2,
                             CASE
                                 WHEN ss.grade1 IS NOT NULL AND ss.grade2 IS NOT NULL THEN (ss.grade1 + ss.grade2) / 2.0
                                 WHEN ss.grade1 IS NOT NULL THEN ss.grade1
                                 WHEN ss.grade2 IS NOT NULL THEN ss.grade2
                                 ELSE NULL
                             END AS media
                         FROM student_subject ss
                         JOIN student s ON s.id = ss.id_student
                         JOIN subject sb ON sb.id = ss.id_subject
                         JOIN school_class sc ON sc.id = s.id_school_class
                         WHERE s.status = ?
                         AND EXISTS (
                             SELECT 1 FROM school_class_teacher sct
                             WHERE sct.id_school_class = sc.id
                             AND sct.id_teacher = ?
                             AND sb.id = ANY(sct.subject_list)
                         )
                     ) AS sub
                     """)) {

            pstmt.setDouble(1, Constants.MIN_GRADE_TO_BE_APPROVAL);
            pstmt.setDouble(2, Constants.MIN_GRADE_TO_BE_APPROVAL);
            pstmt.setInt(3, StudentStatusEnum.ACTIVE.ordinal() + 1);
            pstmt.setInt(4, idTeacher);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new StudentsPerformance(rs.getInt("approved"), rs.getInt("pending"), rs.getInt("failed"));
            }
            return new StudentsPerformance(0, 0, 0);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao buscar alunos", sqle);
        }
    }

    /**
     * Retrieves a list of pending student subjects that require attention from a specific teacher.
     * A pending is considered when a student has at least one missing grade (grade1 or grade2),
     * and the student is actively enrolled. Each pendency includes information about the student,
     * subject, grades, deadline, and the calculated status relative to the deadline:
     * "Atrasada" (Overdue), "Perto do prazo" (Near deadline), or "Dentro do prazo" (Within deadline).
     *
     * @param idTeacher the unique identifier of the teacher for whom the pendencies are retrieved
     * @return a List of TeacherPendency objects representing students with pending subjects
     * @throws ValidationException if the provided teacher ID is invalid
     * @throws DataException if a database access error occurs while retrieving pendencies
     */
    @Override
    public List<TeacherPendency> teacherPendency(int idTeacher) throws DataException, ValidationException {
        InputValidation.validateId(idTeacher, "id do professor");
        List<TeacherPendency> pendencies = new ArrayList<>();

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT \n" +
                     "st.id AS id_student,\n" +
                     "ss.id AS id,\n" +
                     "ss.grade1,\n" +
                     "ss.grade2,\n" +
                     "st.name AS student_name,\n" +
                     "sb.name AS subject_name,\n" +
                     "sb.deadline,\n" +
                     "CASE\n" +
                     "     WHEN sb.deadline < CURRENT_DATE THEN 'Atrasada'\n" +
                     "     WHEN sb.deadline <= CURRENT_DATE + INTERVAL '7 days' THEN 'Perto do prazo'\n" +
                     "     ELSE 'Dentro do prazo'\n" +
                     "END AS status\n" +
                     "FROM student_subject ss\n" +
                     "JOIN student st ON st.id = ss.id_student\n" +
                     "JOIN subject sb ON sb.id = ss.id_subject\n" +
                     "JOIN school_class sc ON sc.id = st.id_school_class\n" +
                     "WHERE EXISTS ( " +
                     "   SELECT 1 FROM school_class_teacher sct " +
                     "   WHERE sct.id_school_class = sc.id " +
                     "   AND sct.id_teacher = ? " +
                     ") " +
                     "AND EXISTS ( " +
                     "   SELECT 1 FROM subject_teacher stt " +
                     "   WHERE stt.id_subject = sb.id " +
                     "   AND stt.id_teacher = ? " +
                     ") " + "AND st.status = ? " +
                     "AND (ss.grade1 IS NULL OR ss.grade2 IS NULL) " +
                     "AND EXISTS ( " +
                     "  SELECT 1 FROM school_class_teacher sct " +
                     "  WHERE sct.id_school_class = sc.id " +
                     "  AND sct.id_teacher = 2 " +
                     "  AND sb.id = ANY(sct.subject_list) " +
                     ")" +
                     "ORDER BY sb.deadline ASC " +
                     "LIMIT ?")) {
            pstmt.setInt(1, idTeacher);
            pstmt.setInt(2, idTeacher);
            pstmt.setInt(3, StudentStatusEnum.ACTIVE.ordinal() + 1);

            pstmt.setInt(4, Constants.PENDENCIES_TAKE);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                pendencies.add(new TeacherPendency(
                        rs.getInt("id_student"),
                        rs.getInt("id"),
                        rs.getString("student_name"),
                        rs.getString("subject_name"),
                        rs.getObject("grade1") != null ? rs.getDouble("grade1") : null,
                        rs.getObject("grade2") != null ? rs.getDouble("grade2") : null,
                        rs.getDate("deadline"),
                        rs.getString("status")
                ));
            }

            return pendencies;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao buscar pendências", sqle);
        }
    }

    /**
     * Returns the total number of records in the student_subject table, representing
     * all existing associations between students and subjects.
     *
     * @return the total count of student-subject associations, or -1 if no records exist
     * @throws DataException if a database access error occurs while counting the records
     */
    @Override
    public int totalCount() throws DataException {
        try (Connection conn = PostgreConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS totalCount FROM student_subject");

            if (rs.next()) {
                return rs.getInt("totalCount");
            }
            return -1;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao contar relações entre alunos e professores", sqle);
        }
    }

    /**
     * Counts the total number of subject relationships associated with a specific student.
     * <p>
     * The method queries the student-subject relationship table and returns
     * the number of subjects linked to the given student identifier.
     * </p>
     *
     * @param studentId the identifier of the student whose subject relationships will be counted
     * @return the total number of subjects associated with the student, or -1 if no result is obtained
     * @throws DataException if an error occurs while accessing the database
     * @throws ValidationException if the provided student identifier is invalid
     */
    @Override
    public int totalCount(int studentId) throws DataException, ValidationException {
        InputValidation.validateId(studentId, "id do aluno");

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT COUNT(*) AS totalCount FROM student_subject WHERE id_student = ?"
             )) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("totalCount");
            }
            return -1;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao contar relações entre alunos e professores", sqle);
        }
    }

    /**
     * Counts the number of subject records for a specific student that contain observations.
     * <p>
     * The method queries the student-subject relationship table and returns
     * the number of records where the observation field is not null for the
     * given student.
     * </p>
     *
     * @param studentId the identifier of the student whose observation records will be counted
     * @return the total number of subject records with non-null observations, or -1 if no result is obtained
     * @throws DataException if an error occurs while accessing the database
     * @throws ValidationException if the provided student identifier is invalid
     */
    public int countObs(int studentId) throws DataException, ValidationException {
        InputValidation.validateId(studentId, "id do aluno");

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT COUNT(*) AS totalCount FROM student_subject WHERE id_student = ? AND obs IS NOT NULL"
             )) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("totalCount");
            }
            return -1;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao contar relações entre alunos e professores", sqle);
        }
    }

    /**
     * Counts the total number of subjects a specific student is enrolled in,
     * considering only valid school class-subject associations.
     *
     * @param studentId the ID of the student to count subjects for
     * @return the total number of subjects associated with the student, or 0 if none are found
     * @throws ValidationException if the provided studentId is invalid
     * @throws DataException if a database access error occurs while counting the subjects
     */
    public int countByStudentId(int studentId) throws DataException, ValidationException {
        InputValidation.validateId(studentId, "id do aluno");

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT COUNT(*) AS totalCount " +
                             "FROM student st " +
                             "JOIN school_class sc ON sc.id = st.id_school_class " +
                             "JOIN student_subject ss ON ss.id_student = st.id " +
                             "JOIN school_class_subject scs ON scs.id_school_class = sc.id AND scs.id_subject = ss.id_subject " +
                             "JOIN subject sb ON sb.id = ss.id_subject " +
                             "WHERE st.id = ?")) {

            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) return rs.getInt("totalCount");
            return 0;

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao contar matérias do aluno", sqle);
        }
    }

    /**
     * Deletes student_subject records for a specific student
     * restricted to the given set of subject IDs.
     * <p>
     * Used during class transfer to remove only the subjects that
     * are NOT offered in the new class, preserving shared grades.
     * <p>
     * DELETE FROM student_subject
     * WHERE id_student = ?
     * AND id_subject  = ANY(?)
     *
     * @param studentId  the student ID
     * @param subjectIds set of subject IDs to delete
     * @throws DataException if a database error occurs
     */
    public void deleteByStudentAndSubjects(int studentId, Set<Integer> subjectIds) throws DataException {
        String sql = "DELETE FROM student_subject WHERE id_student = ? AND id_subject = ANY(?)";

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            // Convert Set<Integer> → Integer[] → java.sql.Array
            Integer[] ids = subjectIds.toArray(new Integer[0]);
            java.sql.Array sqlArray = conn.createArrayOf("integer", ids);
            ps.setArray(2, sqlArray);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataException("Erro ao remover disciplinas do aluno: " + e.getMessage());
        }
    }

    /**
     * Inserts blank student_subject records for a given student
     * for each subject ID in the provided set.
     * <p>
     * Used during class transfer to add only the subjects that are
     * NEW in the target class (subjects shared with the old class
     * are skipped — their records already exist with grades).
     * <p>
     * INSERT INTO student_subject (id_student, id_subject)
     * VALUES (?, ?) ON CONFLICT DO NOTHING
     * <p>
     * ON CONFLICT DO NOTHING is a safety net: if a record already
     * exists for any reason, the insert is silently skipped so no
     * existing grade is overwritten.
     *
     * @param studentId  the student ID
     * @param subjectIds set of subject IDs to insert
     * @throws DataException if a database error occurs
     */
    public void createManyBySubjectIds(int studentId, Set<Integer> subjectIds) throws DataException {
        String sql = """
                INSERT INTO student_subject (id_student, id_subject)
                VALUES (?, ?)
                ON CONFLICT ON CONSTRAINT uk_student_subject DO NOTHING
                """;

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int subjectId : subjectIds) {
                ps.setInt(1, studentId);
                ps.setInt(2, subjectId);
                ps.addBatch();
            }

            ps.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataException("Erro ao inserir disciplinas para o aluno: " + e.getMessage());
        }
    }

    /**
     * Returns the set of subject IDs for which the student already
     * has a student_subject record (with or without grades).
     * <p>
     * Used by UpdateStudentServlet.ensureMissingSubjects to calculate
     * the delta between class subjects and existing student records.
     * <p>
     * SELECT id_subject FROM student_subject WHERE id_student = ?
     *
     * @param studentId the student ID
     * @return a Set of subject IDs; empty if no records exist yet
     * @throws DataException if a database error occurs
     */
    public Set<Integer> findSubjectIdsByStudent(int studentId) throws DataException {
        String sql = "SELECT id_subject FROM student_subject WHERE id_student = ?";

        Set<Integer> subjectIds = new java.util.HashSet<>();

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    subjectIds.add(rs.getInt("id_subject"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataException("Erro ao buscar disciplinas do aluno: " + e.getMessage());
        }

        return subjectIds;
    }

    /**
     * Creates a new relationship between a student and a subject.
     * <p>
     * The method inserts a record into the student-subject relationship table,
     * optionally including grade values and observations associated with the
     * student's performance in the subject.
     * </p>
     *
     * @param studentSubject the entity containing the student, subject,
     *                       grades, and observation data to be stored
     * @throws DataException if an error occurs while accessing or modifying the database
     * @throws ValidationException if the provided student or subject identifiers are invalid
     */
    @Override
    public void create(StudentSubject studentSubject) throws DataException, ValidationException {
        InputValidation.validateId(studentSubject.getStudent().getId(), "id_student");
        InputValidation.validateId(studentSubject.getSubject().getId(), "id_subject");

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO student_subject " +
                     "(id_student, id_subject, grade1, grade2, obs) VALUES " +
                     "(?, ?, ?, ?, ?)")) {
            pstmt.setInt(1, studentSubject.getStudent().getId());
            pstmt.setInt(2, studentSubject.getSubject().getId());
            if (studentSubject.getGrade1() != null) {
                pstmt.setDouble(3, studentSubject.getGrade1());
            } else {
                pstmt.setNull(3, Types.NUMERIC);
            }
            if (studentSubject.getGrade2() != null) {
                pstmt.setDouble(4, studentSubject.getGrade2());
            } else {
                pstmt.setNull(4, Types.NUMERIC);
            }
            pstmt.setString(5, studentSubject.getObs());

            pstmt.executeUpdate();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao atribuir uma matéria a um usuário");
        }
    }

    /**
     * Assigns multiple subjects to all students of a specific school class in a batch operation.
     *
     * <p>This method validates the school class ID and each subject ID before attempting to insert
     * records into the database. It executes the inserts in a single transaction, rolling back if
     * any error occurs.</p>
     *
     * @param idSchoolClass the ID of the school class whose students will receive the subjects
     * @param addedSubjectIds a list of subject IDs to assign to each student in the class
     * @throws ValidationException if the school class ID or any subject ID is invalid,
     *                             or if a student already has a subject assigned
     * @throws DataException if a database access error occurs while performing the batch insert
     */
    public void createManyBySchoolClass(int idSchoolClass, List<Integer> addedSubjectIds)
            throws DataException, ValidationException {

        InputValidation.validateId(idSchoolClass, "id da turma");

        if (addedSubjectIds == null || addedSubjectIds.isEmpty()) return;

        for (Integer subjectId : addedSubjectIds) {
            if (subjectId == null) throw new ValidationException("ID da matéria não pode ser nulo");

            InputValidation.validateId(subjectId, "id da matéria");
        }

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("""
                     INSERT INTO student_subject (id_student, id_subject)
                     SELECT s.id, ?
                     FROM student s
                     WHERE s.id_school_class = ?
                     """)) {

            conn.setAutoCommit(false);

            try {
                for (Integer idSubject : addedSubjectIds) {
                    pstmt.setInt(1, idSubject);
                    pstmt.setInt(2, idSchoolClass);
                    pstmt.addBatch();
                }

                pstmt.executeBatch();
                conn.commit();

            } catch (SQLException e) {
                conn.rollback();

                if (e.getSQLState().equals("23505")) {
                    throw new ValidationException("Um ou mais alunos já possuem essa matéria associada");
                }
                throw e;
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao adicionar matérias aos alunos da turma", sqle);
        }
    }

    /**
     * Assigns all subjects of a specific school class to a single student.
     *
     * <p>This method fetches the subjects associated with the provided school class and inserts
     * them for the given student in a batch operation within a transaction. If no subjects exist
     * for the class, the method returns without performing any insert.</p>
     *
     * @param studentId the ID of the student to receive the subjects
     * @param schoolClassId the ID of the school class from which subjects are retrieved
     * @throws ValidationException if the student ID or school class ID is invalid
     * @throws DataException if a database access error occurs during retrieval or insertion
     */
    public void createManyByStudentClass(int studentId, int schoolClassId) throws DataException, ValidationException {
        InputValidation.validateId(studentId, "id do aluno");
        InputValidation.validateId(schoolClassId, "id da turma");

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement selectPstmt = conn.prepareStatement(
                     "SELECT id_subject FROM school_class_subject WHERE id_school_class = ?");
             PreparedStatement insertPstmt = conn.prepareStatement(
                     "INSERT INTO student_subject (id_student, id_subject) VALUES (?, ?)")) {

            conn.setAutoCommit(false);

            selectPstmt.setInt(1, schoolClassId);
            ResultSet rs = selectPstmt.executeQuery();

            boolean hasSubjects = false;
            while (rs.next()) {
                hasSubjects = true;
                insertPstmt.setInt(1, studentId);
                insertPstmt.setInt(2, rs.getInt("id_subject"));
                insertPstmt.addBatch();
            }

            if (!hasSubjects) {
                conn.setAutoCommit(true);
                return;
            }

            insertPstmt.executeBatch();
            conn.commit();

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao vincular matérias ao aluno", sqle);
        }
    }

    /**
     * Updates an existing student-subject relationship in the database.
     *
     * <p>This method modifies the student ID, subject ID, grades, and observation for a given
     * student-subject entry. It validates all IDs before attempting the update and uses a
     * prepared statement to perform the operation.</p>
     *
     * @param studentSubject the StudentSubject object containing updated data
     * @throws ValidationException if any ID (student, subject, or student-subject) is invalid
     * @throws NotFoundException if the student-subject entry does not exist in the database
     * @throws DataException if a database access error occurs during the update
     */
    @Override
    public void update(StudentSubject studentSubject) throws NotFoundException, DataException, ValidationException {
        InputValidation.validateId(studentSubject.getId(), "id");
        InputValidation.validateId(studentSubject.getSubject().getId(), "id da matéria");
        InputValidation.validateId(studentSubject.getStudent().getId(), "id do aluno");

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("UPDATE student_subject SET " +
                     "id_student = ?, id_subject = ?, grade1 = ?, grade2 = ?, obs = ? WHERE id = ?")) {
            pstmt.setInt(1, studentSubject.getStudent().getId());
            pstmt.setInt(2, studentSubject.getSubject().getId());
            if (studentSubject.getGrade1() != null) {
                pstmt.setDouble(3, studentSubject.getGrade1());
            } else {
                pstmt.setNull(3, Types.NUMERIC);
            }
            if (studentSubject.getGrade2() != null) {
                pstmt.setDouble(4, studentSubject.getGrade2());
            } else {
                pstmt.setNull(4, Types.NUMERIC);
            }
            pstmt.setString(5, studentSubject.getObs());
            pstmt.setInt(6, studentSubject.getId());

            if (pstmt.executeUpdate() <= 0)
                throw new NotFoundException("matérias por aluno", "id", String.valueOf(studentSubject.getId()));
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao atualizar um dado de uma matéria de um aluno");
        }
    }

    /**
     * Deletes a student-subject relationship from the database by its ID.
     *
     * <p>This method validates the provided ID and attempts to remove the corresponding entry
     * from the student_subject table. If no entry matches the given ID, a NotFoundException
     * is thrown.</p>
     *
     * @param id the ID of the student-subject relationship to be deleted
     * @throws ValidationException if the provided ID is invalid
     * @throws NotFoundException if no student-subject entry exists with the specified ID
     * @throws DataException if a database access error occurs during deletion
     */
    @Override
    public void delete(int id) throws NotFoundException, DataException, ValidationException {
        InputValidation.validateId(id, "id");

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM student_subject WHERE id = ?")) {
            pstmt.setInt(1, id);

            if (pstmt.executeUpdate() <= 0) throw new NotFoundException("matérias por aluno", "id", String.valueOf(id));
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao deletar matéria de um aluno");
        }
    }

    /**
     * Deletes multiple student-subject relationships for all students in a given school class.
     *
     * <p>This method removes the specified subjects from all students who belong to the provided
     * school class. The operation is performed in a batch within a single transaction to ensure
     * atomicity. If the provided list of subject IDs is null or empty, the method exits without
     * performing any deletion.</p>
     *
     * @param idSchoolClass the ID of the school class whose students will have subjects removed
     * @param removedSubjectIds a list of subject IDs to be removed from the students
     * @throws ValidationException if the provided school class ID or any subject ID is invalid
     * @throws DataException if a database access error occurs during deletion
     */
    public void deleteManyBySchoolClass(int idSchoolClass, List<Integer> removedSubjectIds)
            throws DataException, ValidationException {
        InputValidation.validateId(idSchoolClass, "id da turma");
        if (removedSubjectIds == null || removedSubjectIds.isEmpty()) return;

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("""
                     DELETE FROM student_subject
                     WHERE id_student IN (
                         SELECT id FROM student WHERE id_school_class = ?
                     )
                     AND id_subject = ?
                     """)) {

            conn.setAutoCommit(false);

            try {
                for (Integer idSubject : removedSubjectIds) {
                    pstmt.setInt(1, idSchoolClass);
                    pstmt.setInt(2, idSubject);
                    pstmt.addBatch();
                }

                pstmt.executeBatch();
                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao remover matérias dos alunos da turma", sqle);
        }
    }
}

