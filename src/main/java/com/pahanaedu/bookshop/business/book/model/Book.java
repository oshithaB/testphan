package com.pahanaedu.bookshop.business.book.model;

import com.pahanaedu.bookshop.persistence.resource.resource.model.Product;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class Book implements Product {
    private int id;
    private String title;
    private String author;
    private String isbn;
    private String category;
    private BigDecimal price;
    private int quantity;
    private String description;
    private boolean isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Constructors
    public Book() {}
    
    public Book(String title, String author, String isbn, String category, BigDecimal price, int quantity) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.isActive = true;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    
    // Product interface implementation
    @Override
    public String getDisplayName() {
        return this.title;
    }
    
    @Override
    public String getProductType() {
        return "book";
    }
    
    @Override
    public boolean validate() {
        return title != null && !title.trim().isEmpty() &&
               author != null && !author.trim().isEmpty() &&
               price != null && price.compareTo(BigDecimal.ZERO) > 0 &&
               quantity >= 0;
    }
    
    @Override
    public String getSearchableText() {
        StringBuilder sb = new StringBuilder();
        if (title != null) sb.append(title).append(" ");
        if (author != null) sb.append(author).append(" ");
        if (isbn != null) sb.append(isbn).append(" ");
        if (category != null) sb.append(category).append(" ");
        return sb.toString().trim();
    }
}