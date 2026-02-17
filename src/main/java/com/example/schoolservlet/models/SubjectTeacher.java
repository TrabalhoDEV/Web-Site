package com.example.schoolservlet.models;

public class SubjectTeacher {
    private int id;
    private int idSubject;
    private int idTeacher;

    public SubjectTeacher() {}

    public SubjectTeacher(int id, int idSubject, int idTeacher) {
        this.id = id;
        this.idSubject = idSubject;
        this.idTeacher = idTeacher;
    }
    
    public int getId() {
        return id;
    }

    public int getIdSubject() {
        return idSubject;
    }

    public int getIdTeacher() {
        return idTeacher;
    }
    
    public void setIdSubject(int idSubject) {
        this.idSubject = idSubject;
    }

    public void setIdTeacher(int idTeacher) {
        this.idTeacher = idTeacher;
    }
}