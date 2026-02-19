package com.example.schoolservlet.utils;

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
    private static String capitalizeOne(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }
    /**
     * Static method that capitalize the observation in the database
     * @param obs Is the observation in database
     * @return    The observation capitalized
     */
    public static String formatObs(String obs){
        if (obs.isEmpty()) return null;
        return capitalizeOne(obs);
    }

    /**
     * Static method that transform cpf with only numbers to a cpf with .and -
     * @param cpf Is the cpf stored in the database
     * @return    Is the cpf formatted
     */
    public static String formatCpf(String cpf){
        if (cpf.isEmpty()) return null;
        return cpf.replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }
}