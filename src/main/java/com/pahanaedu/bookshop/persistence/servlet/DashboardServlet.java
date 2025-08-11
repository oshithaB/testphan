package com.pahanaedu.bookshop.persistence.servlet;

import com.pahanaedu.bookshop.persistence.dao.BillDAO;
import com.pahanaedu.bookshop.persistence.dao.BookDAO;
import com.pahanaedu.bookshop.persistence.dao.CustomerDAO;
import com.pahanaedu.bookshop.business.bill.bill.model.Bill;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class DashboardServlet extends HttpServlet {
    private CustomerDAO customerDAO;
    private BookDAO bookDAO;
    private BillDAO billDAO;
    
    @Override
    public void init() throws ServletException {
        customerDAO = new CustomerDAO();
        bookDAO = new BookDAO();
        billDAO = new BillDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // Get dashboard statistics
            int customerCount = customerDAO.getCustomerCount();
            int bookCount = bookDAO.getBookCount();
            int billCount = billDAO.getBillCount();
            BigDecimal totalSales = billDAO.getTotalSales();
            
            // Get recent bills
            List<Bill> recentBills = billDAO.getRecentBills(5);
            
            // Set attributes
            request.setAttribute("customerCount", customerCount);
            request.setAttribute("bookCount", bookCount);
            request.setAttribute("billCount", billCount);
            request.setAttribute("totalSales", totalSales != null ? totalSales : BigDecimal.ZERO);
            request.setAttribute("recentBills", recentBills);
            
            request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading dashboard data");
            request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
        }
    }
}