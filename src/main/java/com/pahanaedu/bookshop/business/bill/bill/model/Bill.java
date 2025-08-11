package com.pahanaedu.bookshop.business.bill.bill.model;

import com.pahanaedu.bookshop.persistence.resource.resource.model.Product;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class Bill implements Product {
    private int id;
    private String billNumber;
    private int customerId;
    private int cashierId;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private String paymentStatus;
    private Timestamp createdAt;
    
    // Additional fields for display
    private String customerName;
    private String cashierName;
    private List<BillItem> billItems;
    
    // Constructors
    public Bill() {}
    
    public Bill(String billNumber, int customerId, int cashierId) {
        this.billNumber = billNumber;
        this.customerId = customerId;
        this.cashierId = cashierId;
        this.subtotal = BigDecimal.ZERO;
        this.taxAmount = BigDecimal.ZERO;
        this.discountAmount = BigDecimal.ZERO;
        this.totalAmount = BigDecimal.ZERO;
        this.paymentStatus = "paid";
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getBillNumber() { return billNumber; }
    public void setBillNumber(String billNumber) { this.billNumber = billNumber; }
    
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    
    public int getCashierId() { return cashierId; }
    public void setCashierId(int cashierId) { this.cashierId = cashierId; }
    
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    
    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }
    
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public String getCashierName() { return cashierName; }
    public void setCashierName(String cashierName) { this.cashierName = cashierName; }
    
    public List<BillItem> getBillItems() { return billItems; }
    public void setBillItems(List<BillItem> billItems) { this.billItems = billItems; }
    
    // Product interface implementation
    @Override
    public String getDisplayName() {
        return this.billNumber;
    }
    
    @Override
    public String getProductType() {
        return "bill";
    }
    
    @Override
    public boolean validate() {
        return billNumber != null && !billNumber.trim().isEmpty() &&
               customerId > 0 && cashierId > 0 &&
               totalAmount != null && totalAmount.compareTo(BigDecimal.ZERO) >= 0;
    }
    
    @Override
    public String getSearchableText() {
        StringBuilder sb = new StringBuilder();
        if (billNumber != null) sb.append(billNumber).append(" ");
        if (customerName != null) sb.append(customerName).append(" ");
        if (cashierName != null) sb.append(cashierName).append(" ");
        return sb.toString().trim();
    }

    @Override
    public boolean isActive() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isActive'");
    }

    @Override
    public void setActive(boolean active) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setActive'");
    }

    @Override
    public Timestamp getUpdatedAt() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUpdatedAt'");
    }

    @Override
    public void setUpdatedAt(Timestamp updatedAt) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setUpdatedAt'");
    }
}