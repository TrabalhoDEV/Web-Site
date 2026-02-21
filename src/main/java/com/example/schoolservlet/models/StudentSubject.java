package com.example.schoolservlet.models;

public class StudentSubject {
    // Attributes:
    private int id;
    private Student student;
    private Subject subject;
    private Double grade1;
    private Double grade2;
    private String obs;

    // Constructors
    public StudentSubject(){

    }

    public StudentSubject(int id, String obs, Double grade1, Double grade2, Student student, Subject subject){
        this.id = id;
        this.student = student;
        this.subject = subject;
        this.grade1 = grade1;
        this.grade2 = grade2;
        this.obs = obs;
    }

    // Getters:

    public int getId() {
        return id;
    }

    public Student getStudent() {
        return student;
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

    public void setStudent(Student student) {
        this.student = student;
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

    @Override
    public String toString() {
        return "StudentSubject{" +
                "id=" + id +
                ", student=" + student +
                ", subject=" + subject +
                ", grade1=" + grade1 +
                ", grade2=" + grade2 +
                ", obs='" + obs + '\'' +
                '}';
    }
}
