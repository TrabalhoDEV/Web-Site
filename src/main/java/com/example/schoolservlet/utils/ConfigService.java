package com.example.schoolservlet.utils;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Utility service for retrieving configuration values from environment sources.
 *
 * <p>Lookup order:
 * <ol>
 *   <li>System environment variables</li>
 *   <li>Provided {@link Dotenv} instance</li>
 * </ol>
 */
public class ConfigService {
    /**
     * Returns a configuration value for the given variable name.
     *
     * <p>The method first checks system environment variables. If not found (or blank),
     * it then checks the provided {@code dotenv} instance. If no non-blank value is found,
     * it returns {@code null}.
     *
     * @param variable the environment variable key to search for
     * @param dotenv the dotenv instance used as fallback (may be {@code null})
     * @return the resolved non-blank value, or {@code null} if not found
     */
    public static String getEnv(String variable, Dotenv dotenv){
        // Find variable in the system environment
        String value = System.getenv(variable);
        if (value != null && !value.isBlank()) return value;

        // If not found, try .env file
        if (dotenv != null) {
            value = dotenv.get(variable);
            if (value != null && !value.isBlank()) return value;
        }

        // Return null if the variable is not found
        return null;
    }
}