package com.pahanaedu.bookshop.customer.service;

import com.pahanaedu.bookshop.business.customer.model.Customer;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;

/**
 * Unit tests for Customer functionality
 */
public class CustomerServiceTest {
    
    private Customer testCustomer;
    
    @Before
    public void setUp() {
        testCustomer = new Customer();
        testCustomer.setAccountNumber("CUST001");
        testCustomer.setName("John Doe");
        testCustomer.setAddress("123 Main Street");
        testCustomer.setTelephone("0771234567");
        testCustomer.setEmail("john.doe@email.com");
    }
    
    @Test
    public void testCustomerValidation() {
        // Test valid customer
        assertTrue("Valid customer should pass validation", testCustomer.validate());
        
        // Test invalid customer - null account number
        Customer invalidCustomer = new Customer();
        invalidCustomer.setName("John Doe");
        invalidCustomer.setAddress("123 Main Street");
        invalidCustomer.setTelephone("0771234567");
        assertFalse("Customer with null account number should fail validation", invalidCustomer.validate());
        
        // Test invalid customer - empty name
        invalidCustomer.setAccountNumber("CUST001");
        invalidCustomer.setName("");
        assertFalse("Customer with empty name should fail validation", invalidCustomer.validate());
    }
    
    @Test
    public void testCustomerGetters() {
        assertEquals("Account number should match", "CUST001", testCustomer.getAccountNumber());
        assertEquals("Name should match", "John Doe", testCustomer.getName());
        assertEquals("Address should match", "123 Main Street", testCustomer.getAddress());
        assertEquals("Telephone should match", "0771234567", testCustomer.getTelephone());
        assertEquals("Email should match", "john.doe@email.com", testCustomer.getEmail());
    }
    
    @Test
    public void testProductInterfaceMethods() {
        assertEquals("Display name should be name", "John Doe", testCustomer.getDisplayName());
        assertEquals("Product type should be customer", "customer", testCustomer.getProductType());
        
        String searchableText = testCustomer.getSearchableText();
        assertTrue("Searchable text should contain name", searchableText.contains("John Doe"));
        assertTrue("Searchable text should contain account number", searchableText.contains("CUST001"));
        assertTrue("Searchable text should contain email", searchableText.contains("john.doe@email.com"));
        assertTrue("Searchable text should contain telephone", searchableText.contains("0771234567"));
    }
    
    @Test
    public void testCustomerConstructors() {
        // Test default constructor
        Customer customer1 = new Customer();
        assertNotNull("Default constructor should create customer", customer1);
        
        // Test parameterized constructor
        Customer customer2 = new Customer("CUST002", "Jane Smith", "456 Oak Ave", "0772345678", "jane@email.com");
        assertEquals("Account number should be set", "CUST002", customer2.getAccountNumber());
        assertEquals("Name should be set", "Jane Smith", customer2.getName());
        assertEquals("Address should be set", "456 Oak Ave", customer2.getAddress());
        assertEquals("Telephone should be set", "0772345678", customer2.getTelephone());
        assertEquals("Email should be set", "jane@email.com", customer2.getEmail());
        assertTrue("Customer should be active by default", customer2.isActive());
    }
    
    @After
    public void tearDown() {
        testCustomer = null;
    }
}