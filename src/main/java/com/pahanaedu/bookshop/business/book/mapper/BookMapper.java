package com.pahanaedu.bookshop.business.book.mapper;

import com.pahanaedu.bookshop.business.book.dto.BookDTO;
import com.pahanaedu.bookshop.business.book.model.Book;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapper for Book entity and DTO conversion
 */
public class BookMapper {
    
    /**
     * Convert Book entity to BookDTO
     * @param book Book entity
     * @return BookDTO
     */
    public static BookDTO toDTO(Book book) {
        if (book == null) {
            return null;
        }
        
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setIsbn(book.getIsbn());
        dto.setCategory(book.getCategory());
        dto.setPrice(book.getPrice());
        dto.setQuantity(book.getQuantity());
        dto.setDescription(book.getDescription());
        dto.setActive(book.isActive());
        
        if (book.getCreatedAt() != null) {
            dto.setCreatedAt(book.getCreatedAt().toString());
        }
        if (book.getUpdatedAt() != null) {
            dto.setUpdatedAt(book.getUpdatedAt().toString());
        }
        
        return dto;
    }
    
    /**
     * Convert BookDTO to Book entity
     * @param dto BookDTO
     * @return Book entity
     */
    public static Book toEntity(BookDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Book book = new Book();
        book.setId(dto.getId());
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setIsbn(dto.getIsbn());
        book.setCategory(dto.getCategory());
        book.setPrice(dto.getPrice());
        book.setQuantity(dto.getQuantity());
        book.setDescription(dto.getDescription());
        book.setActive(dto.isActive());
        
        return book;
    }
    
    /**
     * Convert list of Book entities to list of BookDTOs
     * @param books List of Book entities
     * @return List of BookDTOs
     */
    public static List<BookDTO> toDTOList(List<Book> books) {
        if (books == null) {
            return null;
        }
        
        List<BookDTO> dtoList = new ArrayList<>();
        for (Book book : books) {
            dtoList.add(toDTO(book));
        }
        return dtoList;
    }
    
    /**
     * Convert list of BookDTOs to list of Book entities
     * @param dtos List of BookDTOs
     * @return List of Book entities
     */
    public static List<Book> toEntityList(List<BookDTO> dtos) {
        if (dtos == null) {
            return null;
        }
        
        List<Book> entityList = new ArrayList<>();
        for (BookDTO dto : dtos) {
            entityList.add(toEntity(dto));
        }
        return entityList;
    }
}