package com.pahanaedu.bookshop.persistence.dao;

import com.pahanaedu.bookshop.business.bill.bill.model.Bill;
import com.pahanaedu.bookshop.business.bill.bill.model.BillItem;
import com.pahanaedu.bookshop.persistence.resource.resource.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class BillDAO {
    private final DatabaseConnection dbConnection;
    
    public BillDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    public boolean createBill(Bill bill, List<BillItem> billItems) throws SQLException {
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Insert bill
            String billSql = "INSERT INTO bills (bill_number, customer_id, cashier_id, subtotal, tax_amount, discount_amount, total_amount) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement billStmt = conn.prepareStatement(billSql, Statement.RETURN_GENERATED_KEYS);
            billStmt.setString(1, bill.getBillNumber());
            billStmt.setInt(2, bill.getCustomerId());
            billStmt.setInt(3, bill.getCashierId());
            billStmt.setBigDecimal(4, bill.getSubtotal());
            billStmt.setBigDecimal(5, bill.getTaxAmount());
            billStmt.setBigDecimal(6, bill.getDiscountAmount());
            billStmt.setBigDecimal(7, bill.getTotalAmount());
            
            int billResult = billStmt.executeUpdate();
            if (billResult == 0) {
                conn.rollback();
                return false;
            }
            
            ResultSet generatedKeys = billStmt.getGeneratedKeys();
            int billId = 0;
            if (generatedKeys.next()) {
                billId = generatedKeys.getInt(1);
            }
            
            // Insert bill items
            String itemSql = "INSERT INTO bill_items (bill_id, book_id, quantity, unit_price, tax_rate, discount_rate, tax_amount, discount_amount, line_total) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement itemStmt = conn.prepareStatement(itemSql);
            
            for (BillItem item : billItems) {
                itemStmt.setInt(1, billId);
                itemStmt.setInt(2, item.getBookId());
                itemStmt.setInt(3, item.getQuantity());
                itemStmt.setBigDecimal(4, item.getUnitPrice());
                itemStmt.setBigDecimal(5, item.getTaxRate());
                itemStmt.setBigDecimal(6, item.getDiscountRate());
                itemStmt.setBigDecimal(7, item.getTaxAmount());
                itemStmt.setBigDecimal(8, item.getDiscountAmount());
                itemStmt.setBigDecimal(9, item.getLineTotal());
                itemStmt.addBatch();
                
                // Update book quantity
                String updateBookSql = "UPDATE books SET quantity = quantity - ? WHERE id = ?";
                PreparedStatement updateBookStmt = conn.prepareStatement(updateBookSql);
                updateBookStmt.setInt(1, item.getQuantity());
                updateBookStmt.setInt(2, item.getBookId());
                updateBookStmt.executeUpdate();
            }
            
            itemStmt.executeBatch();
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                dbConnection.closeConnection(conn);
            }
        }
    }
    
    public List<Bill> getAllBills() throws SQLException {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT b.*, c.name as customer_name, u.full_name as cashier_name " +
                    "FROM bills b " +
                    "JOIN customers c ON b.customer_id = c.id " +
                    "JOIN users u ON b.cashier_id = u.id " +
                    "ORDER BY b.created_at DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                bills.add(mapResultSetToBill(rs));
            }
        }
        return bills;
    }
    
    public Bill getBillById(int id) throws SQLException {
        String sql = "SELECT b.*, c.name as customer_name, u.full_name as cashier_name " +
                    "FROM bills b " +
                    "JOIN customers c ON b.customer_id = c.id " +
                    "JOIN users u ON b.cashier_id = u.id " +
                    "WHERE b.id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Bill bill = mapResultSetToBill(rs);
                bill.setBillItems(getBillItems(id));
                return bill;
            }
        }
        return null;
    }
    
    public List<BillItem> getBillItems(int billId) throws SQLException {
        List<BillItem> items = new ArrayList<>();
        String sql = "SELECT bi.*, b.title as book_title, b.author as book_author " +
                    "FROM bill_items bi " +
                    "JOIN books b ON bi.book_id = b.id " +
                    "WHERE bi.bill_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, billId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                items.add(mapResultSetToBillItem(rs));
            }
        }
        return items;
    }
    
    public List<Bill> searchBills(String searchTerm) throws SQLException {
        List<Bill> bills = new ArrayList<>();
        
        // Use HashMap for search criteria with table aliases
        Map<String, String> searchCriteria = new HashMap<>();
        searchCriteria.put("b.bill_number", "Bill Number");
        searchCriteria.put("c.name", "Customer Name");
        
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT b.*, c.name as customer_name, u.full_name as cashier_name ");
        sqlBuilder.append("FROM bills b ");
        sqlBuilder.append("JOIN customers c ON b.customer_id = c.id ");
        sqlBuilder.append("JOIN users u ON b.cashier_id = u.id ");
        sqlBuilder.append("WHERE ");
        
        List<String> conditions = new ArrayList<>();
        for (String field : searchCriteria.keySet()) {
            conditions.add(field + " LIKE ?");
        }
        
        sqlBuilder.append(String.join(" OR ", conditions));
        sqlBuilder.append(" ORDER BY b.created_at DESC");
        String sql = sqlBuilder.toString();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchTerm + "%";
            
            int paramIndex = 1;
            for (String field : searchCriteria.keySet()) {
                stmt.setString(paramIndex++, searchPattern);
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                bills.add(mapResultSetToBill(rs));
            }
        }
        return bills;
    }
    
    public int getBillCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM bills";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    
    public BigDecimal getTotalSales() throws SQLException {
        String sql = "SELECT SUM(total_amount) FROM bills";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        }
        return BigDecimal.ZERO;
    }
    
    public List<Bill> getRecentBills(int limit) throws SQLException {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT b.*, c.name as customer_name, u.full_name as cashier_name " +
                    "FROM bills b " +
                    "JOIN customers c ON b.customer_id = c.id " +
                    "JOIN users u ON b.cashier_id = u.id " +
                    "ORDER BY b.created_at DESC LIMIT ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                bills.add(mapResultSetToBill(rs));
            }
        }
        return bills;
    }
    
    public String generateBillNumber() throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(bill_number, 5) AS UNSIGNED)) FROM bills WHERE bill_number LIKE 'BILL%'";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            int nextNumber = 1;
            if (rs.next()) {
                nextNumber = rs.getInt(1) + 1;
            }
            
            return String.format("BILL%06d", nextNumber);
        }
    }
    
    private Bill mapResultSetToBill(ResultSet rs) throws SQLException {
        Bill bill = new Bill();
        bill.setId(rs.getInt("id"));
        bill.setBillNumber(rs.getString("bill_number"));
        bill.setCustomerId(rs.getInt("customer_id"));
        bill.setCashierId(rs.getInt("cashier_id"));
        bill.setSubtotal(rs.getBigDecimal("subtotal"));
        bill.setTaxAmount(rs.getBigDecimal("tax_amount"));
        bill.setDiscountAmount(rs.getBigDecimal("discount_amount"));
        bill.setTotalAmount(rs.getBigDecimal("total_amount"));
        bill.setPaymentStatus(rs.getString("payment_status"));
        bill.setCreatedAt(rs.getTimestamp("created_at"));
        bill.setCustomerName(rs.getString("customer_name"));
        bill.setCashierName(rs.getString("cashier_name"));
        return bill;
    }
    
    private BillItem mapResultSetToBillItem(ResultSet rs) throws SQLException {
        BillItem item = new BillItem();
        item.setId(rs.getInt("id"));
        item.setBillId(rs.getInt("bill_id"));
        item.setBookId(rs.getInt("book_id"));
        item.setQuantity(rs.getInt("quantity"));
        item.setUnitPrice(rs.getBigDecimal("unit_price"));
        item.setTaxRate(rs.getBigDecimal("tax_rate"));
        item.setDiscountRate(rs.getBigDecimal("discount_rate"));
        item.setTaxAmount(rs.getBigDecimal("tax_amount"));
        item.setDiscountAmount(rs.getBigDecimal("discount_amount"));
        item.setLineTotal(rs.getBigDecimal("line_total"));
        item.setBookTitle(rs.getString("book_title"));
        item.setBookAuthor(rs.getString("book_author"));
        return item;
    }
}