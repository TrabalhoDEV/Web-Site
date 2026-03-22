package com.example.schoolservlet.utils;

import com.example.schoolservlet.exceptions.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for validating user input using regular expressions and
 * business-rule checks.
 *
 * <p>Centralizing validation rules here makes maintenance easier than spreading
 * validations across servlets and DAOs.
 */
public class InputValidation {
    /**
     * Validates CPF format and character set.
     *
     * @param cpf the user's CPF
     * @throws ValidationException if CPF is null/blank, contains invalid characters, or has invalid format
     */
    public static void validateCpf(String cpf) throws ValidationException{
        validateIsNull("cpf", cpf);
        if (!StandardCharsets.US_ASCII.newEncoder().canEncode(cpf)) throw new ValidationException("CPF contém caracteres inválidos");
        if (!cpf.matches("\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}")) throw new RegexException("cpf");
    }

    /**
     * Validates email format, character set, and length constraints.
     *
     * @param email the user's email
     * @throws ValidationException if email is null/blank, contains invalid characters,
     *                             violates length limits, or has invalid format
     */
    public static void validateEmail(String email) throws ValidationException{
        validateIsNull("email", email);
        if (!StandardCharsets.US_ASCII.newEncoder().canEncode(email)) throw new ValidationException("Email contém caracteres inválidos");
        if (email.length() > Constants.MAX_EMAIL_LENGTH) throw new MaxLengthException("email", Constants.MAX_EMAIL_LENGTH);
        if (email.length() < Constants.MIN_EMAIL_LENGTH) throw new MinLengthException("email", Constants.MIN_EMAIL_LENGTH);
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) throw new RegexException("email");
    }

    /**
     * Validates password against business rules.
     *
     * @param password the user's password
     * @throws ValidationException if password is null/blank, violates length limits,
     *                             or does not contain required character categories
     */
    public static void validatePassword(String password) throws ValidationException {
        validateIsNull("senha", password);

        if (password.length() > Constants.MAX_PASSWORD_LENGTH) throw new MaxLengthException("senha", Constants.MAX_PASSWORD_LENGTH);
        if (password.length() < Constants.MIN_PASSWORD_LENGTH) throw new MinLengthException("senha", Constants.MIN_PASSWORD_LENGTH);

        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;

        for(char c: password.toCharArray()){
            if (Character.isUpperCase(c)){
                hasUppercase = true;
            } else if (Character.isLowerCase(c)){
                hasLowercase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }

            if (hasUppercase && hasLowercase && hasDigit) break;
        }

        if (!hasUppercase) throw new MissingSomethingException("senha", "maiúculas");
        if (!hasLowercase) throw new MissingSomethingException("senha", "minúsculas");
        if (!hasDigit) throw new MissingSomethingException("senha", "números");
    }

    /**
     * Validates whether a grade is within the allowed range.
     *
     * @param grade the grade value
     * @return {@code true} if grade is between minimum and maximum limits (inclusive)
     */
    public static boolean validateGrade(double grade){
        return grade >= Constants.MIN_GRADE && grade <= Constants.MAX_GRADE;
    }

    /**
     * Validates student enrollment format.
     *
     * @param enrollment the student's enrollment
     * @throws ValidationException if enrollment is null/blank or does not match the required 6-digit format
     */
    public static void validateEnrollment(String enrollment) throws ValidationException{
        validateIsNull("matrícula", enrollment);
        if (!enrollment.matches("^\\d{6}$")) throw new RegexException("matrícula");
    }

    /**
     * Validates teacher username format and length.
     *
     * @param userName the teacher username
     * @throws ValidationException if username is null/blank, violates length limits,
     *                             or does not match the required pattern
     */
    public static void validateUserName(String userName) throws ValidationException{
        validateIsNull("usuário", userName);
        if (userName.length() > Constants.MAX_TEACHER_USERNAME_LENGTH) throw new MaxLengthException("usuário", Constants.MAX_TEACHER_USERNAME_LENGTH);
        if (userName.length() < Constants.MIN_TEACHER_USERNAME_LENGTH) throw new MinLengthException("usuário", Constants.MIN_TEACHER_USERNAME_LENGTH);
        if (!userName.matches("^[^\\s]+\\.[^\\s]+$")) throw new RegexException("usuário");
    }

    /**
     * Validates whether an ID is greater than zero.
     *
     * @param id the ID value to validate
     * @param field the field name associated with the ID
     * @throws RequiredFieldException if ID is zero
     * @throws InvalidNumberException if ID is negative
     */
    public static void validateId(int id, String field) throws RequiredFieldException, InvalidNumberException{
        if (id == 0) throw new RequiredFieldException(field);
        if (id < 0) throw new InvalidNumberException("id", "ID deve ser maior do que 0");
    }

    /**
     * Validates whether a student class number is within the allowed range.
     *
     * @param studentClass the student class number
     * @return {@code true} if studentClass is between allowed limits (inclusive)
     */
    public static boolean validateStudentClass(int studentClass) {
        return studentClass >= Constants.MIN_STUDENT_CLASS && studentClass <= Constants.MAX_STUDENT_CLASS;
    }

    /**
     * Validates teacher name format and length.
     *
     * @param name the teacher name
     * @throws ValidationException if name is null/blank, violates length limits,
     *                             or contains invalid characters
     */
    public static void validateTeacherName(String name) throws ValidationException {
        validateIsNull("nome", name);

        if (name.length() > Constants.MAX_TEACHER_NAME_LENGTH)
            throw new MaxLengthException("nome", Constants.MAX_TEACHER_NAME_LENGTH);

        if (name.length() < Constants.MIN_TEACHER_NAME_LENGTH)
            throw new MinLengthException("nome", Constants.MIN_TEACHER_NAME_LENGTH);

        if (!name.matches("^[A-Za-zÀ-ÿ ]+$"))
            throw new RegexException("nome");
    }

    /**
     * Validates that IDs submitted by a form exist in the database list.
     *
     * <p>Only IDs found in {@code idsFromDatabase} are returned.
     *
     * @param idsFromForm array of ID strings submitted from the form
     * @param idsFromDatabase list of valid IDs stored in database
     * @return a list containing only valid and existing IDs
     * @throws ValidationException if any submitted ID is not a valid number
     */
    public static List<Integer> validateIdsExist(
            String[] idsFromForm,
            List<Integer> idsFromDatabase
    ) throws ValidationException {

        List<Integer> validIds = new ArrayList<>();

        if (idsFromForm == null) return validIds;

        for (String idStr : idsFromForm) {
            try {
                int id = Integer.parseInt(idStr);
                validateId(id,"id");

                if (idsFromDatabase.contains(id)) {
                    validIds.add(id);
                }

            } catch (NumberFormatException e) {
                throw new ValidationException("ID inválido enviado.");
            }
        }

        return validIds;
    }

    /**
     * Validates whether a string field is null or blank.
     *
     * @param field the field name
     * @param input the field value
     * @throws RequiredFieldException if value is null or blank
     */
    public static void validateIsNull(String field, String input) throws RequiredFieldException{
        if (input == null || input.isBlank()) throw new RequiredFieldException(field);
    }

    /**
     * Validates subject name length constraints.
     *
     * @param name the subject name
     * @throws RequiredFieldException if name is null or blank
     * @throws MinLengthException if name length is below minimum
     * @throws MaxLengthException if name length exceeds maximum
     */
    public static void validateSubjectName(String name) throws RequiredFieldException, MinLengthException, MaxLengthException{
        validateIsNull("nome", name);
        if (name.length() > Constants.MAX_SUBJECT_NAME_LENGTH) throw new MaxLengthException("nome", Constants.MAX_SUBJECT_NAME_LENGTH);
        if (name.length() < Constants.MIN_SUBJECT_NAME_LENGTH) throw new MinLengthException("nome", Constants.MIN_SUBJECT_NAME_LENGTH);
    }

    /**
     * Validates whether student name contains only letters and whitespace.
     *
     * @param name the student name to validate
     * @throws ValidationException if name is null/blank or contains invalid characters
     */
    public static void validateStudentName(String name) throws ValidationException {
        validateIsNull("nome", name);
        for (char c : name.toCharArray()) {
            if (!Character.isLetter(c) && !Character.isWhitespace(c)) {
                throw new ValidationException("Nome contém caracteres inválidos");
            }
        }
    }

    /**
     * Validates school class name format.
     *
     * <p>Allowed characters: letters, digits, whitespace, ordinal symbols (º, ª).
     *
     * @param name the school class name
     * @throws ValidationException if name is null/blank or contains invalid characters
     */
    public static void validateSchoolClassName(String name) throws ValidationException{
        validateIsNull("nome da turma", name);
        if (!name.matches("^[\\p{L}\\d ºª]+$")) throw new ValidationException("Nome da turma contém caracteres inválidos");
    }
}