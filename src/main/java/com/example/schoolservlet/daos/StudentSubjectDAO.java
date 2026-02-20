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

import java.sql.*;
import java.util.HashMap;
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

                studentsSubjects.put(rs.getInt("id"),
                        new StudentSubject(
                            rs.getInt("id"),
                            rs.getString("obs"),
                            rs.getObject("grade1", Double.class),
                            rs.getObject("grade2", Double.class),
                            student,
                            subject
                        )
                );
            }

            return studentsSubjects;
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao listar student_subject", sqle);
        }
    }

    @Override
    public Map<Integer, StudentSubject> findMany(int skip, int take, int studentId) {
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

            int limit = take < 0 ? 0 : Math.min(take, Constants.MAX_TAKE);
            int offset = Math.max(skip, 0);

            pstmt.setInt(1, studentId);
            pstmt.setInt(2, limit);
            pstmt.setInt(3, offset);

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

                StudentSubject studentSubject = new StudentSubject();
                studentSubject.setId(rs.getInt("id"));
                studentSubject.setObs(rs.getString("obs"));
                studentSubject.setGrade1(rs.getDouble("grade1"));
                studentSubject.setGrade2(rs.getDouble("grade2"));
                studentSubject.setStudent(student);
                studentSubject.setSubject(subject);

                studentsSubjects.put(studentSubject.getId(), studentSubject);
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

        return studentsSubjects;
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
                    "ON sb.id = ss.id_subject WHERE ss.id = ?")){
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
                    rs.getObject("grade1", Double.class),
                    rs.getObject("grade2", Double.class),
                    student,
                    subject
                );
            } else throw new NotFoundException("student_subject", "id", String.valueOf(id));
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao buscar relação entre aluno e matéria", sqle);
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