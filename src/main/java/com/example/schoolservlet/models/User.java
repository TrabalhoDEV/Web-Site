package com.example.schoolservlet.models;

import com.example.schoolservlet.models.enums.UserStatusEnum;

public class User {
    private int enrollment;

    private String name;
    private String email;
    private String password;
    private String cpf;
    private UserStatusEnum status;

    // Getters:
    public int getEnrollment() {
        return enrollment;
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

    public UserStatusEnum getStatus() {
        return status;
    }

    // Setters:
    public void setEnrollment(int enrollment) {
        this.enrollment = enrollment;
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

    public void setStatus(UserStatusEnum status) {
        this.status = status;
    }
}
