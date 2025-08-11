package com.pahanaedu.bookshop.persistence.resource.resource.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    
    /**
     * Hash a password using BCrypt
     * @param password Plain text password
     * @return BCrypt hashed password
     */
    public static String hashPassword(String password) {
        // Generate salt and hash password with BCrypt
        // Using strength 12 for good security (2^12 = 4096 rounds)
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }
    
    /**
     * Verify a password against its BCrypt hash
     * @param password Plain text password to verify
     * @param hashedPassword BCrypt hashed password from database
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String password, String hashedPassword) {
        try {
            return BCrypt.checkpw(password, hashedPassword);
        } catch (Exception e) {
            // If there's any error in verification, return false for security
            System.err.println("Error verifying password: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Check if a password meets minimum security requirements
     * @param password Password to check
     * @return true if password meets requirements
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        
        // Additional password strength checks can be added here
        // For now, just checking minimum length
        return true;
    }
    
    /**
     * Generate a random password for testing purposes
     * @param length Length of password to generate
     * @return Random password string
     */
    public static String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder password = new StringBuilder();
        
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            password.append(chars.charAt(index));
        }
        
        return password.toString();
    }
}