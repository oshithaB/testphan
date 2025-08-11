package com.pahanaedu.bookshop.persistence.resource.resource.model;

import java.sql.Timestamp;

/**
 * Product interface - defines common operations for all products
 * Part of Factory Pattern implementation
 */
public interface Product {
    
    /**
     * Get the unique identifier of the product
     * @return product ID
     */
    int getId();
    
    /**
     * Set the unique identifier of the product
     * @param id product ID
     */
    void setId(int id);
    
    /**
     * Get the display name of the product
     * @return product name/title
     */
    String getDisplayName();
    
    /**
     * Get the product type identifier
     * @return product type (book, customer, etc.)
     */
    String getProductType();
    
    /**
     * Check if the product is active
     * @return true if active, false otherwise
     */
    boolean isActive();
    
    /**
     * Set the active status of the product
     * @param active active status
     */
    void setActive(boolean active);
    
    /**
     * Get the creation timestamp
     * @return creation timestamp
     */
    Timestamp getCreatedAt();
    
    /**
     * Set the creation timestamp
     * @param createdAt creation timestamp
     */
    void setCreatedAt(Timestamp createdAt);
    
    /**
     * Get the last update timestamp
     * @return update timestamp
     */
    Timestamp getUpdatedAt();
    
    /**
     * Set the last update timestamp
     * @param updatedAt update timestamp
     */
    void setUpdatedAt(Timestamp updatedAt);
    
    /**
     * Validate the product data
     * @return true if valid, false otherwise
     */
    boolean validate();
    
    /**
     * Get a string representation suitable for search/display
     * @return searchable string
     */
    String getSearchableText();
}