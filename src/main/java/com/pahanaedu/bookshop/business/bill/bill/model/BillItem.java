package com.pahanaedu.bookshop.business.bill.bill.model;

import com.pahanaedu.bookshop.persistence.resource.resource.model.Product;
import java.math.BigDecimal;

public class BillItem implements Product {
    private int id;
    private int billId;
    private int bookId;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal taxRate;
    private BigDecimal discountRate;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal lineTotal;
    
    // Additional fields for display
    private String bookTitle;
    private String bookAuthor;
    
    // Constructors
    public BillItem() {}
    
    public BillItem(int billId, int bookId, int quantity, BigDecimal unitPrice, 
                   BigDecimal taxRate, BigDecimal discountRate) {
        this.billId = billId;
        this.bookId = bookId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.taxRate = taxRate;
        this.discountRate = discountRate;
        calculateAmounts();
    }
    
    public void calculateAmounts() {
        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        this.discountAmount = subtotal.multiply(discountRate.divide(BigDecimal.valueOf(100)));
        BigDecimal afterDiscount = subtotal.subtract(discountAmount);
        this.taxAmount = afterDiscount.multiply(taxRate.divide(BigDecimal.valueOf(100)));
        this.lineTotal = afterDiscount.add(taxAmount);
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }
    
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    
    public BigDecimal getTaxRate() { return taxRate; }
    public void setTaxRate(BigDecimal taxRate) { this.taxRate = taxRate; }
    
    public BigDecimal getDiscountRate() { return discountRate; }
    public void setDiscountRate(BigDecimal discountRate) { this.discountRate = discountRate; }
    
    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }
    
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    
    public BigDecimal getLineTotal() { return lineTotal; }
    public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }
    
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    
    public String getBookAuthor() { return bookAuthor; }
    public void setBookAuthor(String bookAuthor) { this.bookAuthor = bookAuthor; }
    
    // Product interface implementation
    @Override
    public String getDisplayName() {
        return this.bookTitle != null ? this.bookTitle : "Bill Item #" + this.id;
    }
    
    @Override
    public String getProductType() {
        return "bill_item";
    }
    
    @Override
    public boolean isActive() {
        return true; // Bill items are always active if they exist
    }
    
    @Override
    public void setActive(boolean active) {
        // Bill items don't have active status
    }
    
    @Override
    public java.sql.Timestamp getCreatedAt() {
        return null; // Bill items don't have creation timestamp
    }
    
    @Override
    public void setCreatedAt(java.sql.Timestamp createdAt) {
        // Bill items don't have creation timestamp
    }
    
    @Override
    public java.sql.Timestamp getUpdatedAt() {
        return null; // Bill items don't have update timestamp
    }
    
    @Override
    public void setUpdatedAt(java.sql.Timestamp updatedAt) {
        // Bill items don't have update timestamp
    }
    
    @Override
    public boolean validate() {
        return billId > 0 && bookId > 0 && quantity > 0 &&
               unitPrice != null && unitPrice.compareTo(BigDecimal.ZERO) > 0;
    }
    
    @Override
    public String getSearchableText() {
        StringBuilder sb = new StringBuilder();
        if (bookTitle != null) sb.append(bookTitle).append(" ");
        if (bookAuthor != null) sb.append(bookAuthor).append(" ");
        return sb.toString().trim();
    }
}