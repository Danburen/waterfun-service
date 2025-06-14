package org.waterwood.waterfunservice.utils;

public class ValidateUtil {
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
        String phoneRegex = "^\\+?[0-9]{10,15}$";
        return phone.matches(phoneRegex);
    }
}
