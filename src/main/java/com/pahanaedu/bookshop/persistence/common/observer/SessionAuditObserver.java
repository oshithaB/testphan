package com.pahanaedu.bookshop.persistence.common.observer;

import com.pahanaedu.bookshop.business.user.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


 //Implements Observer Pattern

public class SessionAuditObserver implements SessionObserver {
    
    // Collections for audit tracking
    private final List<AuditEvent> auditLog; // List for audit events
    private final Map<String, Integer> sessionCounts; // Map for session statistics
    
    public SessionAuditObserver() {
        this.auditLog = new ArrayList<>();
        this.sessionCounts = new ConcurrentHashMap<>();
        
        // Initialize counters
        sessionCounts.put("logins", 0);
        sessionCounts.put("logouts", 0);
        sessionCounts.put("expired", 0);
        sessionCounts.put("created", 0);
    }
    
    @Override
    public void onUserLogin(User user, String sessionId) {
        AuditEvent event = new AuditEvent("USER_LOGIN", user.getUsername(), sessionId, System.currentTimeMillis());
        auditLog.add(event);
        sessionCounts.put("logins", sessionCounts.get("logins") + 1);
        
        System.out.println("AUDIT: User " + user.getUsername() + " logged in with session " + sessionId);
    }
    
    @Override
    public void onUserLogout(User user, String sessionId) {
        AuditEvent event = new AuditEvent("USER_LOGOUT", user.getUsername(), sessionId, System.currentTimeMillis());
        auditLog.add(event);
        sessionCounts.put("logouts", sessionCounts.get("logouts") + 1);
        
        System.out.println("AUDIT: User " + user.getUsername() + " logged out from session " + sessionId);
    }
    
    @Override
    public void onSessionExpired(String sessionId) {
        AuditEvent event = new AuditEvent("SESSION_EXPIRED", "SYSTEM", sessionId, System.currentTimeMillis());
        auditLog.add(event);
        sessionCounts.put("expired", sessionCounts.get("expired") + 1);
        
        System.out.println("AUDIT: Session " + sessionId + " expired");
    }
    
    @Override
    public void onSessionCreated(String sessionId) {
        AuditEvent event = new AuditEvent("SESSION_CREATED", "SYSTEM", sessionId, System.currentTimeMillis());
        auditLog.add(event);
        sessionCounts.put("created", sessionCounts.get("created") + 1);
        
        System.out.println("AUDIT: Session " + sessionId + " created");
    }
    
    /**
     * Get audit log
     * @return List of audit events
     */
    public List<AuditEvent> getAuditLog() {
        return new ArrayList<>(auditLog);
    }
    
    /**
     * Get session statistics
     * @return Map of session counts
     */
    public Map<String, Integer> getSessionCounts() {
        return new ConcurrentHashMap<>(sessionCounts);
    }
    
    /**
     * Clear audit log
     */
    public void clearAuditLog() {
        auditLog.clear();
    }
    
    /**
     * Audit event data holder
     */
    public static class AuditEvent {
        private final String eventType;
        private final String username;
        private final String sessionId;
        private final long timestamp;
        
        public AuditEvent(String eventType, String username, String sessionId, long timestamp) {
            this.eventType = eventType;
            this.username = username;
            this.sessionId = sessionId;
            this.timestamp = timestamp;
        }
        
        // Getters
        public String getEventType() { return eventType; }
        public String getUsername() { return username; }
        public String getSessionId() { return sessionId; }
        public long getTimestamp() { return timestamp; }
        
        @Override
        public String toString() {
            return String.format("[%s] %s - User: %s, Session: %s, Time: %d", 
                eventType, eventType, username, sessionId, timestamp);
        }
    }
}