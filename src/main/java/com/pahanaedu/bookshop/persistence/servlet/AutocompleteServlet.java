package com.pahanaedu.bookshop.persistence.servlet;

import com.google.gson.Gson;
import com.pahanaedu.bookshop.persistence.dao.BookDAO;
import com.pahanaedu.bookshop.persistence.dao.CustomerDAO;
import com.pahanaedu.bookshop.business.book.model.Book;
import com.pahanaedu.bookshop.business.customer.model.Customer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class AutocompleteServlet extends HttpServlet {
    private CustomerDAO customerDAO;
    private BookDAO bookDAO;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        customerDAO = new CustomerDAO();
        bookDAO = new BookDAO();
        gson = new Gson();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String type = request.getParameter("type");
        String term = request.getParameter("term");
        
        // Use HashMap to map request types to response data
        Map<String, Object> responseData = new HashMap<>();
        
        try {
            if ("customer".equals(type)) {
                List<Customer> customers = customerDAO.searchCustomers(term);
                responseData.put("data", customers);
                responseData.put("type", "customers");
                responseData.put("count", customers.size());
            } else if ("book".equals(type)) {
                List<Book> books = bookDAO.searchBooks(term);
                responseData.put("data", books);
                responseData.put("type", "books");
                responseData.put("count", books.size());
            } else {
                responseData.put("data", new ArrayList<>());
                responseData.put("type", "unknown");
                responseData.put("count", 0);
            }
            
            // For backward compatibility, still return the data array for frontend
            Object data = responseData.get("data");
            response.getWriter().write(gson.toJson(data));
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Server error");
            errorResponse.put("message", e.getMessage());
            response.getWriter().write(gson.toJson(errorResponse));
        }
    }
}