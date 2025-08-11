package com.pahanaedu.bookshop.resource.util;

import com.pahanaedu.bookshop.persistence.resource.resource.util.DatabaseConnection;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * Unit tests for DatabaseConnection (Singleton Pattern)
 */
public class DatabaseConnectionTest {
    
    private DatabaseConnection dbConnection;
    
    @Before
    public void setUp() {
        dbConnection = DatabaseConnection.getInstance();
    }
    
    @Test
    public void testSingletonPattern() {
        DatabaseConnection instance1 = DatabaseConnection.getInstance();
        DatabaseConnection instance2 = DatabaseConnection.getInstance();
        
        assertNotNull("Instance should not be null", instance1);
        assertNotNull("Instance should not be null", instance2);
        assertSame("Both instances should be the same", instance1, instance2);
    }
    
    @Test
    public void testGetPoolStatistics() {
        Map<String, Integer> stats = dbConnection.getPoolStatistics();
        
        assertNotNull("Statistics should not be null", stats);
        assertTrue("Should contain total connections", stats.containsKey("totalConnections"));
        assertTrue("Should contain available connections", stats.containsKey("availableConnections"));
        assertTrue("Should contain active connections", stats.containsKey("activeConnections"));
        assertTrue("Should contain max pool size", stats.containsKey("maxPoolSize"));
        assertTrue("Should contain min pool size", stats.containsKey("minPoolSize"));
        
        // Verify values are reasonable
        assertTrue("Total connections should be >= 0", stats.get("totalConnections") >= 0);
        assertTrue("Available connections should be >= 0", stats.get("availableConnections") >= 0);
        assertTrue("Active connections should be >= 0", stats.get("activeConnections") >= 0);
        assertTrue("Max pool size should be > 0", stats.get("maxPoolSize") > 0);
        assertTrue("Min pool size should be > 0", stats.get("minPoolSize") > 0);
    }
    
    @Test
    public void testConnectionPoolInitialization() {
        Map<String, Integer> stats = dbConnection.getPoolStatistics();
        
        // Should have minimum connections initialized
        assertTrue("Should have minimum connections", 
                  stats.get("totalConnections") >= stats.get("minPoolSize"));
    }
    
    @Test
    public void testConnectionReturnToPool() throws SQLException {
        Map<String, Integer> initialStats = dbConnection.getPoolStatistics();
        int initialAvailable = initialStats.get("availableConnections");
        int initialActive = initialStats.get("activeConnections");
        
        // Get a connection
        Connection conn = dbConnection.getConnection();
        assertNotNull("Connection should not be null", conn);
        
        Map<String, Integer> afterGetStats = dbConnection.getPoolStatistics();
        assertEquals("Available connections should decrease", 
                    initialAvailable - 1, (int) afterGetStats.get("availableConnections"));
        assertEquals("Active connections should increase", 
                    initialActive + 1, (int) afterGetStats.get("activeConnections"));
        
        // Return the connection
        dbConnection.returnConnection(conn);
        
        Map<String, Integer> afterReturnStats = dbConnection.getPoolStatistics();
        assertEquals("Available connections should increase", 
                    initialAvailable, (int) afterReturnStats.get("availableConnections"));
        assertEquals("Active connections should decrease", 
                    initialActive, (int) afterReturnStats.get("activeConnections"));
    }
    
    @Test
    public void testConnectionValidation() throws SQLException {
        Connection conn = dbConnection.getConnection();
        assertNotNull("Connection should not be null", conn);
        assertFalse("Connection should not be closed", conn.isClosed());
        assertTrue("Connection should be valid", conn.isValid(5));
        
        dbConnection.returnConnection(conn);
    }
    
    @Test
    public void testMultipleConnections() throws SQLException {
        Connection conn1 = dbConnection.getConnection();
        Connection conn2 = dbConnection.getConnection();
        
        assertNotNull("First connection should not be null", conn1);
        assertNotNull("Second connection should not be null", conn2);
        assertNotSame("Connections should be different", conn1, conn2);
        
        dbConnection.returnConnection(conn1);
        dbConnection.returnConnection(conn2);
    }
    
    @After
    public void tearDown() {
        // Don't set to null as it's a singleton
        // dbConnection = null;
    }
}