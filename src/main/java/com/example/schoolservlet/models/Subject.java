package com.example.schoolservlet.models;

public class Subject {
    // Attributes:
    private int id;
    private String name;

    // Constructors:
    public Subject(){

    }

    public Subject(int id, String name){
        this.id = id;
        this.name = name;
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

    // Setters:
    public void setName(String name) {
        this.name = name;
    }
}
