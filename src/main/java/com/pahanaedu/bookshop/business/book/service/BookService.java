package com.pahanaedu.bookshop.business.book.service;

import com.pahanaedu.bookshop.business.book.model.Book;
import com.pahanaedu.bookshop.persistence.resource.resource.factory.impl.BookFactoryImpl;
import com.pahanaedu.bookshop.persistence.resource.resource.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * Service layer for Book operations
 * Uses Collections: List, Map
 */
public class BookService {
    private final DatabaseConnection dbConnection;
    private final BookFactoryImpl bookFactory;
    
    public BookService() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.bookFactory = new BookFactoryImpl();
    }
    
    public List<Book> getAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE is_active = true ORDER BY title";
        
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }
        } finally {
            if (conn != null) {
                dbConnection.returnConnection(conn);
            }
        }
        return books;
    }
    
    public Book getBookById(int id) throws SQLException {
        String sql = "SELECT * FROM books WHERE id = ?";
        
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    return mapResultSetToBook(rs);
                }
            }
        } finally {
            if (conn != null) {
                dbConnection.returnConnection(conn);
            }
        }
        return null;
    }
    
    public boolean addBook(Book book) throws SQLException {
        String sql = "INSERT INTO books (title, author, isbn, category, price, quantity, description) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, book.getTitle());
                stmt.setString(2, book.getAuthor());
                stmt.setString(3, book.getIsbn());
                stmt.setString(4, book.getCategory());
                stmt.setBigDecimal(5, book.getPrice());
                stmt.setInt(6, book.getQuantity());
                stmt.setString(7, book.getDescription());
                
                return stmt.executeUpdate() > 0;
            }
        } finally {
            if (conn != null) {
                dbConnection.returnConnection(conn);
            }
        }
    }
    
    public boolean updateBook(Book book) throws SQLException {
        String sql = "UPDATE books SET title = ?, author = ?, isbn = ?, category = ?, price = ?, quantity = ?, description = ? WHERE id = ?";
        
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, book.getTitle());
                stmt.setString(2, book.getAuthor());
                stmt.setString(3, book.getIsbn());
                stmt.setString(4, book.getCategory());
                stmt.setBigDecimal(5, book.getPrice());
                stmt.setInt(6, book.getQuantity());
                stmt.setString(7, book.getDescription());
                stmt.setInt(8, book.getId());
                
                return stmt.executeUpdate() > 0;
            }
        } finally {
            if (conn != null) {
                dbConnection.returnConnection(conn);
            }
        }
    }
    
    public boolean deleteBook(int id) throws SQLException {
        String sql = "UPDATE books SET is_active = false WHERE id = ?";
        
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
    
    public List<Book> searchBooks(String searchTerm) throws SQLException {
        List<Book> books = new ArrayList<>();
        
        // Use Map for search field mapping
        Map<String, String> searchFields = new HashMap<>();
        searchFields.put("title", "Book Title");
        searchFields.put("author", "Author Name");
        searchFields.put("isbn", "ISBN Number");
        searchFields.put("category", "Category");
        
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM books WHERE is_active = true AND (");
        List<String> conditions = new ArrayList<>();
        
        for (String field : searchFields.keySet()) {
            conditions.add(field + " LIKE ?");
        }
        
        sqlBuilder.append(String.join(" OR ", conditions));
        sqlBuilder.append(") ORDER BY title");
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
                    books.add(mapResultSetToBook(rs));
                }
            }
        } finally {
            if (conn != null) {
                dbConnection.returnConnection(conn);
            }
        }
        return books;
    }
    
    public int getBookCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM books WHERE is_active = true";
        
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
    
    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getInt("id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setIsbn(rs.getString("isbn"));
        book.setCategory(rs.getString("category"));
        book.setPrice(rs.getBigDecimal("price"));
        book.setQuantity(rs.getInt("quantity"));
        book.setDescription(rs.getString("description"));
        book.setActive(rs.getBoolean("is_active"));
        book.setCreatedAt(rs.getTimestamp("created_at"));
        book.setUpdatedAt(rs.getTimestamp("updated_at"));
        return book;
    }
}