package com.example.schoolservlet.models;

public class SchoolClassSubject {
    // Attributes:
    private int id;
    private SchoolClass schoolClass;
    private Subject subject;

    // Construtors:
    public SchoolClassSubject() {}

    public SchoolClassSubject(int id, SchoolClass schoolClass, Subject subject) {
        this.id = id;
        this.schoolClass = schoolClass;
        this.subject = subject;
    }

    // Getters:
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public SchoolClass getSchoolClass() { return schoolClass; }

    // Setters:
    public void setSchoolClass(SchoolClass schoolClass) { this.schoolClass = schoolClass; }
    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }
}