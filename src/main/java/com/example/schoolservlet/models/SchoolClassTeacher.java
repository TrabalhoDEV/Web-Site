package com.example.schoolservlet.models;

/**
 * Represents the association between a school class and a teacher.
 */
public class SchoolClassTeacher {
    //    Atributes:
    private int id;
    private SchoolClass schoolClass;
    private Teacher teacher;

//    Construtors:

    /** Creates an empty association instance. */
    public SchoolClassTeacher() {
    }

    /**
     * Creates a class-teacher association.
     *
     * @param id association identifier
     * @param schoolClass associated class
     * @param teacher associated teacher
     */
    public SchoolClassTeacher(int id, SchoolClass schoolClass, Teacher teacher) {
        this.id = id;
        this.schoolClass = schoolClass;
        this.teacher = teacher;
    }

    //    Getters:

    /** @return association identifier */
    public int getId() { return id; }

    /** @return associated class */
    public SchoolClass getSchoolClass() { return schoolClass; }

    /** @return associated teacher */
    public Teacher getTeacher() { return teacher; }

//    Setters:
    /**
     * Sets association identifier.
     *
     * @param id association identifier
     */
    public void setId(int id) { this.id = id; }

    /**
     * Sets associated class.
     *
     * @param schoolClass associated class
     */
    public void setSchoolClass(SchoolClass schoolClass) { this.schoolClass = schoolClass; }

    /**
     * Sets associated teacher.
     *
     * @param teacher associated teacher
     */
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }
}