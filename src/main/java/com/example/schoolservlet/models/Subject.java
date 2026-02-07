package com.example.schoolservlet.models;

public class Subject {
    // Attributes:
    private int id;
    private String name;
    private String teacherName;
    private String teacherUser;
    private String teacherPassword;

    // Constructors:
    public Subject(){

    }

    public Subject(int id, String name, String teacherName, String teacherUser, String teacherPassword){
        this.id = id;
        this.name = name;
        this.teacherName = teacherName;
        this.teacherUser = teacherUser;
        this.teacherPassword = teacherPassword;
    }
    public Subject(int id){
        this.id = id;
    }

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
    public void setName(String name) {
        this.name = name;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public void setTeacherUser(String teacherUser) {
        this.teacherUser = teacherUser;
    }

    public void setTeacherPassword(String teacherPassword) {this.teacherPassword = teacherPassword; }
}
