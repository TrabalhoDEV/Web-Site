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
     * @param password the user's password
     * @return         a {@link PasswordValidationEnum} indication which validation rule failed, or if the password is valid
     */
    public static PasswordValidationEnum validatePassword(String password){
        if (password.length()>28){
            return PasswordValidationEnum.MAX_LENGHT_EXCEEDED;
        }
        if (password.length()<8){
            return PasswordValidationEnum.MIN_LENGHT_NOT_REACHED;
        }

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

            if (hasUppercase && hasLowercase && hasDigit){
                break;
            }
        }

        if (!hasUppercase){
            return PasswordValidationEnum.MISSING_UPPERCASE;
        }
        if (!hasLowercase){
            return PasswordValidationEnum.MISSING_LOWERCASE;
        }
        if (!hasDigit){
            return PasswordValidationEnum.MISSING_NUMBER;
        }

        return PasswordValidationEnum.RIGHT;
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
    public static boolean validateEnrollment(String enrollment){
        return enrollment.matches("^\\d{6}$");
    }
}