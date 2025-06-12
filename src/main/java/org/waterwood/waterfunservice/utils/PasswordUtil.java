package org.waterwood.waterfunservice.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static String encryptPassword(String password) {
        return encoder.encode(password);
    }

    public static boolean matchPassword(String password, String encodedPassword) {
        if (password == null || encodedPassword == null) {
            return false;
        }
        return encoder.matches(password, encodedPassword);
    }
}
