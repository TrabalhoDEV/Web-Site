package com.example.schoolservlet.models;

public class Admin {
    // Attributes:
    private int id;
    private String document;
    private String email;
    private String password;

    // Constructors:
    public Admin(){

    }

    public Admin(int id, String email, String document){
        this.id = id;
        this.email = email;
        this.document = document;
    }

    public Admin(String document, String password) {
        this.document = document;
        this.password = password;
    }

    // Getters:
    public int getId() {
        return id;
    }

    public String getDocument() {
        return document;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // Setters:

    public void setId(int id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDocument(String document) {
        this.document = document;
    }
}
