package com.example.schoolservlet.models;

import com.example.schoolservlet.utils.enums.StudentStatusEnum;

public class Student {
    // Attributes:
    private int id;

    private String name;
    private String email;
    private String password;
    private String cpf;
    private StudentStatusEnum status;

    // Constructors:
    public Student(){

    }

    public Student(int id, String name, String email, String cpf, StudentStatusEnum status){
        this.id = id;
        this.name = name;
        this.email = email;
        this.cpf = cpf;
        this.status = status;
    }

    // Getters:
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getCpf() {
        return cpf;
    }

    public StudentStatusEnum getStatus() {
        return status;
    }

    public String getEnrollment(){
        return String.format("%06d", this.id);
    }

    // Setters:
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setStatus(StudentStatusEnum status) {
        this.status = status;
    }

}
