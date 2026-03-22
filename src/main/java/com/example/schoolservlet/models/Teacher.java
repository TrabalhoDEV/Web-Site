package com.example.schoolservlet.models;

import java.util.List;

/**
 * Represents a teacher.
 */
public class Teacher {
    //    Atributes:
    private int id;
    private String name;
    private String email;
    private String username;
    private String password;
    private int subjectCount;

//    Construtors:
    /**
     * Creates a teacher without password information.
     *
     * @param id teacher identifier
     * @param name teacher name
     * @param email teacher email
     * @param username teacher username
     */
    public Teacher(int id, String name, String email, String username) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.username = username;
    }

    /**
     * Creates a teacher with authentication information.
     *
     * @param id teacher identifier
     * @param name teacher name
     * @param email teacher email
     * @param username teacher username
     * @param password teacher password
     */
    public Teacher(int id, String name, String email, String username, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    /**
     * Creates an empty teacher instance.
     */
    public Teacher() {}

//    Getters:
    /**
     * Returns the teacher identifier.
     *
     * @return teacher identifier
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the teacher name.
     *
     * @return teacher name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the teacher email.
     *
     * @return teacher email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the teacher username.
     *
     * @return teacher username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the teacher password.
     *
     * @return teacher password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Returns the number of subjects assigned to this teacher.
     *
     * @return subject count
     */
    public int getSubjectCount() { return subjectCount; }

//    Setters:
    /**
     * Sets the teacher identifier.
     *
     * @param id teacher identifier
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets the teacher name.
     *
     * @param name teacher name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the teacher email.
     *
     * @param email teacher email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets the teacher username.
     *
     * @param username teacher username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Sets the teacher password.
     *
     * @param password teacher password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Sets the number of subjects assigned to this teacher.
     *
     * @param subjectCount subject count
     */
    public void setSubjectCount(int subjectCount) { this.subjectCount = subjectCount; }

    /**
     * Returns a string representation of this teacher.
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return "Teacher{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}