package com.example.schoolservlet.daos;

import com.example.schoolservlet.daos.interfaces.GenericDAO;
import com.example.schoolservlet.daos.interfaces.IStudentSubjectDAO;
import com.example.schoolservlet.exceptions.*;
import com.example.schoolservlet.models.Student;
import com.example.schoolservlet.models.StudentSubject;
import com.example.schoolservlet.models.Subject;
import com.example.schoolservlet.utils.Constants;
import com.example.schoolservlet.utils.InputValidation;
import com.example.schoolservlet.utils.enums.StudentStatusEnum;
import com.example.schoolservlet.utils.PostgreConnection;
import com.example.schoolservlet.utils.records.StudentsPerformance;
import com.example.schoolservlet.utils.records.StudentsPerformanceCount;
import com.example.schoolservlet.utils.records.TeacherPendency;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StudentSubjectDAO implements GenericDAO<StudentSubject>, IStudentSubjectDAO {
    @Override
    public Map<Integer, StudentSubject> findMany(int skip, int take) throws DataException{
        Map<Integer, StudentSubject> studentsSubjects = new HashMap<>();

        try(Connection conn = PostgreConnection.getConnection();
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
        ){
            pstmt.setInt(1, take < 0 ? 0 : (take > Constants.MAX_TAKE ? Constants.MAX_TAKE : take));
            pstmt.setInt(2, skip < 0 ? 0 : skip);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()){
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
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao listar student_subject", sqle);
        }
    }
    public Map<Integer, List<StudentSubject>> findManyByTeacherId(int skip, int take, int teacherId) throws DataException, ValidationException {
        InputValidation.validateId(teacherId, "id do professor");
        Map<Integer, List<StudentSubject>> studentsMap = new HashMap<>();

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT\n" +
                             "    ss.id, ss.obs, ss.grade1, ss.grade2,\n" +
                             "    st.id AS id_student, st.name AS student_name, st.cpf AS student_cpf, st.email AS student_email,\n" +
                             "    sb.id AS id_subject, sb.name AS subject_name, sb.deadline AS subject_deadline\n" +
                             "FROM student st\n" +
                             "JOIN school_class sc ON sc.id = st.id_school_class\n" +
                             "JOIN school_class_teacher sct ON sct.id_school_class = sc.id\n" +
                             "JOIN student_subject ss ON ss.id_student = st.id\n" +
                             "JOIN school_class_subject scs ON scs.id_school_class = sc.id\n AND scs.id_subject = ss.id_subject\n" +
                             "JOIN subject sb ON sb.id = ss.id_subject\n" +
                             "WHERE sct.id_teacher = ? AND st.status = ?\n" +
                             "ORDER BY st.id\n" +
                             "LIMIT ? OFFSET ?;")) {

            pstmt.setInt(1, teacherId);
            pstmt.setInt(2, StudentStatusEnum.ACTIVE.ordinal() + 1);
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

    @Override
    public Map<Integer, StudentSubject> findMany(int skip, int take, int studentId) throws DataException, ValidationException{
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
                     "WHERE st.ID = ? " +
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

    public Map<Integer, List<StudentSubject>> findManyByStudentId(int skip, int take, int studentId) throws DataException, ValidationException {
        InputValidation.validateId(studentId, "id do aluno");
        Map<Integer, List<StudentSubject>> studentsMap = new HashMap<>();

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
            pstmt.setInt(2, take < 0 ? 0 : (take > Constants.MAX_TAKE ? Constants.MAX_TAKE : take));
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

    @Override
    public StudentSubject findById(int id) throws NotFoundException, DataException, ValidationException {
        InputValidation.validateId(id, "id");

        try(Connection conn = PostgreConnection.getConnection();
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
                    "ON sb.id = ss.subject_id WHERE ss.id = ?")){
            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
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
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao buscar relação entre aluno e matéria", sqle);
        }
    }

    public StudentsPerformanceCount studentsPerformanceCount(int teacherId) throws DataException, ValidationException{
        InputValidation.validateId(teacherId, "id do professor");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT " +
                    "SUM(CASE WHEN media >= ? THEN 1 ELSE 0 END) AS approved, " +
                    "SUM(CASE WHEN media < ? THEN 1 ELSE 0 END) AS failed, " +
                    "SUM(CASE WHEN media IS NULL THEN 1 ELSE 0 END) AS pending " +
                    "FROM (" +
                    "    SELECT " +
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
                    ") AS sub")){
            pstmt.setDouble(1, Constants.MIN_GRADE_TO_BE_APPROVAL);
            pstmt.setDouble(2, Constants.MIN_GRADE_TO_BE_APPROVAL);
            pstmt.setInt(3, teacherId);
            pstmt.setInt(4, StudentStatusEnum.ACTIVE.ordinal() + 1);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()){
                return new StudentsPerformanceCount(rs.getInt("approved"),  rs.getInt("pending"), rs.getInt("failed"));
            }
            return new StudentsPerformanceCount(0, 0, 0);
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao buscar alunos", sqle);
        }
    }

    @Override
    public Map<Integer, StudentSubject> findStudentsThatRequireTeacher(int teacherId) throws DataException, ValidationException {
        InputValidation.validateId(teacherId, "id do professor");
        Map<Integer, StudentSubject> studentsSubjects = new HashMap<>();

        String sql = "SELECT * FROM (" +
                "SELECT ss.id, ss.obs, ss.grade1, ss.grade2, " +
                "st.id AS id_student, st.name AS student_name, st.cpf AS student_cpf, st.email AS student_email, " +
                "sb.id AS id_subject, sb.name AS subject_name, sb.deadline AS subject_deadline, " +
                "CASE " +
                "WHEN ss.grade1 IS NOT NULL AND ss.grade2 IS NOT NULL THEN (ss.grade1 + ss.grade2) / 2.0 " +
                "WHEN ss.grade1 IS NOT NULL THEN ss.grade1 " +
                "ELSE ss.grade2 " +
                "END AS media " +
                "FROM student_subject ss " +
                "JOIN student st ON st.id = ss.id_student " +
                "JOIN subject sb ON sb.id = ss.id_subject " +
                "JOIN school_class sc ON sc.id = st.id_school_class " +
                "JOIN school_class_teacher sct ON sct.id_school_class = sc.id " +
                "WHERE sct.id_teacher = ? " +
                "AND (ss.grade1 IS NOT NULL OR ss.grade2 IS NOT NULL)" +
                ") AS sub " +
                "WHERE media < ? " +
                "ORDER BY media ASC " +
                "LIMIT ?";

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, teacherId);
            pstmt.setInt(2, Constants.MAX_GRADE_TO_HELP);
            pstmt.setInt(3, Constants.STUDENTS_HELP_TAKE);
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

    @Override
    public StudentsPerformance studentsPerformance(int idTeacher) throws ValidationException, DataException{
        InputValidation.validateId(idTeacher, "id do professor");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT\n" +
                    "COALESCE(\n" +
                    "    ROUND(\n" +
                    "        100.0 * SUM(CASE WHEN media >= ? THEN 1 ELSE 0 END) / NULLIF(COUNT(*),0), 0\n" +
                    "    ), 0) AS approved,\n" +
                    "\n" +
                    "COALESCE(\n" +
                    "    ROUND(\n" +
                    "        100.0 * SUM(CASE WHEN media < ? THEN 1 ELSE 0 END) / NULLIF(COUNT(*),0), 0\n" +
                    "    ), 0) AS failed,\n" +
                    "\n" +
                    "COALESCE(\n" +
                    "    ROUND(\n" +
                    "        100.0 * SUM(CASE WHEN media IS NULL THEN 1 ELSE 0 END) / NULLIF(COUNT(*),0), 0\n" +
                    "    ), 0) AS pending\n" +
                    "\n" +
                    "FROM (\n" +
                    "    SELECT\n" +
                    "        s.id,\n" +
                    "        CASE\n" +
                    "            WHEN ss.grade1 IS NOT NULL AND ss.grade2 IS NOT NULL\n" +
                    "                THEN (ss.grade1 + ss.grade2) / 2.0\n" +
                    "            WHEN ss.grade1 IS NOT NULL\n" +
                    "                THEN ss.grade1\n" +
                    "            WHEN ss.grade2 IS NOT NULL\n" +
                    "                THEN ss.grade2\n" +
                    "            ELSE NULL\n" +
                    "        END AS media\n" +
                    "\n" +
                    "    FROM student s\n" +
                    "    JOIN school_class sc\n" +
                    "        ON sc.id = s.id_school_class\n" +
                    "    JOIN school_class_teacher sct\n" +
                    "        ON sct.id_school_class = sc.id\n" +
                    "    LEFT JOIN student_subject ss\n" +
                    "        ON ss.id_student = s.id\n" +
                    "\n" +
                    "    WHERE sct.id_teacher = ?\n AND s.status = ?" +
                    ") AS sub;")){
            pstmt.setDouble(1, Constants.MIN_GRADE_TO_BE_APPROVAL);
            pstmt.setDouble(2, Constants.MIN_GRADE_TO_BE_APPROVAL);
            pstmt.setInt(3, idTeacher);
            pstmt.setInt(4, StudentStatusEnum.ACTIVE.ordinal() + 1);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()){
                 return new StudentsPerformance(rs.getInt("approved"),  rs.getInt("pending"), rs.getInt("failed"));
            }
            return new StudentsPerformance(0, 0, 0);
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao buscar alunos", sqle);
        }
    }

    @Override
    public List<TeacherPendency> teacherPendency(int idTeacher) throws DataException, ValidationException{
        InputValidation.validateId(idTeacher, "id do professor");
        List<TeacherPendency> pendencies = new ArrayList<>();

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT \n" +
                    "st.id AS id_student,\n" +
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
                    "JOIN school_class_teacher sct ON sct.id_school_class = sc.id\n" +
                    "WHERE sct.id_teacher = ? AND st.status = ?\n" +
                    "AND (ss.grade1 IS NULL or ss.grade2 IS NULL)\n" +
                    "ORDER BY sb.deadline ASC LIMIT ?;")){
            pstmt.setInt(1, idTeacher);
            pstmt.setInt(2, StudentStatusEnum.ACTIVE.ordinal() + 1);
            pstmt.setInt(3, Constants.PENDENCIES_TAKE);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                pendencies.add(new TeacherPendency(
                        rs.getInt("id_student"),
                        rs.getString("student_name"),
                        rs.getString("subject_name"),
                        rs.getObject("grade1") != null ? rs.getDouble("grade1") : null,
                        rs.getObject("grade2") != null ? rs.getDouble("grade2") : null,
                        rs.getDate("deadline"),
                        rs.getString("status")
                ));
            }

            return pendencies;
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao buscar pendências", sqle);
        }
    }

    @Override
    public int totalCount() throws DataException {
        try(Connection conn = PostgreConnection.getConnection();
            Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS totalCount FROM student_subject");

            if (rs.next()) {
                return rs.getInt("totalCount");
            }
            return -1;
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao contar relações entre alunos e professores", sqle);
        }
    }

    public int countByTeacherId(int teacherId) throws DataException, ValidationException{
        InputValidation.validateId(teacherId, "id do professor");
        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT COUNT(*) AS totalCount " +
                             "FROM student_subject ss " +
                             "JOIN subject sb ON sb.id = ss.id_subject " +
                             "JOIN subject_teacher sbt ON sbt.id_subject = sb.id " +
                             "WHERE sbt.id_teacher = ?")) {

            pstmt.setInt(1, teacherId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("totalCount");
            }
            return -1;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao contar relações do professor", sqle);
        }
    }

    @Override
    public int totalCount(int studentId) throws DataException, ValidationException{
        InputValidation.validateId(studentId, "id do aluno");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(
                    "SELECT COUNT(*) AS totalCount FROM student_subject WHERE id_student = ?"
            )){
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("totalCount");
            }
            return -1;
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao contar relações entre alunos e professores", sqle);
        }
    }

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

    @Override
    public void create(StudentSubject studentSubject) throws DataException, ValidationException {
        InputValidation.validateId(studentSubject.getStudent().getId(), "id_student");
        InputValidation.validateId(studentSubject.getSubject().getId(), "id_subject");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO student_subject " +
                    "(id_student, id_subject, grade1, grade2, obs) VALUES " +
                    "(?, ?, ?, ?, ?)")){
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
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao atribuir uma matéria a um usuário");
        }
    }

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

    @Override
    public void update(StudentSubject studentSubject) throws NotFoundException, DataException, ValidationException {
        InputValidation.validateId(studentSubject.getId(), "id");
        InputValidation.validateId(studentSubject.getSubject().getId(),"id da matéria");
        InputValidation.validateId(studentSubject.getStudent().getId(), "id do aluno");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("UPDATE student_subject SET " +
                    "id_student = ?, id_subject = ?, grade1 = ?, grade2 = ?, obs = ? WHERE id = ?")){
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

            if (pstmt.executeUpdate() <= 0) throw new NotFoundException("matérias por aluno", "id", String.valueOf(studentSubject.getId()));
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao atualizar um dado de uma matéria de um aluno");
        }
    }

    @Override
    public void delete(int id) throws NotFoundException, DataException, ValidationException {
        InputValidation.validateId(id, "id");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM student_subject WHERE id = ?")){
            pstmt.setInt(1, id);

            if (pstmt.executeUpdate() <= 0) throw new NotFoundException("matérias por aluno", "id", String.valueOf(id));
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao deletar matéria de um aluno");
        }
    }
}