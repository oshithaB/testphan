package com.pahanaedu.bookshop.persistence.resource.resource.util;

public class EmailUtil {
    
    public static boolean sendEmail(String to, String subject, String body) {
        // Simulate email sending
        System.out.println("=== EMAIL SENT ===");
        System.out.println("To: " + to);
        System.out.println("Subject: " + subject);
        System.out.println("Body: " + body);
        System.out.println("==================");
        
        // In a real application, implement actual email sending logic here
        return true;
    }
    
    public static void sendWelcomeEmail(String userEmail, String username) {
        String subject = "Welcome to Pahana Edu Book Shop";
        String body = "Dear " + username + ",\n\n" +
                     "Welcome to Pahana Edu Book Shop management system.\n" +
                     "Your account has been created successfully.\n\n" +
                     "Best regards,\n" +
                     "Pahana Edu Book Shop Team";
        
        sendEmail(userEmail, subject, body);
    }
    
    public static void sendBillEmail(String customerEmail, String billNumber, double totalAmount) {
        String subject = "Bill Confirmation - " + billNumber;
        String body = "Dear Customer,\n\n" +
                     "Thank you for your purchase.\n" +
                     "Bill Number: " + billNumber + "\n" +
                     "Total Amount: Rs. " + String.format("%.2f", totalAmount) + "\n\n" +
                     "Best regards,\n" +
                     "Pahana Edu Book Shop";
        
        sendEmail(customerEmail, subject, body);
    }
}