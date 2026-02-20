package com.example.schoolservlet.utils;

import com.example.schoolservlet.exceptions.TransformTypeException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *  Class that contains static methods to normalize the user's input to the database format.
 *  This class allows us to change the business' rule easier than if we keep that validation in
 *  other classes like DAOs or Servlets.
 */
public class InputNormalizer {
    /**
     * Static method that normalizes e-mail to lower case
     * @param email Is user's e-mail
     * @return      e-mail in lower case
     */
    public static String normalizeEmail(String email){
        if (email == null || email.isEmpty()) return null;
        return email.trim().toLowerCase();
    }

    /**
     * Static method that normalizes name to lower case
     * @param name Is user's name
     * @return      name in lower case
     */
    public static String normalizeName(String name){
        if (name == null || name.isEmpty()) return null;
        return name.trim().toLowerCase();
    }

    /**
     * Static method that normalizes student's enrollment to lower case
     * @param enrollment Is the enrollment that every student has
     * @return      enrollment without the initial zeros
     */
    public static int normalizeEnrollment(String enrollment){
        if (enrollment == null || enrollment.isEmpty()) return -1;
        String helper = enrollment.trim();
        helper = helper.replaceFirst("^0+", "");
        return Integer.parseInt(helper);
    }

    /**
     * Static method that normalizes observation to lower case
     * @param obs is the observation made by a teacher for some user
     * @return    observation in lower case
     */
    public static String normalizeObs(String obs){
        if (obs == null || obs.isEmpty()) return null;
        return obs.trim();
    }

    /**
     * Static method that normalizes userName to lower case
     * @param userName is teacher's userName
     * @return         userName in lower case
     */
    public static String normalizeUserName(String userName){
        if (userName == null || userName.isEmpty()) return null;
        return userName.trim().toLowerCase();
    }

    /**
     * Static method that normalizes cpf to only have number
     * @param cpf is the cpf of user
     * @return    cpf without . or -
     */
    public static String normalizeCpf(String cpf){
        if (cpf == null || cpf.isEmpty()) return null;
        return cpf.replaceAll("[^\\d]", "");
    }

    public static Date normalizeDate(String date) throws TransformTypeException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(date);
        } catch (ParseException e){
            e.printStackTrace();
            throw new TransformTypeException("data limite", "data (ano-mês-dia)");
        }
    }
}
