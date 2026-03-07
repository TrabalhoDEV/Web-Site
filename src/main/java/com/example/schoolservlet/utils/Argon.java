package com.example.schoolservlet.utils;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import io.github.cdimascio.dotenv.Dotenv;


public class Argon {

    private static final Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
    private static final Dotenv dotenv = Dotenv.configure()
                                            .ignoreIfMissing()
                                            .load();
    private static final String secret = dotenv.get("SECRET") == null?"":dotenv.get("SECRET");

    public static String hash(String password){
        password = password.concat(secret);

        char[] pwd = password.toCharArray();
        try {
            return argon2.hash(3, 65536, 1, pwd);
        } finally {
            argon2.wipeArray(pwd);
        }
    }

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