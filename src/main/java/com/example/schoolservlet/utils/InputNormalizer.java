package com.example.schoolservlet.utils;

/**
 *  Class that contains static methods to normalize the user's input to the database format.
 *  This class allows us to change the business' rule easier than if we keep that validation in
 *  other classes like DAOs or Servlets.
 */
public class InputNormalizer {
    /**
     * Static method that normalize e-mail to lower case
     * @param email Is user's e-mail
     * @return      e-mail in lower case
     */
    public static String normalizeEmail(String email){
        return email.trim().toLowerCase();
    }

    /**
     * Static method that normalize name to lower case
     * @param name Is user's name
     * @return      name in lower case
     */
    public static String normalizeName(String name){

        return name.trim().toLowerCase();
    }

    /**
     * Static method that normalize student's enrollment to lower case
     * @param enrollment Is the enrollment that every student has
     * @return      enrollment without the initial zeros
     */
    public static int normalizeEnrollment(String enrollment){
        String helper = enrollment.trim();
        helper = helper.replaceFirst("^0+", "");
        return Integer.parseInt(helper);
    }

    /**
     * Static method that normalize observation to lower case
     * @param obs Is the observation made by a teacher for a user
     * @return    observation in lower case
     */
    public static String normalizeObs(String obs){
        return obs.trim();
    }

    /**
     * Static method that normalize userName to lower case
     * @param userName Is the userName of the teachers
     * @return         userName in lower case
     */
    public static String normalizeUserName(String userName){
        return userName.trim().toLowerCase();
    }

    /**
     * Static method that normalize cpf to only have number
     * @param cpf Is the cpf of user
     * @return    The cf without . or -
     */
    public static String normalizeCpf(String cpf){
        return cpf.replaceAll("[^\\d]", "");
    }
}
