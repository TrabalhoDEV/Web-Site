package com.example.schoolservlet.daos;

import com.example.schoolservlet.daos.interfaces.GenericDAO;
import com.example.schoolservlet.daos.interfaces.IStudentDAO;
import com.example.schoolservlet.exceptions.*;
import com.example.schoolservlet.models.SchoolClass;
import com.example.schoolservlet.models.Student;
import com.example.schoolservlet.models.StudentSubject;
import com.example.schoolservlet.utils.*;
import com.example.schoolservlet.utils.enums.StudentStatusEnum;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class StudentDAO implements GenericDAO<Student>, IStudentDAO {

    /**
     * Retrieves a Student record from the database using its unique identifier.
     * The method validates the provided id, executes a query on the student table,
     * and maps the resulting data to a Student object.
     *
     * @param id the unique identifier of the student to be retrieved
     * @return the Student object corresponding to the provided id
     * @throws NotFoundException if no student is found with the specified identifier
     * @throws DataException if a database access error occurs during query execution
     * @throws InvalidNumberException if the provided id is less than or equal to zero
     */
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

    /**
     * Retrieves a Student record from the database using the CPF identifier.
     * The method validates the provided CPF value, executes a query on the
     * student table, and maps the resulting data to a Student object.
     *
     * @param cpf the CPF identifier used to search for the student
     * @return the Student object corresponding to the provided CPF
     * @throws DataException if a database access error occurs during query execution
     * @throws ValidationException if the provided CPF does not pass validation
     * @throws NotFoundException if no student is found with the specified CPF
     */
    public Student findByCpf(String cpf) throws DataException, ValidationException, NotFoundException {
        InputValidation.validateIsNull("cpf", cpf);

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT * FROM student WHERE cpf = ?")) {

            pstmt.setString(1, InputNormalizer.normalizeCpf(cpf));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Student student = new Student();
                student.setId(rs.getInt("id"));
                student.setName(rs.getString("name"));
                student.setEmail(rs.getString("email"));
                student.setCpf(rs.getString("cpf"));
                student.setIdSchoolClass(rs.getInt("id_school_class"));
                student.setStatus(StudentStatusEnum.values()[rs.getInt("status") - 1]);
                return student;
            } else throw new NotFoundException("student", "cpf", cpf);

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao buscar aluno por cpf", sqle);
        }
    }

    /**
     * Retrieves multiple Student records from the database using pagination.
     * The method applies limit and offset parameters to control the number of
     * results returned and the starting position of the query. The results are
     * ordered by student status in descending order and by identifier in ascending
     * order. Each record is mapped to a Student object and stored in a map where
     * the key represents the student identifier.
     *
     * @param skip the number of records to skip before starting to return results
     * @param take the maximum number of records to retrieve
     * @return a map containing Student objects indexed by their unique identifier
     * @throws DataException if a database access error occurs during query execution
     */
    @Override
    public Map<Integer, Student> findMany(int skip, int take) throws DataException{
        Map<Integer, Student> students = new HashMap<>();

        try (Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM student ORDER BY status DESC, id ASC LIMIT ? OFFSET ?")){
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

    /**
     * Retrieves multiple Student records associated with a specific Teacher using pagination.
     * The method validates the teacher identifier and executes a query joining the
     * student, school_class, and school_class_teacher tables to obtain students
     * belonging to classes taught by the specified teacher. The results are ordered
     * by student status in descending order and limited using pagination parameters.
     * Each record is mapped to a Student object and stored in a map indexed by the student identifier.
     *
     * @param skip the number of records to skip before starting to return results
     * @param take the maximum number of records to retrieve
     * @param idTeacher the unique identifier of the teacher whose students will be retrieved
     * @return a map containing Student objects indexed by their unique identifier
     * @throws DataException if a database access error occurs during query execution
     * @throws ValidationException if the provided teacher identifier does not pass validation
     */
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

    /**
     * Retrieves a paginated collection of students with optional filtering by
     * name, CPF, enrollment number, and status.
     * <p>
     * The method dynamically determines the filter type based on the provided
     * filter value and applies the corresponding search condition. Results are
     * joined with the class table to obtain class information and are ordered
     * by student status and identifier.
     * </p>
     *
     * @param skip the number of records to skip before collecting the results
     * @param take the maximum number of records to return
     * @param filter the value used to filter students by name, CPF, or enrollment number
     * @param status the optional status used to filter students
     * @return a map containing student entities indexed by their identifier
     * @throws DataException if an error occurs while accessing the database
     */
    public Map<Integer, Student> findMany(int skip, int take, String filter, StudentStatusEnum status) throws DataException {
        FilterType type = detectFilterType(filter);

        if (type == FilterType.NONE && status == null) return findMany(skip, take);

        String sql = "SELECT s.id, s.name, s.cpf, s.email, s.status, s.id_school_class, sc.school_year "
                + "FROM student s "
                + "JOIN school_class sc ON sc.id = s.id_school_class "
                + "WHERE 1=1 "
                + (switch (type) {
                    case NAME       -> "AND s.name ILIKE ? ";
                    case CPF        -> "AND s.cpf = ? ";
                    case ENROLLMENT -> "AND s.id = ? ";
                    default         -> " ";
                })
                + (status != null ? "AND s.status = ? " : "")
                + "ORDER BY s.status DESC, s.id ASC "
                + "LIMIT ? OFFSET ?";

        Map<Integer, Student> students = new HashMap<>();

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int paramIndex = 1;

            if (type != FilterType.NONE) {
                String trim = filter.trim();
                switch (type) {
                    case NAME       -> pstmt.setString(paramIndex++, "%" + trim + "%");
                    case CPF        -> pstmt.setString(paramIndex++, InputNormalizer.normalizeCpf(trim));
                    case ENROLLMENT -> pstmt.setInt(paramIndex++, InputNormalizer.normalizeEnrollment(trim));
                }
            }

            if (status != null) pstmt.setInt(paramIndex++, status.ordinal() + 1);

            pstmt.setInt(paramIndex++, Math.min(Math.max(take, 0), Constants.MAX_TAKE));
            pstmt.setInt(paramIndex,   Math.max(skip, 0));

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Student student = new Student();
                student.setId(rs.getInt("id"));
                student.setName(rs.getString("name"));
                student.setCpf(rs.getString("cpf"));
                student.setEmail(rs.getString("email"));
                student.setIdSchoolClass(rs.getInt("id_school_class"));
                student.setStatus(StudentStatusEnum.values()[rs.getInt("status") - 1]);
                students.put(student.getId(), student);
            }

            return students;

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao listar alunos", sqle);
        }
    }

    /**
     * Counts the total number of students that match the provided filter
     * and optional status criteria.
     * <p>
     * The method determines the filter type dynamically based on the given
     * filter value and applies the corresponding search condition for
     * student name, CPF, or enrollment number. If a status is provided,
     * the count is restricted to students with that status.
     * </p>
     *
     * @param filter the value used to filter students by name, CPF, or enrollment number
     * @param status the optional status used to restrict the count of students
     * @return the total number of students that match the specified criteria
     * @throws DataException if an error occurs while accessing the database
     */
    public int count(String filter, StudentStatusEnum status) throws DataException {
        FilterType type = detectFilterType(filter);

        if (type == FilterType.NONE && status == null) return totalCount();

        String sql = "SELECT COUNT(*) FROM student s WHERE 1=1 "
                + (switch (type) {
                    case NAME -> "AND s.name ILIKE ? ";
                    case CPF -> "AND s.cpf = ? ";
                    case ENROLLMENT -> "AND s.id = ? ";
                    default -> "";
                })
                + (status != null ? "AND s.status = ? " : "");

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int paramIndex = 1;

            if (type != FilterType.NONE) {
                String trim = filter.trim();
                switch (type) {
                    case NAME       -> pstmt.setString(paramIndex++, "%" + trim + "%");
                    case CPF        -> pstmt.setString(paramIndex++, InputNormalizer.normalizeCpf(trim));
                    case ENROLLMENT -> pstmt.setInt(paramIndex++, InputNormalizer.normalizeEnrollment(trim));
                }
            }

            if (status != null) pstmt.setInt(paramIndex, status.ordinal() + 1);

            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao contar alunos", sqle);
        }
    }

    /**
     * Retrieves the total number of Student records associated with a specific SchoolClass.
     * The method validates the provided school class identifier and executes a COUNT
     * query on the student table filtered by the class identifier.
     *
     * @param schoolClassId the unique identifier of the school class whose students will be counted
     * @return the number of students associated with the specified school class,
     *         or -1 if the query returns no result
     * @throws ValidationException if the provided school class identifier does not pass validation
     * @throws DataException if a database access error occurs during query execution
     */
    public int countBySchoolClass(int schoolClassId) throws ValidationException, DataException{
        InputValidation.validateId(schoolClassId, "id da turma");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) AS count_by_id_teacher FROM student s WHERE id_school_class = ?")){
            pstmt.setInt(1, schoolClassId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                return rs.getInt(1);
            }
            return -1;
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao contar alunos", sqle);
        }
    }

    /**
     * Retrieves the total number of Student records stored in the database.
     * The method executes an aggregate COUNT query on the student table
     * and returns the resulting value.
     *
     * @return the total number of student records in the database,
     *         or -1 if the query returns no result
     * @throws DataException if a database access error occurs during query execution
     */
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

    /**
     * Retrieves the total number of Student records associated with a specific Teacher.
     * The method validates the provided teacher identifier and executes a COUNT
     * query joining the student, school_class, and school_class_teacher tables
     * to determine how many students belong to classes taught by the specified teacher.
     *
     * @param idTeacher the unique identifier of the teacher whose students will be counted
     * @return the number of students associated with the specified teacher,
     *         or -1 if the query returns no result
     * @throws DataException if a database access error occurs during query execution
     * @throws ValidationException if the provided teacher identifier does not pass validation
     */
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

    /**
     * Creates a new Student record in the database.
     * The method validates required fields, verifies whether the CPF value
     * is already used in related tables, and inserts a new entry into the
     * student table with an initial status.
     *
     * @param student the Student object containing the data to be persisted
     * @throws DataException if a database access error occurs during the insert operation
     * @throws ValidationException if the provided data does not pass validation
     * @throws RequiredFieldException if the CPF field is null or empty
     * @throws ValueAlreadyExistsException if the provided CPF is already in use
     */
    @Override
    public void create(Student student) throws DataException, ValidationException {
        if (student.getCpf() == null || student.getCpf().isEmpty()) throw new RequiredFieldException("cpf");
        InputValidation.validateId(student.getIdSchoolClass(), "id_school_class");

        try (Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO student " +
                    "(status, cpf, email, id_school_class) " +
                    "VALUES (?, ?, ?, ?)")){

            FieldAlreadyUsedValidation.exists("student", "cpf", "cpf", student.getCpf());
            FieldAlreadyUsedValidation.exists("admin", "document", "cpf", String.valueOf(student.getCpf()));
            pstmt.setInt(1, StudentStatusEnum.INACTIVE.ordinal() + 1);
            pstmt.setString(2, InputNormalizer.normalizeCpf(student.getCpf()));
            pstmt.setString(3, InputNormalizer.normalizeEmail(student.getEmail()));
            pstmt.setInt(4, student.getIdSchoolClass());

            pstmt.executeUpdate();
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao criar aluno", sqle);
        } catch (ValueAlreadyExistsException vaee){
            throw new ValueAlreadyExistsException("cpf", student.getCpf());
        }
    }

    /**
     * Updates the name and email of an existing Student record in the database.
     * The method validates the student identifier and performs an update operation
     * in the student table using the normalized name and email values.
     *
     * @param student the Student object containing the identifier and updated data
     * @throws NotFoundException if no student exists with the specified identifier
     * @throws DataException if a database access error occurs during the update operation
     * @throws ValidationException if the provided student identifier does not pass validation
     */
    @Override
    public void update(Student student) throws NotFoundException, DataException, ValidationException {
        InputValidation.validateId(student.getId(), "id");

        try (Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("UPDATE student SET name = ?, " +
                    "email = ?, id_school_class = ? WHERE id = ?")){
            pstmt.setString(1, InputNormalizer.normalizeName(student.getName()));
            pstmt.setString(2, InputNormalizer.normalizeEmail(student.getEmail()));
            pstmt.setInt(3, student.getIdSchoolClass());
            pstmt.setInt(4, student.getId());

            if (pstmt.executeUpdate() <= 0) throw new NotFoundException("aluno", "matrícula", String.valueOf(student.getId()));
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao atualizar aluno", sqle);
        }
    }

    /**
     * Updates the school class identifier associated with an existing Student record.
     * The method validates both the student identifier and the target school class
     * identifier, then performs an update operation in the student table to change
     * the class assignment of the specified student.
     *
     * @param id the unique identifier of the student whose class will be updated
     * @param idSchoolClass the unique identifier of the school class to associate with the student
     * @throws NotFoundException if no student exists with the specified identifier
     * @throws DataException if a database access error occurs during the update operation
     * @throws ValidationException if any of the provided identifiers do not pass validation
     */
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

    /**
     * Updates the school class identifier for multiple Student records.
     * The method validates both the current and the new school class identifiers
     * and updates all students associated with the old class to reference the new one.
     *
     * @param oldId the identifier of the current school class assigned to the students
     * @param newId the identifier of the new school class that will replace the old one
     * @throws NotFoundException if no student records are found associated with the specified old class identifier
     * @throws DataException if a database access error occurs during the update operation
     * @throws ValidationException if any of the provided identifiers do not pass validation
     */
    public void updateManyIdSchoolClass(int oldId, int newId) throws NotFoundException, DataException, ValidationException{
        InputValidation.validateId(oldId, "id_school_class");
        InputValidation.validateId(newId, "id_school_class");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("UPDATE student SET id_school_class = ? WHERE id_school_class = ?")){
            pstmt.setInt(1, newId);
            pstmt.setInt(2, oldId);

            if (pstmt.executeUpdate() <= 0) throw new NotFoundException("aluno", "turma", String.valueOf(oldId));
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao atualizar id da sala", sqle);
        }
    }

    /**
     * Updates the password of an existing Student record in the database.
     * The method validates the provided student identifier and updates the
     * password field with a hashed value for the specified student.
     *
     * @param id the unique identifier of the student whose password will be updated
     * @param password the new password that will replace the current one
     * @throws NotFoundException if no student exists with the specified identifier
     * @throws DataException if a database access error occurs during the update operation
     * @throws ValidationException if the provided student identifier does not pass validation
     */
    @Override
    public void updatePassword(int id, String password) throws NotFoundException, DataException, ValidationException{
        InputValidation.validateId(id, "id");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("UPDATE student SET password = ? WHERE id = ?")){
            pstmt.setString(1, Argon.hash(password));
            pstmt.setInt(2, id);

            if (pstmt.executeUpdate() <= 0) throw new NotFoundException("aluno", "matrícula", String.valueOf(id));
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao atualizar senha", sqle);
        }
    }

    /**
     * Confirms the enrollment of a Student by updating their personal information
     * and setting their status to ACTIVE. The method validates the student identifier,
     * normalizes the name and email, hashes the password, and updates the corresponding
     * record in the database.
     *
     * @param student the Student object containing updated personal data and credentials
     * @throws NotFoundException if no student exists with the specified identifier
     * @throws DataException if a database access error occurs during the update operation
     * @throws ValidationException if the provided student identifier does not pass validation
     */
    @Override
    public void enrollIn(Student student) throws NotFoundException, DataException, ValidationException{
        InputValidation.validateId(student.getId(), "id");

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("UPDATE student SET name = ?, email = ?, password = ?, status = ? WHERE id = ?")){
            pstmt.setString(1, InputNormalizer.normalizeName(student.getName()));
            pstmt.setString(2, InputNormalizer.normalizeEmail(student.getEmail()));
            pstmt.setString(3, Argon.hash(student.getPassword()));
            pstmt.setInt(4, StudentStatusEnum.ACTIVE.ordinal() + 1);
            pstmt.setInt(5, student.getId());

            if (pstmt.executeUpdate() <= 0) throw new NotFoundException("alunos", "matrícula", String.valueOf(student.getId()));
        } catch (SQLException sqle){
            sqle.printStackTrace();
            throw new DataException("Erro ao confimar matrícula", sqle);
        }
    }

    /**
     * Deletes a Student record from the database by its unique identifier.
     * The method validates the student ID before attempting the deletion
     * and throws an exception if no matching record is found.
     *
     * @param id the unique identifier of the student to be deleted
     * @throws NotFoundException if no student exists with the specified identifier
     * @throws DataException if a database access error occurs during the deletion
     * @throws ValidationException if the provided student identifier does not pass validation
     */
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

    /**
     * Authenticates a Student using their enrollment number and password.
     * The method validates and normalizes the enrollment number, retrieves
     * the hashed password from the database, and verifies it against the
     * provided password using Argon2 hashing.
     *
     * @param enrollment the enrollment number of the student
     * @param password the plaintext password to be verified
     * @return true if the password matches the stored hash, false otherwise
     * @throws NotFoundException if no student exists with the specified enrollment
     * @throws DataException if a database access error occurs during authentication
     * @throws ValidationException if the enrollment number is invalid or missing
     */
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
                return Argon.verify(hash, password);
            } else throw new NotFoundException("aluno", "matrícula", enrollment);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new DataException("Erro ao logar aluno", sqle);
        }
    }

    /**
     * Enumeration representing the supported types of filters that can be
     * applied when searching for students.
     * <p>
     * The filter type determines how the input value should be interpreted
     * and which database field will be used in the query condition.
     * </p>
     */
    private enum FilterType { NAME, CPF, ENROLLMENT, NONE }

    /**
     * Determines the type of filter to be applied when searching for students.
     * <p>
     * The method analyzes the provided filter value and classifies it as an
     * enrollment number, CPF, name, or no filter. This classification is used
     * to decide which database field should be used in query conditions.
     * </p>
     *
     * @param filter the input value used to determine the filter type
     * @return the corresponding {@code FilterType} based on the detected pattern
     */
    private FilterType detectFilterType(String filter) {
        if (filter == null || filter.isBlank()) return FilterType.NONE;

        String trim = filter.trim();

        if (trim.matches("\\d{6}")) return FilterType.ENROLLMENT;

        if (trim.matches("\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}"))
            return FilterType.CPF;

        return FilterType.NAME;
    }
}
