package com.example.schoolservlet.utils;

/**
 * Class that user regex or ifs to validate if user's input is valid or not.
 * This allows us to change business' rules of validation much easier than if we use that
 * directed in the Servlets or DAOs
 */
public class ValidationService {
    /**
     * Static method that verifies if cpf is valid
     * @param cpf Is user's cpf
     * @return    true if cpf's format is valid
     */
    public static boolean validateCpf(String cpf){
        return cpf.matches("\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}");
    }

    /**
     * Static method to verifies email shape
     * @param email Is the e-mail from the user
     * @return      true if the email's format is valid (name@domain.com)
     */
    public static boolean validateEmail(String email){
        return email.matches("^[^@\\s]+@[^@\\s]+\\.[A-Za-z]+$");
    }

    /**
     * Static method that verifies password format
     * @param password Is the user password
     * @return         A number that allows to determine in which validation the user is being wrong
     */
    public static int validatePassword(String password){
        if (password.length()>28){
            return 1;
        }
        if (password.length()<8){
            return 2;
        }
        if (!password.matches(".*[A-Z].*")){
            return 3;
        }
        if (!password.matches(".*[a-z].*")){
            return 4;
        }
        if (!password.matches(".*\\d.*")){
            return 5;
        }
        return 0;
    }

    /**
     * Static method that verifies if the grade is not greater than 10 or less than 0
     * @param grade Is the user's grade
     * @return      if grade is valid returns true else returns false
     */
    public static boolean validateGrade(double grade){
        return grade >= 0 && grade <= 10;
    }

    /**
     * Static method that validates enrollment
     * @param enrollment Is student's enrollment
     * @return          true if enrollment has 6 characters
     */
    public static boolean validateEnrollment(int enrollment){
        return Integer.toString(enrollment).matches("^\\d{6}$");
    }
}