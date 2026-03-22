package com.example.schoolservlet.models;

/**
 * Represents the association between a subject and a teacher.
 */
public class SubjectTeacher {
    private int id;
    private Subject subject;
    private Teacher teacher;

//    Construtors:
    /** Creates an empty association instance. */
    public SubjectTeacher() {}

    /**
     * Creates a subject-teacher association.
     *
     * @param id association identifier
     * @param subject associated subject
     * @param teacher associated teacher
     */
    public SubjectTeacher(int id, Subject subject, Teacher teacher) {
        this.id = id;
        this.subject = subject;
        this.teacher = teacher;
    }

//    Getters:
    /** @return association identifier */
    public int getId() {
        return id;
    }

    /** @return associated subject */
    public Subject getSubject() {
        return subject;
    }

    /** @return associated teacher */
    public Teacher getTeacher() {
        return teacher;
    }

//    Setters:

    /**
     * Sets association identifier.
     *
     * @param id association identifier
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets associated subject.
     *
     * @param subject associated subject
     */
    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    /**
     * Sets associated teacher.
     *
     * @param teacher associated teacher
     */
    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }
}