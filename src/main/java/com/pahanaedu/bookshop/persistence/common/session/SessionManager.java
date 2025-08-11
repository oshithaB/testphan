package com.pahanaedu.bookshop.persistence.common.session;

import com.pahanaedu.bookshop.persistence.common.observer.SessionObserver;
import com.pahanaedu.bookshop.business.user.model.User;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

  // Session Manager with Observer Pattern

public class SessionManager {
    
    // Singleton instance
    private static volatile SessionManager instance;
    private static final Object lock = new Object();
    
    // Collections for session management
    private final Map<String, SessionInfo> activeSessions; // Map for session storage
    private final List<SessionObserver> observers; // List for observers
    private final Set<String> expiredSessions; // Set for expired session tracking
    
    // Session configuration
    private static final int DEFAULT_SESSION_TIMEOUT = 30 * 60; // 30 minutes in seconds
    private static final String SESSION_COOKIE_NAME = "BOOKSHOP_SESSION";
    
    private SessionManager() {
        this.activeSessions = new ConcurrentHashMap<>();
        this.observers = Collections.synchronizedList(new ArrayList<>());
        this.expiredSessions = Collections.synchronizedSet(new HashSet<>());
    }
    
    /**
     * Get singleton instance
     * @return SessionManager instance
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new SessionManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * Create new session for user
     * @param request HTTP request
     * @param response HTTP response
     * @param user logged in user
     * @return session ID
     */
    public String createSession(HttpServletRequest request, HttpServletResponse response, User user) {
        HttpSession httpSession = request.getSession(true);
        String sessionId = httpSession.getId();
        
        // Create session info
        SessionInfo sessionInfo = new SessionInfo(sessionId, user, System.currentTimeMillis());
        activeSessions.put(sessionId, sessionInfo);
        
        // Set session attributes
        httpSession.setAttribute("user", user);
        httpSession.setAttribute("userRole", user.getRole());
        httpSession.setAttribute("userName", user.getFullName());
        httpSession.setMaxInactiveInterval(DEFAULT_SESSION_TIMEOUT);
        
        // Create session cookie
        Cookie sessionCookie = new Cookie(SESSION_COOKIE_NAME, sessionId);
        sessionCookie.setMaxAge(DEFAULT_SESSION_TIMEOUT);
        sessionCookie.setPath("/");
        sessionCookie.setHttpOnly(true);
        response.addCookie(sessionCookie);
        
        // Notify observers
        notifyUserLogin(user, sessionId);
        notifySessionCreated(sessionId);
        
        return sessionId;
    }
    
    /**
     * Invalidate session
     * @param request HTTP request
     * @param response HTTP response
     */
    public void invalidateSession(HttpServletRequest request, HttpServletResponse response) {
        HttpSession httpSession = request.getSession(false);
        if (httpSession != null) {
            String sessionId = httpSession.getId();
            SessionInfo sessionInfo = activeSessions.get(sessionId);
            
            // Remove from active sessions
            activeSessions.remove(sessionId);
            
            // Add to expired sessions
            expiredSessions.add(sessionId);
            
            // Invalidate HTTP session
            httpSession.invalidate();
            
            // Remove session cookie
            Cookie sessionCookie = new Cookie(SESSION_COOKIE_NAME, "");
            sessionCookie.setMaxAge(0);
            sessionCookie.setPath("/");
            response.addCookie(sessionCookie);
            
            // Notify observers
            if (sessionInfo != null) {
                notifyUserLogout(sessionInfo.getUser(), sessionId);
            }
        }
    }
    
    /**
     * Get session info by session ID
     * @param sessionId session ID
     * @return SessionInfo or null if not found
     */
    public SessionInfo getSessionInfo(String sessionId) {
        return activeSessions.get(sessionId);
    }
    
    /**
     * Check if session is valid
     * @param sessionId session ID
     * @return true if valid, false otherwise
     */
    public boolean isSessionValid(String sessionId) {
        SessionInfo sessionInfo = activeSessions.get(sessionId);
        if (sessionInfo == null) {
            return false;
        }
        
        // Check if session has expired
        long currentTime = System.currentTimeMillis();
        long sessionAge = currentTime - sessionInfo.getCreatedTime();
        
        if (sessionAge > (DEFAULT_SESSION_TIMEOUT * 1000)) {
            // Session expired
            activeSessions.remove(sessionId);
            expiredSessions.add(sessionId);
            notifySessionExpired(sessionId);
            return false;
        }
        
        // Update last access time
        sessionInfo.setLastAccessTime(currentTime);
        return true;
    }
    
