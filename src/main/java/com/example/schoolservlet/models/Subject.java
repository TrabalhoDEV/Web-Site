package com.example.schoolservlet.models;

import java.util.Date;

public class Subject {
    // Attributes:
    private int id;
    private String name;
    private Date dateline;

    // Constructors:
    public Subject(){

    }

    public Subject(int id, String name, Date dateline){
        this.id = id;
        this.name = name;
        this.dateline = dateline;
    }


    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getDateline() {
        return dateline;
    }
    // Setters:

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDateline(Date dateline) {
        this.dateline = dateline;
    }
}
