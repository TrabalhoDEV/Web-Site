package com.example.schoolservlet.models;

public class StudentSubject {
    // Attributes:
    private int studentEnrollment;
    private int subjectId;
    private Double grade1;
    private Double grade2;
    private String obs;

    // Constructors
    public StudentSubject(){

    }

    public StudentSubject(int studentEnrollment, int subjectId, Double grade1, Double grade2, String obs){
        this.studentEnrollment = studentEnrollment;
        this.subjectId = subjectId;
        this.grade1 = grade1;
        this.grade2 = grade2;
        this.obs = obs;
    }

    // Getters:

    public int getStudentEnrollment() {
        return studentEnrollment;
    }

    public int getSubjectId() {
        return subjectId;
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

    public void setStudentEnrollment(int studentEnrollment) {
        this.studentEnrollment = studentEnrollment;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
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
