package com.example.schoolservlet.utils;

public class RegexService {
    /**
     * Static method that verifies if cpd is valid
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
     * @return          true if password is valid
     */
    public static boolean validatePassword(String password){
        return password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,28}$");
    }
}
