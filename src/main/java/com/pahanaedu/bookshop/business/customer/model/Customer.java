package com.pahanaedu.bookshop.business.customer.model;

import com.pahanaedu.bookshop.persistence.resource.resource.model.Product;
import java.sql.Timestamp;

public class Customer implements Product {
    private int id;
    private String accountNumber;
    private String name;
    private String address;
    private String telephone;
    private String email;
    private boolean isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Constructors
    public Customer() {}
    
    public Customer(String accountNumber, String name, String address, String telephone, String email) {
        this.accountNumber = accountNumber;
        this.name = name;
        this.address = address;
        this.telephone = telephone;
        this.email = email;
        this.isActive = true;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    
    // Product interface implementation
    @Override
    public String getDisplayName() {
        return this.name;
    }
    
    @Override
    public String getProductType() {
        return "customer";
    }
    
    @Override
    public boolean validate() {
        return accountNumber != null && !accountNumber.trim().isEmpty() &&
               name != null && !name.trim().isEmpty() &&
               address != null && !address.trim().isEmpty() &&
               telephone != null && !telephone.trim().isEmpty();
    }
    
    @Override
    public String getSearchableText() {
        StringBuilder sb = new StringBuilder();
        if (name != null) sb.append(name).append(" ");
        if (accountNumber != null) sb.append(accountNumber).append(" ");
        if (email != null) sb.append(email).append(" ");
        if (telephone != null) sb.append(telephone).append(" ");
        return sb.toString().trim();
    }
}