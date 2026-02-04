package com.example.schoolservlet.models;

import com.example.schoolservlet.models.enums.UserStatusEnum;

public class User {
    private int enrollment;

    private String name;
    private String email;
    private String password;
    private String cpf;
    private UserStatusEnum status;
    public UserStatusEnum getStatus() {
        return status;
    }
}
