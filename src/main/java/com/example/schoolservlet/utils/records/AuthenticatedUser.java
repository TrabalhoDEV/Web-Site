package com.example.schoolservlet.utils.records;

import com.example.schoolservlet.utils.enums.UserRoleEnum;

/**
 * Represents an authenticated user in the system.
 *
 * <p>Contains the user's ID, email, and role.
 *
 * @param id    the unique identifier of the user
 * @param email the email address of the user
 * @param role  the {@link UserRoleEnum} representing the user's role
 */
public record AuthenticatedUser(int id, String email, UserRoleEnum role) {}