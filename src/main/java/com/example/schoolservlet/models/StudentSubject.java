package com.example.schoolservlet.models;

public class StudentSubject {
    // Attributes:
    private int id;
    private int studentId;
    private Student student;
    private int subjectId;
    private Subject subject;
    private Double grade1;
    private Double grade2;
    private String obs;

    // Constructors
    public StudentSubject(){

    }

    public StudentSubject(int id, int studentId, int subjectId, Double grade1, Double grade2, String obs){
        this.id = id;
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.grade1 = grade1;
        this.grade2 = grade2;
        this.obs = obs;
    }

    // Getters:

    public int getId() {
        return id;
    }

    public int getStudentId() {
        return studentId;
    }

    public Student getStudent() {
        return student;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public Subject getSubject() {
        return subject;
    }

    public Double getGrade1() {
        return grade1;
    }

    public Double getGrade2() {
        return grade2;
    }

    public Double getAverage() {
        if (grade1 == null || grade2 == null) {
            return null;
        }
        return (grade1 + grade2) / 2;
    }

    public String getObs() {
        return obs;
    }

    // Setters:


    public void setId(int id) {
        this.id = id;
    }

    public void setStudentId(int studentId) {
        this.studentId = StudentSubject.this.studentId;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public void setGrade1(Double grade1) {
        this.grade1 = grade1;
    }

    public void setGrade2(Double grade2) {
        this.grade2 = grade2;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }
}
