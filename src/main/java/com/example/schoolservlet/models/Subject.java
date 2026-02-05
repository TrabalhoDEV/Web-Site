package com.example.schoolservlet.models;

public class Subject {
    // Attributes:
    private int id;
    private String name;
    private String teacherName;
    private String teacherUser;
    private String teacherPassword;

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public String getTeacherUser() {
        return teacherUser;
    }

    public String getTeacherPassword() {
        return teacherPassword;
    }

    // Setters:

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public void setTeacherUser(String teacherUser) {
        this.teacherUser = teacherUser;
    }
}
