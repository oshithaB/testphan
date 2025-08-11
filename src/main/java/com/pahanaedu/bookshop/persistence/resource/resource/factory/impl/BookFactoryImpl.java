package com.pahanaedu.bookshop.persistence.resource.resource.factory.impl;

import com.pahanaedu.bookshop.business.book.model.Book;
import com.pahanaedu.bookshop.persistence.resource.resource.factory.ProductFactory;
import com.pahanaedu.bookshop.persistence.resource.resource.model.Product;
import java.math.BigDecimal;


public class BookFactoryImpl extends ProductFactory {
    
    public static final String BOOK_TYPE = "book";
    
    @Override
    public Product createProduct(String type) {
        return createProduct(type, (Object[]) null);
    }
    
    @Override
    public Product createProduct(String type, Object... params) {
        if (!BOOK_TYPE.equalsIgnoreCase(type)) {
            throw new IllegalArgumentException("Unsupported product type: " + type);
        }
        
        Book book = new Book();
        
        if (params != null && params.length >= 6) {
            // Create book with parameters: title, author, isbn, category, price, quantity
            book.setTitle((String) params[0]);
            book.setAuthor((String) params[1]);
            book.setIsbn((String) params[2]);
            book.setCategory((String) params[3]);
            book.setPrice((BigDecimal) params[4]);
            book.setQuantity((Integer) params[5]);
            
            if (params.length > 6) {
                book.setDescription((String) params[6]);
            }
        }
        
        return book;
    }
    
    @Override
    public String[] getSupportedTypes() {
        return new String[]{BOOK_TYPE};
    }
    
    /**
     * Create a book with specific parameters
     * @param title book title
     * @param author book author
     * @param isbn book ISBN
     * @param category book category
     * @param price book price
     * @param quantity book quantity
     * @return Book instance
     */
    public Book createBook(String title, String author, String isbn, String category, BigDecimal price, int quantity) {
        return (Book) createProduct(BOOK_TYPE, title, author, isbn, category, price, quantity);
    }
}