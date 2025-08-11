package com.pahanaedu.bookshop.persistence.resource.resource.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

//Singleton Database Connection
//Uses Collections: Map (connection pool), Queue (available connections), Set (active connections)

public class DatabaseConnection {
    // Singleton instance
    private static volatile DatabaseConnection instance;
    private static final Object lock = new Object();
    
    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/pahana_bookshop";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "";
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String CONNECTION_PARAMS = "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    
    // Connection Pool using Collections
    private final Map<String, Connection> connectionPool; // Map for connection management
    private final Queue<Connection> availableConnections; // Queue for available connections
    private final Set<Connection> activeConnections; // Set for active connections tracking
    private final int maxPoolSize = 10;
    private final int minPoolSize = 3;
    
    // Private constructor for Singleton
    private DatabaseConnection() {
        // Initialize collections
        this.connectionPool = new ConcurrentHashMap<>();
        this.availableConnections = new LinkedBlockingQueue<>();
        this.activeConnections = Collections.synchronizedSet(new HashSet<>());
        
        initializeDriver();
        initializeConnectionPool();
    }
    
    /**
     * Get singleton instance using double-checked locking
     * @return DatabaseConnection instance
     */
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }
    
    /**
     * Initialize MySQL JDBC Driver
     */
    private void initializeDriver() {
        try {
            Class.forName(DB_DRIVER);
            System.out.println("MySQL JDBC Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }
    
    /**
     * Initialize connection pool with minimum connections
     */
    private void initializeConnectionPool() {
        try {
            for (int i = 0; i < minPoolSize; i++) {
                Connection conn = createNewConnection();
                String connectionId = "conn_" + i;
                connectionPool.put(connectionId, conn);
                availableConnections.offer(conn);
            }
            System.out.println("Connection pool initialized with " + minPoolSize + " connections");
        } catch (SQLException e) {
            System.err.println("Failed to initialize connection pool: " + e.getMessage());
        }
    }
    
    /**
     * Get database connection from pool
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        Connection connection = null;
        
        // Try to get from available connections queue
        synchronized (availableConnections) {
            connection = availableConnections.poll();
        }
        
        // If no available connection and pool not at max, create new one
        if (connection == null && connectionPool.size() < maxPoolSize) {
            connection = createNewConnection();
            String connectionId = "conn_" + connectionPool.size();
            connectionPool.put(connectionId, connection);
        }
        
        // If still no connection, create a new temporary connection
        if (connection == null) {
            System.out.println("Pool exhausted, creating temporary connection");
            connection = createNewConnection();
        }
        
        // Check if connection is still valid
        if (connection.isClosed() || !connection.isValid(5)) {
            connection = createNewConnection();
        }
        
        // Add to active connections set
        activeConnections.add(connection);
        
        return connection;
    }
    
    /**
     * Return connection to pool
     * @param connection Connection to return
     */
    public void returnConnection(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.isClosed() && connection.isValid(5)) {
                    // Remove from active connections set
                    activeConnections.remove(connection);
                    // Add back to available connections queue
                    availableConnections.offer(connection);
                } else {
                    // Connection is invalid, remove from pool
                    removeFromPool(connection);
                }
            } catch (SQLException e) {
                System.err.println("Error returning connection to pool: " + e.getMessage());
                removeFromPool(connection);
            }
        }
    }
    
    /**
     * Close database connection safely
     * @param connection Connection to close
     */
    public void closeConnection(Connection connection) {
        returnConnection(connection);
    }
    
    /**
     * Create new database connection
     * @return Connection object
     * @throws SQLException if connection fails
     */
    private Connection createNewConnection() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(
                DB_URL + CONNECTION_PARAMS, 
                DB_USERNAME, 
                DB_PASSWORD
            );
            connection.setAutoCommit(true);
            return connection;
        } catch (SQLException e) {
            System.err.println("Failed to create database connection!");
            System.err.println("URL: " + DB_URL);
            System.err.println("Username: " + DB_USERNAME);
            System.err.println("Error: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Remove connection from pool
     * @param connection Connection to remove
     */
    private void removeFromPool(Connection connection) {
        activeConnections.remove(connection);
        availableConnections.remove(connection);
        
        // Remove from connection pool map
        connectionPool.entrySet().removeIf(entry -> entry.getValue().equals(connection));
        
        try {
            if (!connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
    
    /**
     * Get pool statistics
     * @return Map containing pool statistics
     */
    public Map<String, Integer> getPoolStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("totalConnections", connectionPool.size());
        stats.put("availableConnections", availableConnections.size());
        stats.put("activeConnections", activeConnections.size());
        stats.put("maxPoolSize", maxPoolSize);
        stats.put("minPoolSize", minPoolSize);
        return stats;
    }
    
    /**
     * Test database connection
     * @return true if connection is successful
     */
    public boolean testConnection() {
        try (Connection connection = getConnection()) {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Shutdown connection pool
     */
    public void shutdown() {
        // Close all connections in the pool
        for (Connection conn : connectionPool.values()) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection during shutdown: " + e.getMessage());
            }
        }
        
        connectionPool.clear();
        availableConnections.clear();
        activeConnections.clear();
        
        System.out.println("Database connection pool shutdown completed");
    }
}