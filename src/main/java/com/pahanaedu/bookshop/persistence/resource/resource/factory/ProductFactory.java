package com.pahanaedu.bookshop.persistence.resource.resource.factory;

import com.pahanaedu.bookshop.persistence.resource.resource.model.Product;


 //Abstract Creator class for Product Factory Pattern

public abstract class ProductFactory {

    // Factory method to create products

    public abstract Product createProduct(String type);
    

    public abstract Product createProduct(String type, Object... params);

    public abstract String[] getSupportedTypes();
    

    public boolean isTypeSupported(String type) {
        if (type == null) return false;
        
        String[] supportedTypes = getSupportedTypes();
        for (String supportedType : supportedTypes) {
            if (supportedType.equalsIgnoreCase(type)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Create a product with validation
     * @param type product type
     * @param params creation parameters
     * @return validated Product instance
     * @throws IllegalArgumentException if type is not supported or validation fails
     */
    public Product createValidatedProduct(String type, Object... params) {
        if (!isTypeSupported(type)) {
            throw new IllegalArgumentException("Unsupported product type: " + type);
        }
        
        Product product = createProduct(type, params);
        if (product != null && !product.validate()) {
            throw new IllegalArgumentException("Product validation failed for type: " + type);
        }
        
        return product;
    }
}