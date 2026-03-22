package com.example.schoolservlet.utils;

import com.example.schoolservlet.exceptions.TransformTypeException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class with static methods to normalize user input into formats
 * expected by persistence and business layers.
 *
 * <p>Centralizing normalization rules simplifies maintenance and avoids
 * duplicating formatting logic across DAOs and servlets.
 */
public class InputNormalizer {
    /**
     * Normalizes an email by trimming it and converting it to lowercase.
     *
     * @param email the user's email
     * @return normalized email in lowercase, or {@code null} if input is null/empty
     */
    public static String normalizeEmail(String email){
        if (email == null || email.isEmpty()) return null;
        return email.trim().toLowerCase();
    }

    /**
     * Normalizes a name by trimming it and converting it to lowercase.
     *
     * @param name the user's name
     * @return normalized name in lowercase, or {@code null} if input is null/empty
     */
    public static String normalizeName(String name){
        if (name == null || name.isEmpty()) return null;
        return name.trim().toLowerCase();
    }

    /**
     * Normalizes a student enrollment string by trimming and removing leading zeros.
         *
     * @param enrollment the student's enrollment value
     * @return enrollment as integer without leading zeros, or {@code -1} if input is null/empty
     */
    public static int normalizeEnrollment(String enrollment){
        if (enrollment == null || enrollment.isEmpty()) return -1;
        String helper = enrollment.trim();
        helper = helper.replaceFirst("^0+", "");
        return Integer.parseInt(helper);
    }

    /**
     * Normalizes an observation by trimming surrounding whitespace.
     *
     * @param obs the observation text
     * @return trimmed observation, or {@code null} if input is null/empty
     */
    public static String normalizeObs(String obs){
        if (obs == null || obs.isEmpty()) return null;
        return obs.trim();
    }

    /**
     * Normalizes a teacher username by trimming it and converting it to lowercase.
     *
     * @param userName the teacher username
     * @return normalized username in lowercase, or {@code null} if input is null/empty
     */
    public static String normalizeUserName(String userName){
        if (userName == null || userName.isEmpty()) return null;
        return userName.trim().toLowerCase();
    }

    /**
     * Normalizes a CPF by removing all non-digit characters.
     *
     * @param cpf the user's CPF
     * @return CPF containing digits only, or {@code null} if input is null/empty
     */
    public static String normalizeCpf(String cpf){
        if (cpf == null || cpf.isEmpty()) return null;
        return cpf.replaceAll("[^\\d]", "");
    }

    /**
     * Parses a date string in {@code yyyy-MM-dd} format.
     *
     * @param date the date string to parse
     * @return parsed {@link Date}
     * @throws TransformTypeException if the input cannot be parsed to the expected date format
     */
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