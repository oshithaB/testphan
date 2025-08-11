package com.pahanaedu.bookshop.webservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pahanaedu.bookshop.business.book.dto.BookDTO;
import com.pahanaedu.bookshop.business.book.mapper.BookMapper;
import com.pahanaedu.bookshop.business.book.model.Book;
import com.pahanaedu.bookshop.persistence.dao.BookDAO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Web Service for Book operations
 * Provides JSON API endpoints for distributed access
 */
public class BookWebService extends HttpServlet {
    private BookDAO bookDAO;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        bookDAO = new BookDAO();
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();
        String action = request.getParameter("action");

        try {
            Map<String, Object> result = new HashMap<>();

            if (pathInfo != null && pathInfo.startsWith("/")) {
                // RESTful URL: /api/books/123
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length > 1) {
                    int bookId = Integer.parseInt(pathParts[1]);
                    Book book = bookDAO.getBookById(bookId);
                    if (book != null) {
                        result.put("success", true);
                        result.put("data", BookMapper.toDTO(book));
                    } else {
                        result.put("success", false);
                        result.put("message", "Book not found");
                    }
                }
            } else if ("list".equals(action)) {
                // Get all books
                List<Book> books = bookDAO.getAllBooks();
                result.put("success", true);
                result.put("data", BookMapper.toDTOList(books));
                result.put("count", books.size());

            } else if ("search".equals(action)) {
                // Search books
                String searchTerm = request.getParameter("term");
                List<Book> books = bookDAO.searchBooks(searchTerm);
                result.put("success", true);
                result.put("data", BookMapper.toDTOList(books));
                result.put("count", books.size());
                result.put("searchTerm", searchTerm);

            } else if ("count".equals(action)) {
                // Get book count
                int count = bookDAO.getBookCount();
                result.put("success", true);
                result.put("count", count);

            } else {
                // Default: return all books
                List<Book> books = bookDAO.getAllBooks();
                result.put("success", true);
                result.put("data", BookMapper.toDTOList(books));
                result.put("count", books.size());
            }

            response.getWriter().write(objectMapper.writeValueAsString(result));

        } catch (Exception e) {
            handleError(response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            Map<String, Object> result = new HashMap<>();

            // Read JSON from request body
            StringBuilder jsonBuffer = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                jsonBuffer.append(line);
            }

            if (jsonBuffer.length() > 0) {
                // JSON request body
                BookDTO bookDTO = objectMapper.readValue(jsonBuffer.toString(), BookDTO.class);
                Book book = BookMapper.toEntity(bookDTO);

                if (bookDAO.addBook(book)) {
                    result.put("success", true);
                    result.put("message", "Book added successfully");
                    result.put("data", BookMapper.toDTO(book));
                } else {
                    result.put("success", false);
                    result.put("message", "Failed to add book");
                }
            } else {
                // Form data request
                Book book = new Book();
                book.setTitle(request.getParameter("title"));
                book.setAuthor(request.getParameter("author"));
                book.setIsbn(request.getParameter("isbn"));
                book.setCategory(request.getParameter("category"));
                book.setPrice(new java.math.BigDecimal(request.getParameter("price")));
                book.setQuantity(Integer.parseInt(request.getParameter("quantity")));
                book.setDescription(request.getParameter("description"));

                if (bookDAO.addBook(book)) {
                    result.put("success", true);
                    result.put("message", "Book added successfully");
                    result.put("data", BookMapper.toDTO(book));
                } else {
                    result.put("success", false);
                    result.put("message", "Failed to add book");
                }
            }

            response.getWriter().write(objectMapper.writeValueAsString(result));

        } catch (Exception e) {
            handleError(response, e);
        }
    }

    private void handleError(HttpServletResponse response, Exception e) throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", "Server error: " + e.getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}