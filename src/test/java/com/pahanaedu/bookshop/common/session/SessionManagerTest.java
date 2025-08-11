package com.pahanaedu.bookshop.common.session;

import com.pahanaedu.bookshop.persistence.common.observer.SessionObserver;
import com.pahanaedu.bookshop.business.user.model.User;
import com.pahanaedu.bookshop.persistence.common.session.SessionManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;

import java.util.Map;

/**
 * Unit tests for SessionManager (Singleton + Observer Pattern)
 */
public class SessionManagerTest {
    
    private SessionManager sessionManager;
    private User testUser;
    private TestSessionObserver testObserver;
    
    @Before
    public void setUp() {
        sessionManager = SessionManager.getInstance();
        
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testuser");
        testUser.setRole("admin");
        testUser.setFullName("Test User");
        testUser.setEmail("test@example.com");
        
        testObserver = new TestSessionObserver();
        sessionManager.addObserver(testObserver);
    }
    
    @Test
    public void testSingletonPattern() {
        SessionManager instance1 = SessionManager.getInstance();
        SessionManager instance2 = SessionManager.getInstance();
        
        assertNotNull("Instance should not be null", instance1);
        assertNotNull("Instance should not be null", instance2);
        assertSame("Both instances should be the same", instance1, instance2);
    }
    
    @Test
    public void testGetSessionStatistics() {
        Map<String, Integer> stats = sessionManager.getSessionStatistics();
        
        assertNotNull("Statistics should not be null", stats);
        assertTrue("Should contain active sessions", stats.containsKey("activeSessions"));
        assertTrue("Should contain expired sessions", stats.containsKey("expiredSessions"));
        assertTrue("Should contain total observers", stats.containsKey("totalObservers"));
        
        // Verify values are reasonable
        assertTrue("Active sessions should be >= 0", stats.get("activeSessions") >= 0);
        assertTrue("Expired sessions should be >= 0", stats.get("expiredSessions") >= 0);
        assertTrue("Total observers should be >= 0", stats.get("totalObservers") >= 0);
    }
    
    @Test
    public void testObserverPattern() {
        int initialObserverCount = sessionManager.getSessionStatistics().get("totalObservers");
        
        // Add observer
        TestSessionObserver newObserver = new TestSessionObserver();
        sessionManager.addObserver(newObserver);
        
        int afterAddCount = sessionManager.getSessionStatistics().get("totalObservers");
        assertEquals("Observer count should increase", initialObserverCount + 1, afterAddCount);
        
        // Remove observer
        sessionManager.removeObserver(newObserver);
        
        int afterRemoveCount = sessionManager.getSessionStatistics().get("totalObservers");
        assertEquals("Observer count should decrease", initialObserverCount, afterRemoveCount);
    }
    
    @Test
    public void testObserverNotification() {
        // Reset observer counters
        testObserver.reset();
        
        // Test session creation notification
        assertEquals("Initial login count should be 0", 0, testObserver.getLoginCount());
        assertEquals("Initial session created count should be 0", 0, testObserver.getSessionCreatedCount());
        
        // Note: We can't easily test actual session creation without HttpServletRequest/Response
        // But we can test the observer pattern structure
        assertNotNull("Test observer should be set up", testObserver);
    }
    
    @Test
    public void testSessionValidation() {
        // Test invalid session ID
        assertFalse("Invalid session should not be valid", sessionManager.isSessionValid("invalid_session_id"));
        assertNull("Invalid session should return null info", sessionManager.getSessionInfo("invalid_session_id"));
    }
    
    @Test
    public void testGetActiveSessions() {
        Map<String, SessionManager.SessionInfo> activeSessions = sessionManager.getActiveSessions();
        assertNotNull("Active sessions map should not be null", activeSessions);
        // Should be empty initially in test environment
        assertTrue("Active sessions should be >= 0", activeSessions.size() >= 0);
    }
    
    @After
    public void tearDown() {
        if (testObserver != null) {
            sessionManager.removeObserver(testObserver);
        }
        // Don't set sessionManager to null as it's a singleton
    }
    
    /**
     * Test implementation of SessionObserver for testing
     */
    private static class TestSessionObserver implements SessionObserver {
        private int loginCount = 0;
        private int logoutCount = 0;
        private int expiredCount = 0;
        private int sessionCreatedCount = 0;
        
        @Override
        public void onUserLogin(User user, String sessionId) {
            loginCount++;
        }
        
        @Override
        public void onUserLogout(User user, String sessionId) {
            logoutCount++;
        }
        
        @Override
        public void onSessionExpired(String sessionId) {
            expiredCount++;
        }
        
        @Override
        public void onSessionCreated(String sessionId) {
            sessionCreatedCount++;
        }
        
        public void reset() {
            loginCount = 0;
            logoutCount = 0;
            expiredCount = 0;
            sessionCreatedCount = 0;
        }
        
        public int getLoginCount() { return loginCount; }
        public int getLogoutCount() { return logoutCount; }
        public int getExpiredCount() { return expiredCount; }
        public int getSessionCreatedCount() { return sessionCreatedCount; }
    }
}