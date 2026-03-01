package com.example.schoolservlet.utils;

import com.example.schoolservlet.utils.enums.StudentStatusEnum;

import java.util.Date;

/**
 * Public class that has static methods to format the data in the database to the output.
 */
public class OutputFormatService {
    /**
     * Static method that makes the name in database to capitalize
     * @param name Is the student or teacher name
     * @return     name with all parts capitalized
     */
    public static String formatName(String name){
        if (name.isEmpty()) return null;
        name = escapeHtml(name);

        String[] nameDivided = name.split("\\s+");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < nameDivided.length; i++){
            result.append(capitalizeOne(nameDivided[i]));

            if (i < nameDivided.length - 1){
                result.append(" ");
            }
        }

        return result.toString();
    }

    /**
     * Private static method that capitalize a word, it's a helper in this class
     * @param word Is the word to capitalize
     * @return     The word capitalized
     */
    private static String capitalizeOne(String word){
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }
    /**
     * Static method that capitalize the observation in the database
     * @param obs Is the observation in database
     * @return    The observation capitalized
     */
    public static String formatObs(String obs){
        if (obs.isEmpty()) return null;
        obs = escapeHtml(obs);
        return capitalizeOne(obs);
    }

    /**
     * Static method that transform cpf with only numbers to a cpf with .and -
     * @param cpf Is the cpf stored in the database
     * @return    Is the cpf formatted
     */
    public static String formatCpf(String cpf){
        if (cpf.isEmpty()) return null;
        cpf = escapeHtml(cpf);
        return cpf.replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }

    /**
     * Static method that transform date to string
     * @param date Is the date stored in the database
     * @return    Is the date formatted to string day/month
     */
    public static String formatDate(Date date) {
        if (date == null) return "-";
        return new java.text.SimpleDateFormat("yyyy/MM/dd").format(date);
    }

    /**
     * Static method that transform student status to a portuguese string
     * @param status is the status stored in the database
     * @return       is a string representing the status in portuguese
     */
    public static String formatStudentStatus(StudentStatusEnum status) {
        return status == StudentStatusEnum.ACTIVE ? "Ativo" : "Inativo";
    }

    /**
     * Static method that replaces possible js injections in output
     * @param input is the data return from servlet
     * @return      data normalize
     */
    public static String escapeHtml(String input) {
        if (input == null) return "";
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}