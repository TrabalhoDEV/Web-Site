package com.example.schoolservlet.models;

public class SubjectTeacher {
    private int id;
    private Subject subject;
    private Teacher teacher;

//    Construtors:
    public SubjectTeacher() {}

    public SubjectTeacher(int id, Subject subject, Teacher teacher) {
        this.id = id;
        this.subject = subject;
        this.teacher = teacher;
    }

//    Getters:
    public int getId() {
        return id;
    }

    public Subject getSubject() {
        return subject;
    }

    public Teacher getTeacher() {
        return teacher;
    }

//    Setters:

    public void setId(int id) {
        this.id = id;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }
}