package com.pahanaedu.bookshop.persistence.servlet;

import com.pahanaedu.bookshop.persistence.dao.CustomerDAO;
import com.pahanaedu.bookshop.business.customer.model.Customer;
import com.pahanaedu.bookshop.persistence.resource.resource.factory.impl.CustomerFactoryImpl;
import com.pahanaedu.bookshop.webservice.CustomerWebService;
import com.pahanaedu.bookshop.business.user.model.User;
import com.pahanaedu.bookshop.persistence.resource.resource.util.DatabaseConnection;
import com.pahanaedu.bookshop.persistence.resource.resource.util.EmailUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public class CustomerServlet extends HttpServlet {
    private CustomerDAO customerDAO;
    private CustomerFactoryImpl customerFactory;
    private CustomerWebService webService;

    @Override
    public void init() throws ServletException {
        customerDAO = new CustomerDAO();
        customerFactory = new CustomerFactoryImpl();
        webService = new CustomerWebService();
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
                    listCustomers(request, response);
                    break;
                case "add":
                    showAddForm(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "delete":
                    deleteCustomer(request, response);
                    break;
                case "search":
                    searchCustomers(request, response);
                    break;
                default:
                    listCustomers(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error processing request: " + e.getMessage());
            request.getRequestDispatcher("/customers.jsp").forward(request, response);
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
                    addCustomer(request, response);
                    break;
                case "edit":
                    updateCustomer(request, response);
                    break;
                default:
                    listCustomers(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error processing request: " + e.getMessage());
            request.getRequestDispatcher("/customers.jsp").forward(request, response);
        }
    }

    private void listCustomers(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<Customer> customers = customerDAO.getAllCustomers();
        request.setAttribute("customers", customers);
        request.getRequestDispatcher("/customers.jsp").forward(request, response);
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String accountNumber = customerDAO.generateAccountNumber();
        request.setAttribute("accountNumber", accountNumber);
        request.setAttribute("action", "add");
        request.getRequestDispatcher("/customer-form.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        int id = Integer.parseInt(request.getParameter("id"));
        Customer customer = customerDAO.getCustomerById(id);
        request.setAttribute("customer", customer);
        request.setAttribute("action", "edit");
        request.getRequestDispatcher("/customer-form.jsp").forward(request, response);
    }

    private void addCustomer(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        // Use factory to create customer
        Customer customer = customerFactory.createCustomer(
                request.getParameter("accountNumber"),
                request.getParameter("name"),
                request.getParameter("address"),
                request.getParameter("telephone"),
                request.getParameter("email")
        );

        if (customerDAO.addCustomer(customer)) {
            // Send welcome email
            if (customer.getEmail() != null && !customer.getEmail().isEmpty()) {
                EmailUtil.sendEmail(customer.getEmail(),
                        "Welcome to Pahana Edu Book Shop",
                        "Dear " + customer.getName() + ",\n\nWelcome to Pahana Edu Book Shop! Your account number is: " + customer.getAccountNumber());
            }

            request.setAttribute("success", "Customer added successfully!");
        } else {
            request.setAttribute("error", "Failed to add customer");
        }

        listCustomers(request, response);
    }

    private void updateCustomer(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Customer customer = new Customer();
        customer.setId(Integer.parseInt(request.getParameter("id")));
        customer.setAccountNumber(request.getParameter("accountNumber"));
        customer.setName(request.getParameter("name"));
        customer.setAddress(request.getParameter("address"));
        customer.setTelephone(request.getParameter("telephone"));
        customer.setEmail(request.getParameter("email"));

        if (customerDAO.updateCustomer(customer)) {
            request.setAttribute("success", "Customer updated successfully!");
        } else {
            request.setAttribute("error", "Failed to update customer");
        }

        listCustomers(request, response);
    }

    private void deleteCustomer(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        int id = Integer.parseInt(request.getParameter("id"));

        if (customerDAO.deleteCustomer(id)) {
            request.setAttribute("success", "Customer deleted successfully!");
        } else {
            request.setAttribute("error", "Failed to delete customer");
        }

        listCustomers(request, response);
    }

    private void searchCustomers(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String searchTerm = request.getParameter("search");
        List<Customer> customers;

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            customers = customerDAO.searchCustomers(searchTerm);
        } else {
            customers = customerDAO.getAllCustomers();
        }

        request.setAttribute("customers", customers);
        request.setAttribute("searchTerm", searchTerm);
        request.getRequestDispatcher("/customers.jsp").forward(request, response);
    }
}