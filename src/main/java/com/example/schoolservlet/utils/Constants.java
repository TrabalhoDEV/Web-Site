package com.example.schoolservlet.utils;

/**
 * Java class that contains constants that define business' rules, like length limits.
 * Doing that based on each table of database.
 */
public class Constants {
//    GLOBAL constraints:
    public static final int MAX_EMAIL_LENGTH = 355;
    public static final int MIN_EMAIL_LENGTH = 2;
    public static final int MAX_PASSWORD_LENGTH = 28;
    public static final int MIN_PASSWORD_LENGTH = 8;


//    TEACHER constraints:
    public static final int MAX_TEACHER_NAME_LENGTH = 150;
    public static final int MIN_TEACHER_NAME_LENGTH = 3;
    public static final int MAX_TEACHER_USERNAME_LENGTH = 50;
    public static final int MIN_TEACHER_USERNAME_LENGTH = 3;


//    STUDENT constraints:
    public static final int MAX_STUDENT_NAME_LENGHT = 150;
    public static final int MIN_STUDENT_NAME_LENGHT = 3;
    public static final int MAX_STUDENT_CLASS = 12;
    public static final int MIN_STUDENT_CLASS = 1;


//  SUBJECT constraints:
    public static final int MAX_SUBJECT_NAME_LENGTH = 100;
    public static final int MIN_SUBJECT_NAME_LENGTH = 2;

//  STUDENT_SUBJECT constraints:
    public static final double MAX_GRADE = 10.0;
    public static final double MIN_GRADE = 0.0;
    public static final int MAX_OBS_LENGTH = 2000;
    public static final int MIN_OBS_LENGTH = 3;

//    PAGES constratins:
    public static final int MAX_TAKE = 6;
    public static final int MIN_PAGE = 0;

// STRINGS
    public static final String EXPIRED_SESSION_MESSAGE = "Acesso expirado. Faça login novamente.";
    public static final String BLANK_ARGUMENT_MESSAGE = "Argumento ilegal: nenhum campo pode ser vazio.";
    public static final String INVALID_NUMBER_FORMAT_MESSAGE = "Argumento ilegal: formato numérico inválido.";
    public static final String INVALID_CPF_MESSAGE = "Argumento ilegal: CPF inválido.";
    public static final String INVALID_STUDENT_CLASS_MESSAGE = "Argumento ilegal: série escolar inválida.";
    public static final String UNEXPECTED_ERROR_MESSAGE = "Ocorreu um erro inesperado. Por favor, tente novamente mais tarde.";
    public static final String UNIQUE_VIOLATION_MESSAGE = "Ocorreu um erro: Dados ja estavam inseridos no sistema";
}