package com.example.schoolservlet.models;

public class SubjectTeacher {
    // Attributes
    private int id;
    private int subjectId;
    private int teacherId;

    // Constructors
    public SubjectTeacher() {

    }

    public SubjectTeacher(int id, int subjectId, int teacherId) {
        this.id = id;
        this.subjectId = subjectId;
        this.teacherId = teacherId;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public int getTeacherId() {
        return teacherId;
    }

    //Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = Teacher.this.teacherId;
    }
}
