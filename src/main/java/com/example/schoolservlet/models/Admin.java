package com.example.schoolservlet.models;

public class Admin {
    // Attributes:
    private int id;
    private String document;
    private String password;

    // Constructors:
    public Admin(){

    }

    public Admin(int id, String document){
        this.id = id;
        this.document = document;
    }

    // Getters:
    public int getId() {
        return id;
    }

    public String getDocument() {
        return document;
    }

    public String getPassword() {
        return password;
    }

    // Setters:

    public void setId(int id) {
        this.id = id;
    }

    public void setDocument(String document) {
        this.document = document;
    }
}
