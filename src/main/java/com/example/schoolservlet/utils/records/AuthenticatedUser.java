package com.example.schoolservlet.utils.records;

import com.example.schoolservlet.utils.enums.UserRoleEnum;

public record AuthenticatedUser(int id, String email, UserRoleEnum role) {}