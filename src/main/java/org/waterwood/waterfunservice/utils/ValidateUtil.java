package org.waterwood.waterfunservice.utils;

public class ValidateUtil {
    private static final int MIN_PASSWORD_LENGTH = 8;

    public static boolean validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        // Basic regex for email validation
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }

    public static boolean validatePhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        // Adjust regex as per your phone number format
        String phoneRegex = "^1[3-9]\\d{9}$";
        return phone.matches(phoneRegex);
    }

    public static boolean validateUsername(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }
        // Username must be alphanumeric and can include underscores, 3-20 characters
        String usernameRegex = "^[a-zA-Z0-9_]{3,20}$";
        return username.matches(usernameRegex);
    }

    /**
     * Basic password validate
     * check length > 8
     * check string has two case of character
     * check string whether it has digital
     * @param password original password
     * @return whether the password is valid
     */
    public static boolean validateBasicPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }

        if (password.length() <= MIN_PASSWORD_LENGTH) {
            return false;
        }

        boolean hasUpper = false, hasLower = false, hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpper = true;
            } else if (Character.isLowerCase(c)) {
                hasLower = true;
            } else if(Character.isDigit(c)){
                hasDigit = true;
            }
            if (hasUpper && hasLower && hasDigit) {
                break;
            }
        }

        return hasUpper && hasLower && hasDigit;
    }

    /**
     * Validate Strong password
     * check length > 12
     * check string whether it has two cases.
     * check string whether it has digit.
     * check string whether it has special
     * @param password original password
     * @return whether the password is valid.
     */
    public static boolean validateStrongPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }

        if (password.length() < 12) {
            return false;
        }

        boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpper = true;
            } else if (Character.isLowerCase(c)) {
                hasLower = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecial = true;
            }
            if (hasUpper && hasLower && hasDigit && hasSpecial) {
                break;
            }
        }

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
}
