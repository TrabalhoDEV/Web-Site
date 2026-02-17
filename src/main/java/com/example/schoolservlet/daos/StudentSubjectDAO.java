package com.example.schoolservlet.daos;

import com.example.schoolservlet.daos.interfaces.GenericDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.InvalidNumberException;
import com.example.schoolservlet.exceptions.NotFoundException;
import com.example.schoolservlet.exceptions.RequiredFieldException;
import com.example.schoolservlet.models.Student;
import com.example.schoolservlet.models.StudentSubject;
import com.example.schoolservlet.models.Subject;
import com.example.schoolservlet.utils.Constants;
import com.example.schoolservlet.utils.enums.StudentStatusEnum;
import com.example.schoolservlet.utils.PostgreConnection;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;


public class StudentSubjectDAO implements GenericDAO<StudentSubject> {
    @Override
    public Map<Integer, StudentSubject> findMany(int skip, int take) {
        Map<Integer, StudentSubject> studentsSubjects = new HashMap<>();

        try(Connection conn = PostgreConnection.getConnection();
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
                    "FROM student_subject ss JOIN student st ON st.id = ss.student_id JOIN subject sb " +
                    "ON sb.id = ss.subject_id ORDER BY ss.id LIMIT ? OFFSET ?")
        ){
            pstmt.setInt(1, take < 0 ? 0 : (take > Constants.MAX_TAKE ? Constants.MAX_TAKE : take));
            pstmt.setInt(2, skip < 0 ? 0 : skip);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()){
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
                studentSubject.setGrade1(rs.getObject("grade1", Double.class));
                studentSubject.setGrade2(rs.getObject("grade2", Double.class));
                studentSubject.setStudentId(student.getId());
                studentSubject.setStudent(student);
                studentSubject.setSubjectId(subject.getId());
                studentSubject.setSubject(subject);

                studentsSubjects.put(studentSubject.getId(), studentSubject);
            }
        } catch (SQLException sqle){
            sqle.printStackTrace();
        }

        return studentsSubjects;
    }

    @Override
    public StudentSubject findById(int id) {
        StudentSubject studentSubject = null;

        try(Connection conn = PostgreConnection.getConnection();
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
                    "FROM student_subject ss JOIN student st ON st.id = ss.student_id JOIN subject sb " +
                    "ON sb.id = ss.subject_id WHERE ss.id = ?")){
            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
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

                studentSubject = new StudentSubject();
                studentSubject.setId(rs.getInt("id"));
                studentSubject.setObs(rs.getString("obs"));
                studentSubject.setGrade1(rs.getObject("grade1", Double.class));
                studentSubject.setGrade2(rs.getObject("grade2", Double.class));
                studentSubject.setStudentId(student.getId());
                studentSubject.setStudent(student);
                studentSubject.setSubjectId(subject.getId());
                studentSubject.setSubject(subject);
            }

        } catch (SQLException sqle){
            sqle.printStackTrace();
        }

        return studentSubject;
    }
    @Override
    public int totalCount() {
        int totalCount = -1;
        try(Connection conn = PostgreConnection.getConnection();
            Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS totalCount FROM student_subject");

            if (rs.next()) {
                totalCount = rs.getInt("totalCount");
            }
        } catch (SQLException sqle){
            sqle.printStackTrace();
        }

        return totalCount;
    }

    @Override
    public void create(StudentSubject studentSubject) throws DataException, RequiredFieldException {
        if (studentSubject.getStudentId() == 0) throw new RequiredFieldException("id do aluno");
        if (studentSubject.getSubjectId() == 0) throw new RequiredFieldException("id da matéria");
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO student_subject " +
                    "(student_id, subject_id, grade1, grade2, obs) VALUES " +
                    "(?, ?, ?, ?, ?)")){
            pstmt.setInt(1, studentSubject.getStudentId());
            pstmt.setInt(2, studentSubject.getSubjectId());
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
    public void update(StudentSubject studentSubject) throws InvalidNumberException, NotFoundException, DataException {
        if (studentSubject.getId() <= 0) throw new InvalidNumberException("id", "ID deve ser maior do que 0");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("UPDATE student_subject SET " +
                    "student_id = ?, subject_id = ?, grade1 = ?, grade2 = ?, obs = ? WHERE id = ?")){
            pstmt.setInt(1, studentSubject.getStudentId());
            pstmt.setInt(2, studentSubject.getSubjectId());
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
    public void delete(int id) throws NotFoundException, DataException, InvalidNumberException {
        if (id <= 0) throw new InvalidNumberException("id", "ID deve ser maior do que 0");

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