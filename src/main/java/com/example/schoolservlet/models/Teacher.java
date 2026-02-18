package com.example.schoolservlet.models;

public class Teacher {
<<<<<<< HEAD
//    Atributes:
=======
//    Atributtes:
>>>>>>> 127339c (feat: fixing some comments, also adding the exceptions to others DAO's methods)
    private int id;
    private String name;
    private String email;
    private String username;
    private String password;

//    Construtors:
    public Teacher(int id, String name, String email, String username) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.username = username;
    }
    public Teacher(int id, String name, String email, String username, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
    }
    public Teacher(){};

//    Getters:
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

//    Setters:
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
