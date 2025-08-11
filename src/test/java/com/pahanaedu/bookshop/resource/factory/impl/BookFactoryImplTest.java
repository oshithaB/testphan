package com.pahanaedu.bookshop.resource.factory.impl;

import com.pahanaedu.bookshop.business.book.model.Book;
import com.pahanaedu.bookshop.persistence.resource.resource.factory.impl.BookFactoryImpl;
import com.pahanaedu.bookshop.persistence.resource.resource.model.Product;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;

import java.math.BigDecimal;

/**
 * Unit tests for BookFactoryImpl
 */
public class BookFactoryImplTest {
    
    private BookFactoryImpl bookFactory;
    
    @Before
    public void setUp() {
        bookFactory = new BookFactoryImpl();
    }
    
    @Test
    public void testCreateProductWithoutParameters() {
        Product product = bookFactory.createProduct("book");
        assertNotNull("Product should be created", product);
        assertTrue("Product should be instance of Book", product instanceof Book);
        assertEquals("Product type should be book", "book", product.getProductType());
    }
    
    @Test
    public void testCreateProductWithParameters() {
        Object[] params = {
            "Java Programming",
            "John Smith",
            "978-0123456789",
            "Programming",
            new BigDecimal("29.99"),
            25
        };
        
        Product product = bookFactory.createProduct("book", params);
        assertNotNull("Product should be created", product);
        assertTrue("Product should be instance of Book", product instanceof Book);
        
        Book book = (Book) product;
        assertEquals("Title should match", "Java Programming", book.getTitle());
        assertEquals("Author should match", "John Smith", book.getAuthor());
        assertEquals("ISBN should match", "978-0123456789", book.getIsbn());
        assertEquals("Category should match", "Programming", book.getCategory());
        assertEquals("Price should match", new BigDecimal("29.99"), book.getPrice());
        assertEquals("Quantity should match", 25, book.getQuantity());
    }
    
    @Test
    public void testCreateProductWithDescription() {
        Object[] params = {
            "Advanced Java",
            "Jane Doe",
            "978-9876543210",
            "Programming",
            new BigDecimal("39.99"),
            15,
            "Advanced Java programming concepts"
        };
        
        Product product = bookFactory.createProduct("book", params);
        Book book = (Book) product;
        assertEquals("Description should match", "Advanced Java programming concepts", book.getDescription());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreateProductWithInvalidType() {
        bookFactory.createProduct("invalid_type");
    }
    
    @Test
    public void testGetSupportedTypes() {
        String[] supportedTypes = bookFactory.getSupportedTypes();
        assertNotNull("Supported types should not be null", supportedTypes);
        assertEquals("Should support one type", 1, supportedTypes.length);
        assertEquals("Should support book type", "book", supportedTypes[0]);
    }
    
    @Test
    public void testIsTypeSupported() {
        assertTrue("Should support book type", bookFactory.isTypeSupported("book"));
        assertTrue("Should support book type case insensitive", bookFactory.isTypeSupported("BOOK"));
        assertFalse("Should not support customer type", bookFactory.isTypeSupported("customer"));
        assertFalse("Should not support null type", bookFactory.isTypeSupported(null));
    }
    
    @Test
    public void testCreateBookMethod() {
        Book book = bookFactory.createBook("Test Title", "Test Author", "123456789", 
                                          "Test Category", new BigDecimal("19.99"), 10);
        
        assertNotNull("Book should be created", book);
        assertEquals("Title should match", "Test Title", book.getTitle());
        assertEquals("Author should match", "Test Author", book.getAuthor());
        assertEquals("ISBN should match", "123456789", book.getIsbn());
        assertEquals("Category should match", "Test Category", book.getCategory());
        assertEquals("Price should match", new BigDecimal("19.99"), book.getPrice());
        assertEquals("Quantity should match", 10, book.getQuantity());
    }
    
    @After
    public void tearDown() {
        bookFactory = null;
    }
}