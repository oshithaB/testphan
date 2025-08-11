package com.pahanaedu.bookshop.persistence.servlet;

import com.pahanaedu.bookshop.persistence.dao.BookDAO;
import com.pahanaedu.bookshop.business.book.model.Book;
import com.pahanaedu.bookshop.persistence.resource.resource.factory.impl.BookFactoryImpl;
import com.pahanaedu.bookshop.webservice.BookWebService;
import com.pahanaedu.bookshop.business.user.model.User;
import com.pahanaedu.bookshop.persistence.resource.resource.util.DatabaseConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class BookServlet extends HttpServlet {
    private BookDAO bookDAO;
    private BookFactoryImpl bookFactory;
    private BookWebService webService;

    @Override
    public void init() throws ServletException {
        bookDAO = new BookDAO();
        bookFactory = new BookFactoryImpl();
        webService = new BookWebService();
        webService.init();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check if this is an API request
        String acceptHeader = request.getHeader("Accept");
        if (acceptHeader != null && acceptHeader.contains("application/json")) {
            // Delegate to web service for JSON response
            webService.doGet(request, response);
            return;
        }

        String action = request.getParameter("action");
        if (action == null) action = "list";

        try {
            switch (action) {
                case "list":
                    listBooks(request, response);
                    break;
                case "add":
                    showAddForm(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "delete":
                    deleteBook(request, response);
                    break;
                case "search":
                    searchBooks(request, response);
                    break;
                default:
                    listBooks(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error processing request: " + e.getMessage());
            request.getRequestDispatcher("/books.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check if this is an API request
        String contentType = request.getContentType();
        if (contentType != null && contentType.contains("application/json")) {
            // Delegate to web service for JSON request
            webService.doPost(request, response);
            return;
        }

        String action = request.getParameter("action");

        try {
            switch (action) {
                case "add":
                    addBook(request, response);
                    break;
                case "edit":
                    updateBook(request, response);
                    break;
                default:
                    listBooks(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error processing request: " + e.getMessage());
            request.getRequestDispatcher("/books.jsp").forward(request, response);
        }
    }

    private void listBooks(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<Book> books = bookDAO.getAllBooks();
        request.setAttribute("books", books);
        request.getRequestDispatcher("/books.jsp").forward(request, response);
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        request.setAttribute("action", "add");
        request.getRequestDispatcher("/book-form.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        int id = Integer.parseInt(request.getParameter("id"));
        Book book = bookDAO.getBookById(id);
        request.setAttribute("book", book);
        request.setAttribute("action", "edit");
        request.getRequestDispatcher("/book-form.jsp").forward(request, response);
    }

    private void addBook(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        // Use factory to create book
        Book book = bookFactory.createBook(
                request.getParameter("title"),
                request.getParameter("author"),
                request.getParameter("isbn"),
                request.getParameter("category"),
                new BigDecimal(request.getParameter("price")),
                Integer.parseInt(request.getParameter("quantity"))
        );
        book.setDescription(request.getParameter("description"));

        if (bookDAO.addBook(book)) {
            request.setAttribute("success", "Book added successfully!");
        } else {
            request.setAttribute("error", "Failed to add book");
        }

        listBooks(request, response);
    }

    private void updateBook(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Book book = new Book();
        book.setId(Integer.parseInt(request.getParameter("id")));
        book.setTitle(request.getParameter("title"));
        book.setAuthor(request.getParameter("author"));
        book.setIsbn(request.getParameter("isbn"));
        book.setCategory(request.getParameter("category"));
        book.setPrice(new BigDecimal(request.getParameter("price")));
        book.setQuantity(Integer.parseInt(request.getParameter("quantity")));
        book.setDescription(request.getParameter("description"));

        if (bookDAO.updateBook(book)) {
            request.setAttribute("success", "Book updated successfully!");
        } else {
            request.setAttribute("error", "Failed to update book");
        }

        listBooks(request, response);
    }

    private void deleteBook(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        int id = Integer.parseInt(request.getParameter("id"));

        if (bookDAO.deleteBook(id)) {
            request.setAttribute("success", "Book deleted successfully!");
        } else {
            request.setAttribute("error", "Failed to delete book");
        }

        listBooks(request, response);
    }

    private void searchBooks(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String searchTerm = request.getParameter("search");
        List<Book> books;

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            books = bookDAO.searchBooks(searchTerm);
        } else {
            books = bookDAO.getAllBooks();
        }

        request.setAttribute("books", books);
        request.setAttribute("searchTerm", searchTerm);
        request.getRequestDispatcher("/books.jsp").forward(request, response);
    }
}