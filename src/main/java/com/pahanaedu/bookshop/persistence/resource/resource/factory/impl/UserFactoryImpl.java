package com.pahanaedu.bookshop.persistence.resource.resource.factory.impl;

import com.pahanaedu.bookshop.business.user.model.User;
import com.pahanaedu.bookshop.persistence.resource.resource.factory.ProductFactory;
import com.pahanaedu.bookshop.persistence.resource.resource.model.Product;


public class UserFactoryImpl extends ProductFactory {
    
    public static final String USER_TYPE = "user";
    
    @Override
    public Product createProduct(String type) {
        return createProduct(type, (Object[]) null);
    }
    
    @Override
    public Product createProduct(String type, Object... params) {
        if (!USER_TYPE.equalsIgnoreCase(type)) {
            throw new IllegalArgumentException("Unsupported product type: " + type);
        }
        
        User user = new User();
        
        if (params != null && params.length >= 5) {
            // Create user with parameters: username, password, role, fullName, email
            user.setUsername((String) params[0]);
            user.setPassword((String) params[1]);
            user.setRole((String) params[2]);
            user.setFullName((String) params[3]);
            user.setEmail((String) params[4]);
            
            if (params.length > 5) {
                user.setPhone((String) params[5]);
            }
        }
        
        return user;
    }
    
    @Override
    public String[] getSupportedTypes() {
        return new String[]{USER_TYPE};
    }
    
    /**
     * Create a user with specific parameters
     * @param username username
     * @param password password
     * @param role user role
     * @param fullName full name
     * @param email email
     * @return User instance
     */
    public User createUser(String username, String password, String role, String fullName, String email) {
        return (User) createProduct(USER_TYPE, username, password, role, fullName, email);
    }
}