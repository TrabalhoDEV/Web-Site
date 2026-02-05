package com.example.schoolservlet.models;

public class Admin {
    // Attributes:
    private String document;
    private String password;

    // Constructors:
    public Admin(){

    }

    public Admin(String document){
        this.document = document;
    }

    // Getters:
    public String getDocument() {
        return document;
    }

    public String getPassword() {
        return password;
    }

    // Setters:

    public void setDocument(String document) {
        this.document = document;
    }
}
