package com.example.schoolservlet.utils.records;

import com.example.schoolservlet.utils.enums.UserRoleEnum;

public record AuthenticatedUser(String document, UserRoleEnum role) {}