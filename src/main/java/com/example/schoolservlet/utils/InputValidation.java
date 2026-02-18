package com.example.schoolservlet.utils;

import com.example.schoolservlet.exceptions.*;

import java.nio.charset.StandardCharsets;

/**
 * Class that user regex or ifs to validate if user's input is valid or not.
 * This allows us to change business' rules of validation much easier than if we use that
 * directed in the Servlets or DAOs
 */
public class InputValidation {
    /**
     * Static method that verifies if cpf is valid
     * @param cpf Is user's cpf
     * @return    true if cpf's format is valid
     */
    public static void validateCpf(String cpf) throws ValidationException{
        if (cpf == null || cpf.isEmpty()) throw new RequiredFieldException("cpf");
        if (!StandardCharsets.US_ASCII.newEncoder().canEncode(cpf)) throw new ValidationException("CPF contém caracteres inválidos");
        if (!cpf.matches("\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}")) throw new RegexException("cpf");
    }

    /**
     * Static method to verifies email shape
     * @param email Is the e-mail from the user
     * @return      true if the email's format is valid (name@domain.com)
     */
    public static void validateEmail(String email) throws ValidationException{
        if (email == null || email.isEmpty()) throw new RequiredFieldException("email");
        if (!StandardCharsets.US_ASCII.newEncoder().canEncode(email)) throw new ValidationException("Email contém caracteres inválidos");
        if (email.length() > Constants.MAX_EMAIL_LENGTH) throw new MaxLengthException("email", Constants.MAX_EMAIL_LENGTH);
        if (email.length() < Constants.MIN_EMAIL_LENGTH) throw new MinLengthException("email", Constants.MIN_EMAIL_LENGTH);
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) throw new RegexException("email");
    }

    /**
     * Static method that verifies password format
     * @param password the user's password
     * @throws ValidationException if the password is different of some bussiness' rule for that
     */
    public static void validatePassword(String password) throws ValidationException {
        if (password == null || password.isEmpty()) throw new RequiredFieldException("senha");

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
     * Static method that validates if password passed in login is not empty
     * @param password is user's password
     * @throws RequiredFieldException if password id empty
     */
    public static void validateLoginPassword(String password) throws RequiredFieldException{
        if (password == null || password.isEmpty()) throw new RequiredFieldException("senha");
    }

    /**
     * Static method that verifies if the grade is not greater than 10 or less than 0
     * @param grade Is the user's grade
     * @return      if grade is valid returns true else returns false
     */
    public static boolean validateGrade(double grade){
        return grade >= Constants.MIN_GRADE && grade <= Constants.MAX_GRADE;
    }

    /**
     * Static method that validates enrollment
     * @param enrollment is student's enrollment
     * @throws ValidationException if enrollment has not 6 characters or if it's empty
     */
    public static void validateEnrollment(String enrollment) throws ValidationException{
        if (enrollment == null || enrollment.isEmpty()) throw new RequiredFieldException("matrícula");
        if (!enrollment.matches("^\\d{6}$")) throw new RegexException("matrícula");
    }

    /**
     * Static method that validates userName
     * @param userName is the teacher userName
     * @throws ValidationException if userName is empty, or has more or less characters than allowed,
     *                             or if it has not a dot in the middle
     */
    public static void validateUserName(String userName) throws ValidationException{
        if (userName == null || userName.isEmpty()) throw new RequiredFieldException("usuário");
        if (userName.length() > Constants.MAX_TEACHER_USERNAME_LENGTH) throw new MaxLengthException("usuário", Constants.MAX_TEACHER_USERNAME_LENGTH);
        if (userName.length() < Constants.MIN_TEACHER_USERNAME_LENGTH) throw new MinLengthException("usuário", Constants.MIN_TEACHER_USERNAME_LENGTH);
        if (!userName.matches("^[^\\s]+\\.[^\\s]+$")) throw new RegexException("usuário");
    }

    /**
     * Static method that validates userName
     * @param studentClass Is the student's class
     * @return             true if studentClass is between 1 and 12
     */
    public static boolean validateStudentClass(int studentClass) {
        return studentClass >= Constants.MIN_STUDENT_CLASS && studentClass <= Constants.MAX_STUDENT_CLASS;
    }

    /**
     * Static method that validates if ID value is greater than 0
     * @param id is the ID that user want to find, update or delete
     * @param field is the name of field ID, if field is ID pass id, but if field is id_table pass id_table
     * @throws ValidationException  if id is empty or less than 0
     */
    public static void validateId(int id, String field) throws ValidationException{
        if (id == 0) throw new RequiredFieldException(field);
        if (id < 0) throw new InvalidNumberException("id", "ID deve ser maior do que 0");
    }
}