package com.pahanaedu.bookshop.persistence.resource.resource.factory.impl;

import com.pahanaedu.bookshop.business.customer.model.Customer;
import com.pahanaedu.bookshop.persistence.resource.resource.factory.ProductFactory;
import com.pahanaedu.bookshop.persistence.resource.resource.model.Product;

/**
 * Concrete Factory for Customer products
 * Implements Factory Pattern for creating Customer objects
 */
public class CustomerFactoryImpl extends ProductFactory {
    
    public static final String CUSTOMER_TYPE = "customer";
    
    @Override
    public Product createProduct(String type) {
        return createProduct(type, (Object[]) null);
    }
    
    @Override
    public Product createProduct(String type, Object... params) {
        if (!CUSTOMER_TYPE.equalsIgnoreCase(type)) {
            throw new IllegalArgumentException("Unsupported product type: " + type);
        }
        
        Customer customer = new Customer();
        
        if (params != null && params.length >= 5) {
            // Create customer with parameters: accountNumber, name, address, telephone, email
            customer.setAccountNumber((String) params[0]);
            customer.setName((String) params[1]);
            customer.setAddress((String) params[2]);
            customer.setTelephone((String) params[3]);
            customer.setEmail((String) params[4]);
        }
        
        return customer;
    }
    
    @Override
    public String[] getSupportedTypes() {
        return new String[]{CUSTOMER_TYPE};
    }
    
    /**
     * Create a customer with specific parameters
     * @param accountNumber customer account number
     * @param name customer name
     * @param address customer address
     * @param telephone customer telephone
     * @param email customer email
     * @return Customer instance
     */
    public Customer createCustomer(String accountNumber, String name, String address, String telephone, String email) {
        return (Customer) createProduct(CUSTOMER_TYPE, accountNumber, name, address, telephone, email);
    }
}