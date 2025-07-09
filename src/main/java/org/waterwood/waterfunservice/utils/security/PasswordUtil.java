package org.waterwood.waterfunservice.utils.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static String encryptPassword(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    public static boolean matchPassword(String password, String encodedPassword) {
        if (password == null || encodedPassword == null) {
            return false;
        }
        return encoder.matches(password, encodedPassword);
    }
}
