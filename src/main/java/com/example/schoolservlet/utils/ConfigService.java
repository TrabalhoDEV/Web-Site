package com.example.schoolservlet.utils;

import io.github.cdimascio.dotenv.Dotenv;

public class ConfigService {
    public static String getEnv(String variable, Dotenv dotenv){
        // Find variable in the system's variables
        String value = System.getenv(variable);
        if (value != null && !value.isBlank()) return value;

        // If not found, tries to find in .env file
        value = dotenv.get(variable);
        if (value != null && !value.isBlank()) return value;

        // Return null if the variable is not founded
        return null;
    }
}
