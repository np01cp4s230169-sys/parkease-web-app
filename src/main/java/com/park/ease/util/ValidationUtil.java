package com.park.ease.util;

/**
 * ValidationUtil provides reusable input validation methods
 * used across the ParkEase system during registration, login,
 * profile updates, and form submissions.
 * 
 * All methods are static for easy access without instantiation.
 */
public class ValidationUtil {

    /**
     * Validates email format using standard email pattern.
     * Checks for valid characters, @ symbol, and domain structure.
     * 
     * @param email the email address to validate
     * @return true if email format is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    /**
     * Validates phone number format.
     * Accepts numeric phone numbers between 10 and 15 digits.
     * 
     * @param phone the phone number to validate
     * @return true if phone format is valid, false otherwise
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("\\d{10,15}");
    }

    /**
     * Validates that a name contains only letters and spaces.
     * Prevents numeric or special characters in name fields.
     * 
     * @param name the name to validate
     * @return true if name contains only letters and spaces, false otherwise
     */
    public static boolean isValidName(String name) {
        return name != null && name.matches("[a-zA-Z ]+");
    }

    /**
     * Checks that all provided string fields are non-null and non-empty.
     * Used to validate required form fields before processing.
     * 
     * @param fields one or more string values to check
     * @return true if all fields have content, false if any is null or empty
     */
    public static boolean isNotEmpty(String... fields) {
        for (String field : fields) {
            if (field == null || field.trim().isEmpty()) return false;
        }
        return true;
    }
}