package com.example.schoolservlet.utils;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import io.github.cdimascio.dotenv.Dotenv;

/**
 * Utility class for password hashing and verification using Argon2id.
 *
 * <p>A secret value loaded from environment configuration is appended to the
 * provided password before hashing/verifying.
 */
public class Argon {

    /**
     * Shared Argon2 instance configured with Argon2id.
     */
    private static final Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);

    /**
     * Dotenv instance used to load environment variables.
     */
    private static final Dotenv dotenv = Dotenv.configure()
                                            .ignoreIfMissing()
                                            .load();

    /**
     * Optional secret value appended to passwords before hashing/verifying.
     */
    private static final String secret = dotenv.get("SECRET") == null ? "" : dotenv.get("SECRET");

    /**
     * Hashes a password using Argon2id after appending the configured secret.
     *
     * @param password the plain password to hash
     * @return the generated Argon2 hash string
     */
    public static String hash(String password){
        password = password.concat(secret);

        char[] pwd = password.toCharArray();
        try {
            return argon2.hash(3, 65536, 1, pwd);
        } finally {
            argon2.wipeArray(pwd);
        }
    }

    /**
     * Verifies a plain password against an Argon2 hash after appending the configured secret.
     *
     * @param hash the stored Argon2 hash
     * @param password the plain password to verify
     * @return {@code true} if the password matches the hash; otherwise {@code false}
     */
    public static boolean verify(String hash, String password){
        password = password.concat(secret);

        char[] pwd = password.toCharArray();
        try {
            return argon2.verify(hash, pwd);
        } finally {
            argon2.wipeArray(pwd);
        }
    }
}