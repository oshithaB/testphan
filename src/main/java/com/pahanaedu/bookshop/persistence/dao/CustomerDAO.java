package com.pahanaedu.bookshop.persistence.dao;

import com.pahanaedu.bookshop.business.customer.model.Customer;
import com.pahanaedu.bookshop.persistence.resource.resource.factory.impl.CustomerFactoryImpl;
import com.pahanaedu.bookshop.persistence.resource.resource.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class CustomerDAO {
    private final DatabaseConnection dbConnection;
    private final CustomerFactoryImpl customerFactory;
    
    public CustomerDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.customerFactory = new CustomerFactoryImpl();
    }
    
    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE is_active = true ORDER BY name";
        
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    customers.add(mapResultSetToCustomer(rs));
                }
            }
        } finally {
            if (conn != null) {
                dbConnection.returnConnection(conn);
            }
        }
        return customers;
    }
    
    public Customer getCustomerById(int id) throws SQLException {
        String sql = "SELECT * FROM customers WHERE id = ?";
        
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    return mapResultSetToCustomer(rs);
                }
            }
        } finally {
            if (conn != null) {
                dbConnection.returnConnection(conn);
            }
        }
        return null;
    }
    
    public boolean addCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers (account_number, name, address, telephone, email) VALUES (?, ?, ?, ?, ?)";
        
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, customer.getAccountNumber());
                stmt.setString(2, customer.getName());
                stmt.setString(3, customer.getAddress());
                stmt.setString(4, customer.getTelephone());
                stmt.setString(5, customer.getEmail());
                
                return stmt.executeUpdate() > 0;
            }
        } finally {
            if (conn != null) {
                dbConnection.returnConnection(conn);
            }
        }
    }
    
    public boolean updateCustomer(Customer customer) throws SQLException {
        String sql = "UPDATE customers SET account_number = ?, name = ?, address = ?, telephone = ?, email = ? WHERE id = ?";
        
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, customer.getAccountNumber());
                stmt.setString(2, customer.getName());
                stmt.setString(3, customer.getAddress());
                stmt.setString(4, customer.getTelephone());
                stmt.setString(5, customer.getEmail());
                stmt.setInt(6, customer.getId());
                
                return stmt.executeUpdate() > 0;
            }
        } finally {
            if (conn != null) {
                dbConnection.returnConnection(conn);
            }
        }
    }
    
    public boolean deleteCustomer(int id) throws SQLException {
        String sql = "UPDATE customers SET is_active = false WHERE id = ?";
        
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                return stmt.executeUpdate() > 0;
            }
        } finally {
            if (conn != null) {
                dbConnection.returnConnection(conn);
            }
        }
    }
    
    public List<Customer> searchCustomers(String searchTerm) throws SQLException {
        List<Customer> customers = new ArrayList<>();
        
        // Use HashMap for search field mapping
        Map<String, String> searchFields = new HashMap<>();
        searchFields.put("name", "Customer Name");
        searchFields.put("account_number", "Account Number");
        searchFields.put("email", "Email Address");
        searchFields.put("telephone", "Phone Number");
        
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM customers WHERE is_active = true AND (");
        List<String> conditions = new ArrayList<>();
        
        for (String field : searchFields.keySet()) {
            conditions.add(field + " LIKE ?");
        }
        
        sqlBuilder.append(String.join(" OR ", conditions));
        sqlBuilder.append(") ORDER BY name");
        String sql = sqlBuilder.toString();
        
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                String searchPattern = "%" + searchTerm + "%";
                
                int paramIndex = 1;
                for (String field : searchFields.keySet()) {
                    stmt.setString(paramIndex++, searchPattern);
                }
                
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    customers.add(mapResultSetToCustomer(rs));
                }
            }
        } finally {
            if (conn != null) {
                dbConnection.returnConnection(conn);
            }
        }
        return customers;
    }
    
    public int getCustomerCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM customers WHERE is_active = true";
        
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } finally {
            if (conn != null) {
                dbConnection.returnConnection(conn);
            }
        }
        return 0;
    }
    
    public String generateAccountNumber() throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(account_number, 5) AS UNSIGNED)) FROM customers WHERE account_number LIKE 'CUST%'";
        
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                int nextNumber = 1;
                if (rs.next()) {
                    nextNumber = rs.getInt(1) + 1;
                }
                
                return String.format("CUST%03d", nextNumber);
            }
        } finally {
            if (conn != null) {
                dbConnection.returnConnection(conn);
            }
        }
    }
    
    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setId(rs.getInt("id"));
        customer.setAccountNumber(rs.getString("account_number"));
        customer.setName(rs.getString("name"));
        customer.setAddress(rs.getString("address"));
        customer.setTelephone(rs.getString("telephone"));
        customer.setEmail(rs.getString("email"));
        customer.setActive(rs.getBoolean("is_active"));
        customer.setCreatedAt(rs.getTimestamp("created_at"));
        customer.setUpdatedAt(rs.getTimestamp("updated_at"));
        return customer;
    }
}