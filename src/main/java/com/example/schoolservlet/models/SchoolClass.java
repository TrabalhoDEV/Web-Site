package com.example.schoolservlet.models;

public class SchoolClass {

    // Attributes:
    private int id;
    private String schoolYear;

    // Constructors:
    public SchoolClass() {

    }

    public SchoolClass (int id, String schoolYear) {
        this.id = id;
        this.schoolYear = schoolYear;
    }

    // Getters:
    public int getId() {
        return id;
    }

    public String getSchoolYear() {
        return schoolYear;
    }

    // Setters:
    public void setId(int id) {
        this.id = id;
    }

    public void setSchoolYear(String schoolYear) {
        this.schoolYear = schoolYear;
    }
}