    /**
     * Get all active sessions
     * @return Map of active sessions
     */
    public Map<String, SessionInfo> getActiveSessions() {
        return new HashMap<>(activeSessions);
    }
    
    /**
     * Get session statistics
     * @return Map containing session statistics
     */
    public Map<String, Integer> getSessionStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("activeSessions", activeSessions.size());
        stats.put("expiredSessions", expiredSessions.size());
        stats.put("totalObservers", observers.size());
        return stats;
    }
    
    /**
     * Add session observer
     * @param observer observer to add
     */
    public void addObserver(SessionObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    /**
     * Remove session observer
     * @param observer observer to remove
     */
    public void removeObserver(SessionObserver observer) {
        observers.remove(observer);
    }
    
    /**
     * Notify observers of user login
     * @param user logged in user
     * @param sessionId session ID
     */
    private void notifyUserLogin(User user, String sessionId) {
        for (SessionObserver observer : observers) {
            try {
                observer.onUserLogin(user, sessionId);
            } catch (Exception e) {
                System.err.println("Error notifying observer of user login: " + e.getMessage());
            }
        }
    }
    
    /**
     * Notify observers of user logout
     * @param user logged out user
     * @param sessionId session ID
     */
    private void notifyUserLogout(User user, String sessionId) {
        for (SessionObserver observer : observers) {
            try {
                observer.onUserLogout(user, sessionId);
            } catch (Exception e) {
                System.err.println("Error notifying observer of user logout: " + e.getMessage());
            }
        }
    }
    
    /**
     * Notify observers of session expiration
     * @param sessionId expired session ID
     */
    private void notifySessionExpired(String sessionId) {
        for (SessionObserver observer : observers) {
            try {
                observer.onSessionExpired(sessionId);
            } catch (Exception e) {
                System.err.println("Error notifying observer of session expiration: " + e.getMessage());
            }
        }
    }
    
    /**
     * Notify observers of session creation
     * @param sessionId new session ID
     */
    private void notifySessionCreated(String sessionId) {
        for (SessionObserver observer : observers) {
            try {
                observer.onSessionCreated(sessionId);
            } catch (Exception e) {
                System.err.println("Error notifying observer of session creation: " + e.getMessage());
            }
        }
    }
    
    /**
     * Clean up expired sessions
     */
    public void cleanupExpiredSessions() {
        long currentTime = System.currentTimeMillis();
        List<String> expiredSessionIds = new ArrayList<>();
        
        for (Map.Entry<String, SessionInfo> entry : activeSessions.entrySet()) {
            SessionInfo sessionInfo = entry.getValue();
            long sessionAge = currentTime - sessionInfo.getLastAccessTime();
            
            if (sessionAge > (DEFAULT_SESSION_TIMEOUT * 1000)) {
                expiredSessionIds.add(entry.getKey());
            }
        }
        
        // Remove expired sessions
        for (String sessionId : expiredSessionIds) {
            activeSessions.remove(sessionId);
            expiredSessions.add(sessionId);
            notifySessionExpired(sessionId);
        }
    }
    
    /**
     * Session information holder
     */
    public static class SessionInfo {
        private final String sessionId;
        private final User user;
        private final long createdTime;
        private long lastAccessTime;
        
        public SessionInfo(String sessionId, User user, long createdTime) {
            this.sessionId = sessionId;
            this.user = user;
            this.createdTime = createdTime;
            this.lastAccessTime = createdTime;
        }
        
        // Getters and setters
        public String getSessionId() { return sessionId; }
        public User getUser() { return user; }
        public long getCreatedTime() { return createdTime; }
        public long getLastAccessTime() { return lastAccessTime; }
        public void setLastAccessTime(long lastAccessTime) { this.lastAccessTime = lastAccessTime; }
    }
}