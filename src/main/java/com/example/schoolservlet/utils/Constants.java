package com.example.schoolservlet.utils;

/**
 * Java class that contains constants that define business' rules, like length limits.
 * Doing that based on each table of database.
 */
public class Constants {
//    GLOBAL constraints:
    public static final int MAX_EMAIL_LENGHT = 355;
    public static final int MIN_EMAIL_LENGHT = 2;
    public static final int MAX_PASSWORD_LENGTH = 28;
    public static final int MIN_PASSWORD_LENGHT = 8;


//    TEACHER constraints:
    public static final int MAX_TEACHER_NAME_LENGHT = 150;
    public static final int MIN_TEACHER_NAME_LENGHT = 3;
    public static final int MAX_TEACHER_USERNAME_LENGHT = 50;
    public static final int MIN_TEACHER_USERNAME_LENGHT = 3;


//    STUDENT constraints:
    public static final int MAX_STUDENT_NAME_LENGHT = 150;
    public static final int MIN_STUDENT_NAME_LENGHT = 3;


//  SUBJECT constraints:
    public static final int MAX_SUBJECT_NAME_LENGHT = 100;
    public static final int MIN_SUBJECT_NAME_LENGHT = 2;

//  STUDENT_SUBJECT constraints:
    public static final double MAX_GRADE = 10.0;
    public static final double MIN_GRADE = 0.0;
    public static final int MAX_OBS_LENGHT = 2000;
    public static final int MIN_OBS_LENGHT = 3;
}