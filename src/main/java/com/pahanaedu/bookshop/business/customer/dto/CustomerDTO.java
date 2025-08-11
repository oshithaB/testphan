package com.pahanaedu.bookshop.business.customer.dto;

/**
 * Data Transfer Object for Customer
 */
public class CustomerDTO {
    private int id;
    private String accountNumber;
    private String name;
    private String address;
    private String telephone;
    private String email;
    private boolean isActive;
    private String createdAt;
    private String updatedAt;
    
    // Constructors
    public CustomerDTO() {}
    
    public CustomerDTO(int id, String accountNumber, String name, String telephone) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.name = name;
        this.telephone = telephone;
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
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}