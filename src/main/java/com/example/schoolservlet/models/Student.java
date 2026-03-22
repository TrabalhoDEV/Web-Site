package com.example.schoolservlet.models;

import com.example.schoolservlet.utils.enums.StudentStatusEnum;

/**
 * Represents a student.
 */
public class Student {
    // Attributes:
    private int id;
    private String name;
    private String email;
    private String password;
    private String cpf;
    private StudentStatusEnum status;
    private int idSchoolClass;

    /** Creates an empty student instance. */
    public Student(){}

    /**
     * Creates a student without password.
     *
     * @param id student identifier
     * @param name student name
     * @param email student email
     * @param cpf student CPF
     * @param status current student status
     * @param idSchoolClass class identifier
     */
    public Student(int id, String name, String email, String cpf, StudentStatusEnum status, int idSchoolClass){
        this.id = id;
        this.name = name;
        this.email = email;
        this.cpf = cpf;
        this.status = status;
        this.idSchoolClass = idSchoolClass;
    }

    /**
     * Creates a student with full data.
     *
     * @param id student identifier
     * @param name student name
     * @param email student email
     * @param password student password
     * @param cpf student CPF
     * @param status current student status
     * @param idSchoolClass class identifier
     */
    public Student(int id, String name, String email, String password, String cpf, StudentStatusEnum status, int idSchoolClass) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.cpf = cpf;
        this.status = status;
        this.idSchoolClass = idSchoolClass;
    }

    /** @return student identifier */
    public int getId() { return id; }

    /** @return student name */
    public String getName() { return name; }

    /** @return student email */
    public String getEmail() { return email; }

    /** @return student CPF */
    public String getCpf() { return cpf; }

    /** @return student status */
    public StudentStatusEnum getStatus() { return status; }

    /**
     * Returns enrollment code based on student ID, left-padded to 6 digits.
     *
     * @return enrollment string
     */
    public String getEnrollment(){
        return String.format("%06d", this.id);
    }

    /** @return class identifier */
    public int getIdSchoolClass() { return idSchoolClass; }

    /** @return student password */
    public String getPassword() { return password; }

    /** @param id student identifier */
    public void setId(int id) { this.id = id; }

    /** @param name student name */
    public void setName(String name) { this.name = name; }

    /** @param email student email */
    public void setEmail(String email) { this.email = email; }

    /** @param cpf student CPF */
    public void setCpf(String cpf) { this.cpf = cpf; }

    /** @param status student status */
    public void setStatus(StudentStatusEnum status) { this.status = status; }

    /** @param idSchoolClass class identifier */
    public void setIdSchoolClass(int idSchoolClass) { this.idSchoolClass = idSchoolClass; }

    /** @param password student password */
    public void setPassword(String password) { this.password = password; }

    /**
     * Returns a printable representation with masked password.
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + "****" + '\'' +
                ", cpf='" + cpf + '\'' +
                ", status=" + status +
                ", idSchoolClass=" + idSchoolClass +
                '}';
    }
}