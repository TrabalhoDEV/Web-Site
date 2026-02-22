package com.example.schoolservlet.daos;

import com.example.schoolservlet.daos.interfaces.GenericDAO;
import com.example.schoolservlet.daos.interfaces.IStudentDAO;
import com.example.schoolservlet.exceptions.*;
import com.example.schoolservlet.models.Student;
import com.example.schoolservlet.models.StudentSubject;
import com.example.schoolservlet.utils.*;
import com.example.schoolservlet.utils.enums.StudentStatusEnum;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class StudentDAO implements GenericDAO<Student>, IStudentDAO {
    @Override
    public Student findById(int id) throws NotFoundException, DataException, InvalidNumberException{
        if (id <= 0) throw new InvalidNumberException("id", "ID deve ser maior do que 0");

        try(
            Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM student WHERE id = ?")
        ) {
            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()){
                Student student = new Student();
                student.setId(id);
                student.setIdSchoolClass(rs.getInt("id_school_class"));
                student.setCpf(rs.getString("cpf"));
                student.setName(rs.getString("name"));
                student.setEmail(rs.getString("email"));
                student.setStatus(StudentStatusEnum.values()[rs.getInt("status") - 1]);

                return student;
            } else throw new NotFoundException("aluno", "matrícula", String.valueOf(id));
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao buscar aluno", sqle);
        }
    }

    @Override
    public Map<Integer, Student> findMany(int skip, int take) throws DataException{
        Map<Integer, Student> students = new HashMap<>();

        try (Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM student ORDER BY status DESC LIMIT ? OFFSET ?")){
            pstmt.setInt(1, take < 0 ? 0 : (take > Constants.MAX_TAKE ? Constants.MAX_TAKE : take));
            pstmt.setInt(2, skip < 0 ? 0 : skip);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()){
                Student student = new Student();
                student.setId(rs.getInt("id"));
                student.setIdSchoolClass(rs.getInt("id_school_class"));
                student.setCpf(rs.getString("cpf"));
                student.setName(rs.getString("name"));
                student.setEmail(rs.getString("email"));
                student.setStatus(StudentStatusEnum.values()[rs.getInt("status") - 1]);

                students.put(rs.getInt("id"), student);
            }

            return students;
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao listar alunos", sqle);
        }
    }

    @Override
    public Map<Integer, Student> findManyByTeacherId(int skip, int take, int idTeacher) throws DataException, ValidationException{
        InputValidation.validateId(idTeacher, "id do professor");
        Map<Integer, Student> students = new HashMap<>();

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT s.id AS id_student, " +
                     "s.name," +
                     "s.cpf, " +
                     "s.email, " +
                     "s.id_school_class, " +
                     "s.status, " +
                     "sc.school_year, " +
                     "sct.* FROM student s " +
                     "JOIN school_class sc ON sc.id = s.id_school_class " +
                     "JOIN school_class_teacher sct ON sct.id_school_class = sc.id " +
                     "AND sct.id_teacher = ? ORDER BY status DESC LIMIT ? OFFSET ?")){
            pstmt.setInt(1, idTeacher);
            pstmt.setInt(2, take < 0 ? 0 : (take > Constants.MAX_TAKE ? Constants.MAX_TAKE : take));
            pstmt.setInt(3, skip < 0 ? 0 : skip);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()){
                Student student = new Student();
                student.setId(rs.getInt("id_student"));
                student.setIdSchoolClass(rs.getInt("id_school_class"));
                student.setCpf(rs.getString("cpf"));
                student.setName(rs.getString("name"));
                student.setEmail(rs.getString("email"));
                student.setStatus(StudentStatusEnum.values()[rs.getInt("status") - 1]);

                students.put(rs.getInt("id_student"), student);
            }

            return students;
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao listar alunos", sqle);
        }
    }

    @Override
    public int totalCount() throws DataException{
        try(Connection conn = PostgreConnection.getConnection();
            Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS totalCount FROM student");

            if (rs.next()){
                return rs.getInt("totalCount");
            }
            return -1;
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao contar alunos", sqle);
        }
    }

    public int countByTeacherId(int idTeacher) throws DataException, ValidationException{
        InputValidation.validateId(idTeacher, "id do professor");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) AS count_by_id_teacher FROM student s " +
                    "JOIN school_class sc ON sc.id = s.id_school_class " +
                    "JOIN school_class_teacher sct ON sct.id_school_class = sc.id AND sct.id_teacher = ?;")){
            pstmt.setInt(1, idTeacher);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                return rs.getInt("count_by_id_teacher");
            }
            return -1;
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao contar alunos", sqle);
        }
    }
    @Override
    public void create(Student student) throws DataException, ValidationException {
        if (student.getCpf() == null || student.getCpf().isEmpty()) throw new RequiredFieldException("cpf");
        InputValidation.validateId(student.getIdSchoolClass(), "id_school_class");

        try (Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO student " +
                    "(status, cpf, id_school_class) " +
                    "VALUES (?, ?, ?)")){

            FieldAlreadyUsedValidation.exists("student", "cpf", student.getCpf());
            FieldAlreadyUsedValidation.exists("admin", "document", String.valueOf(student.getCpf()));
            pstmt.setInt(1, StudentStatusEnum.INACTIVE.ordinal() + 1);
            pstmt.setString(2, InputNormalizer.normalizeCpf(student.getCpf()));
            pstmt.setInt(3, student.getIdSchoolClass());

            pstmt.executeUpdate();
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao criar aluno", sqle);
        } catch (ValueAlreadyExistsException vaee){
            throw new ValueAlreadyExistsException("cpf", student.getCpf());
        }
    }

    @Override
    public void update(Student student) throws NotFoundException, DataException, ValidationException {
        InputValidation.validateId(student.getId(), "id");

        try (Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("UPDATE student SET name = ?, " +
                    "email = ? WHERE id = ?")){
            pstmt.setString(1, InputNormalizer.normalizeName(student.getName()));
            pstmt.setString(2, InputNormalizer.normalizeEmail(student.getEmail()));
            pstmt.setInt(3, student.getId());

            if (pstmt.executeUpdate() <= 0) throw new NotFoundException("aluno", "matrícula", String.valueOf(student.getId()));
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao atualizar aluno", sqle);
        }
    }

    @Override
    public void updateIdSchoolClass(int id, int idSchoolClass) throws NotFoundException, DataException, ValidationException{
        InputValidation.validateId(id, "id");
        InputValidation.validateId(idSchoolClass, "id_school_class");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("UPDATE student SET id_school_class = ? WHERE id = ?")){
            pstmt.setInt(1, idSchoolClass);
            pstmt.setInt(2, id);

            if (pstmt.executeUpdate() <= 0) throw new NotFoundException("aluno", "matrícula", String.valueOf(id));
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao atualizar id da sala", sqle);
        }
    }

    @Override
    public void updatePassword(int id, String password) throws NotFoundException, DataException, ValidationException{
        InputValidation.validateId(id, "id");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("UPDATE student SET password = ? WHERE id = ?")){
            pstmt.setString(1, BCrypt.hashpw(password, BCrypt.gensalt(12)));
            pstmt.setInt(2, id);

            if (pstmt.executeUpdate() <= 0) throw new NotFoundException("aluno", "matrícula", String.valueOf(id));
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao atualizar senha", sqle);
        }
    }

    @Override
    public void enrollIn(Student student) throws NotFoundException, DataException, ValidationException{
        InputValidation.validateId(student.getId(), "id");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("UPDATE student SET name = ?, email = ?, password = ?, status = ? WHERE id = ?")){
            pstmt.setString(1, InputNormalizer.normalizeName(student.getName()));
            pstmt.setString(2, InputNormalizer.normalizeEmail(student.getEmail()));
            pstmt.setString(3, BCrypt.hashpw(student.getPassword(), BCrypt.gensalt(12)));
            pstmt.setInt(4, StudentStatusEnum.ACTIVE.ordinal() + 1);
            pstmt.setInt(5, student.getId());

            if (pstmt.executeUpdate() <= 0) throw new NotFoundException("alunos", "matrícula", String.valueOf(student.getId()));
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao confimar matrícula", sqle);
        }
    }

    @Override
    public void delete(int id) throws NotFoundException, DataException, ValidationException {
        InputValidation.validateId(id, "id");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM student WHERE id = ?")){
            pstmt.setInt(1, id);

            if (pstmt.executeUpdate() <= 0) throw new NotFoundException("aluno", "matrícula", String.valueOf(id));
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao deletar aluno", sqle);
        }
    }

    @Override
    public boolean login(String enrollment, String password) throws NotFoundException, DataException, ValidationException {
        if (enrollment == null || enrollment.isEmpty()) throw new RequiredFieldException("matrícula");
        if (password == null || password.isEmpty()) throw new RequiredFieldException("senha");
        InputValidation.validateEnrollment(enrollment);
        int id = InputNormalizer.normalizeEnrollment(enrollment);

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT password FROM student WHERE id = ?")){
            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                String hash = rs.getString("password");
                return BCrypt.checkpw(password, hash);
            } else throw new NotFoundException("aluno", "matrícula", enrollment);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao logar aluno", sqle);
        }
    }
}
