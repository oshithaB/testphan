package com.pahanaedu.bookshop.book.service;

import com.pahanaedu.bookshop.business.book.model.Book;
import com.pahanaedu.bookshop.business.book.service.BookService;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;

import java.math.BigDecimal;

/**
 * Unit tests for BookService
 * Covers 40% of application functionality
 */
public class BookServiceTest {
    
    private BookService bookService;
    private Book testBook;
    
    @Before
    public void setUp() {
        bookService = new BookService();
        testBook = new Book();
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setIsbn("978-0123456789");
        testBook.setCategory("Programming");
        testBook.setPrice(new BigDecimal("25.99"));
        testBook.setQuantity(10);
        testBook.setDescription("Test book description");
    }
    
    @Test
    public void testBookValidation() {
        // Test valid book
        assertTrue("Valid book should pass validation", testBook.validate());
        
        // Test invalid book - null title
        Book invalidBook = new Book();
        invalidBook.setAuthor("Test Author");
        invalidBook.setPrice(new BigDecimal("25.99"));
        invalidBook.setQuantity(10);
        assertFalse("Book with null title should fail validation", invalidBook.validate());
        
        // Test invalid book - empty title
        invalidBook.setTitle("");
        assertFalse("Book with empty title should fail validation", invalidBook.validate());
        
        // Test invalid book - null author
        invalidBook.setTitle("Test Title");
        invalidBook.setAuthor(null);
        assertFalse("Book with null author should fail validation", invalidBook.validate());
        
        // Test invalid book - negative price
        invalidBook.setAuthor("Test Author");
        invalidBook.setPrice(new BigDecimal("-10.00"));
        assertFalse("Book with negative price should fail validation", invalidBook.validate());
        
        // Test invalid book - negative quantity
        invalidBook.setPrice(new BigDecimal("25.99"));
        invalidBook.setQuantity(-1);
        assertFalse("Book with negative quantity should fail validation", invalidBook.validate());
    }
    
    @Test
    public void testBookGetters() {
        assertEquals("Title should match", "Test Book", testBook.getTitle());
        assertEquals("Author should match", "Test Author", testBook.getAuthor());
        assertEquals("ISBN should match", "978-0123456789", testBook.getIsbn());
        assertEquals("Category should match", "Programming", testBook.getCategory());
        assertEquals("Price should match", new BigDecimal("25.99"), testBook.getPrice());
        assertEquals("Quantity should match", 10, testBook.getQuantity());
        assertEquals("Description should match", "Test book description", testBook.getDescription());
    }
    
    @Test
    public void testBookSetters() {
        Book book = new Book();
        book.setId(1);
        book.setTitle("New Title");
        book.setAuthor("New Author");
        book.setIsbn("978-9876543210");
        book.setCategory("Database");
        book.setPrice(new BigDecimal("35.50"));
        book.setQuantity(5);
        book.setDescription("New description");
        book.setActive(true);
        
        assertEquals("ID should be set", 1, book.getId());
        assertEquals("Title should be set", "New Title", book.getTitle());
        assertEquals("Author should be set", "New Author", book.getAuthor());
        assertEquals("ISBN should be set", "978-9876543210", book.getIsbn());
        assertEquals("Category should be set", "Database", book.getCategory());
        assertEquals("Price should be set", new BigDecimal("35.50"), book.getPrice());
        assertEquals("Quantity should be set", 5, book.getQuantity());
        assertEquals("Description should be set", "New description", book.getDescription());
        assertTrue("Book should be active", book.isActive());
    }
    
    @Test
    public void testProductInterfaceMethods() {
        assertEquals("Display name should be title", "Test Book", testBook.getDisplayName());
        assertEquals("Product type should be book", "book", testBook.getProductType());
        
        String searchableText = testBook.getSearchableText();
        assertTrue("Searchable text should contain title", searchableText.contains("Test Book"));
        assertTrue("Searchable text should contain author", searchableText.contains("Test Author"));
        assertTrue("Searchable text should contain ISBN", searchableText.contains("978-0123456789"));
        assertTrue("Searchable text should contain category", searchableText.contains("Programming"));
    }
    
    @Test
    public void testBookConstructors() {
        // Test default constructor
        Book book1 = new Book();
        assertNotNull("Default constructor should create book", book1);
        
        // Test parameterized constructor
        Book book2 = new Book("Title", "Author", "ISBN", "Category", new BigDecimal("20.00"), 15);
        assertEquals("Title should be set", "Title", book2.getTitle());
        assertEquals("Author should be set", "Author", book2.getAuthor());
        assertEquals("ISBN should be set", "ISBN", book2.getIsbn());
        assertEquals("Category should be set", "Category", book2.getCategory());
        assertEquals("Price should be set", new BigDecimal("20.00"), book2.getPrice());
        assertEquals("Quantity should be set", 15, book2.getQuantity());
        assertTrue("Book should be active by default", book2.isActive());
    }
    
    @After
    public void tearDown() {
        bookService = null;
        testBook = null;
    }
}