package com.example.schoolservlet.models;

/**
 * Represents the association between a school class and a subject.
 */
public class SchoolClassSubject {
    // Attributes:
    private int id;
    private SchoolClass schoolClass;
    private Subject subject;

    /** Creates an empty association instance. */
    public SchoolClassSubject() {}

    /**
     * Creates a class-subject association.
     *
     * @param id association identifier
     * @param schoolClass associated class
     * @param subject associated subject
     */
    public SchoolClassSubject(int id, SchoolClass schoolClass, Subject subject) {
        this.id = id;
        this.schoolClass = schoolClass;
        this.subject = subject;
    }

    /** @return association identifier */
    public int getId() { return id; }

    /**
     * Sets association identifier.
     *
     * @param id association identifier
     */
    public void setId(int id) { this.id = id; }

    /** @return associated class */
    public SchoolClass getSchoolClass() { return schoolClass; }

    /**
     * Sets associated class.
     *
     * @param schoolClass associated class
     */
    public void setSchoolClass(SchoolClass schoolClass) { this.schoolClass = schoolClass; }

    /** @return associated subject */
    public Subject getSubject() { return subject; }

    /**
     * Sets associated subject.
     *
     * @param subject associated subject
     */
    public void setSubject(Subject subject) { this.subject = subject; }
}