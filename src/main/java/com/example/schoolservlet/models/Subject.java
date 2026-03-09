package com.example.schoolservlet.models;

import java.util.Date;

public class Subject {
    // Attributes:
    private int id;
    private String name;
    private Date deadline;

    // Constructors:
    public Subject(){

    }

    public Subject(int id, String name, Date deadline){
        this.id = id;
        this.name = name;
        this.deadline = deadline;
    }


    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getDeadline() {
        return deadline;
    }
    // Setters:

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", deadline=" + deadline +
                '}';
    }
}
