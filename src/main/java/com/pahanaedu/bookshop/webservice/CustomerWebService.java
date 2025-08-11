package com.pahanaedu.bookshop.webservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pahanaedu.bookshop.business.customer.dto.CustomerDTO;
import com.pahanaedu.bookshop.business.customer.mapper.CustomerMapper;
import com.pahanaedu.bookshop.business.customer.model.Customer;
import com.pahanaedu.bookshop.persistence.dao.CustomerDAO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Web Service for Customer operations
 * Provides JSON API endpoints for distributed access
 */
public class CustomerWebService extends HttpServlet {
    private CustomerDAO customerDAO;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        customerDAO = new CustomerDAO();
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
                // RESTful URL: /api/customers/123
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length > 1) {
                    int customerId = Integer.parseInt(pathParts[1]);
                    Customer customer = customerDAO.getCustomerById(customerId);
                    if (customer != null) {
                        result.put("success", true);
                        result.put("data", CustomerMapper.toDTO(customer));
                    } else {
                        result.put("success", false);
                        result.put("message", "Customer not found");
                    }
                }
            } else if ("list".equals(action)) {
                // Get all customers
                List<Customer> customers = customerDAO.getAllCustomers();
                result.put("success", true);
                result.put("data", CustomerMapper.toDTOList(customers));
                result.put("count", customers.size());

            } else if ("search".equals(action)) {
                // Search customers
                String searchTerm = request.getParameter("term");
                List<Customer> customers = customerDAO.searchCustomers(searchTerm);
                result.put("success", true);
                result.put("data", CustomerMapper.toDTOList(customers));
                result.put("count", customers.size());
                result.put("searchTerm", searchTerm);

            } else if ("count".equals(action)) {
                // Get customer count
                int count = customerDAO.getCustomerCount();
                result.put("success", true);
                result.put("count", count);

            } else if ("generateAccountNumber".equals(action)) {
                // Generate new account number
                String accountNumber = customerDAO.generateAccountNumber();
                result.put("success", true);
                result.put("accountNumber", accountNumber);

            } else {
                // Default: return all customers
                List<Customer> customers = customerDAO.getAllCustomers();
                result.put("success", true);
                result.put("data", CustomerMapper.toDTOList(customers));
                result.put("count", customers.size());
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
                CustomerDTO customerDTO = objectMapper.readValue(jsonBuffer.toString(), CustomerDTO.class);
                Customer customer = CustomerMapper.toEntity(customerDTO);

                if (customerDAO.addCustomer(customer)) {
                    result.put("success", true);
                    result.put("message", "Customer added successfully");
                    result.put("data", CustomerMapper.toDTO(customer));
                } else {
                    result.put("success", false);
                    result.put("message", "Failed to add customer");
                }
            } else {
                // Form data request
                Customer customer = new Customer();
                customer.setAccountNumber(request.getParameter("accountNumber"));
                customer.setName(request.getParameter("name"));
                customer.setAddress(request.getParameter("address"));
                customer.setTelephone(request.getParameter("telephone"));
                customer.setEmail(request.getParameter("email"));

                if (customerDAO.addCustomer(customer)) {
                    result.put("success", true);
                    result.put("message", "Customer added successfully");
                    result.put("data", CustomerMapper.toDTO(customer));
                } else {
                    result.put("success", false);
                    result.put("message", "Failed to add customer");
                }
            }

            response.getWriter().write(objectMapper.writeValueAsString(result));

        } catch (Exception e) {
            handleError(response, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
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

            CustomerDTO customerDTO = objectMapper.readValue(jsonBuffer.toString(), CustomerDTO.class);
            Customer customer = CustomerMapper.toEntity(customerDTO);

            if (customerDAO.updateCustomer(customer)) {
                result.put("success", true);
                result.put("message", "Customer updated successfully");
                result.put("data", CustomerMapper.toDTO(customer));
            } else {
                result.put("success", false);
                result.put("message", "Failed to update customer");
            }

            response.getWriter().write(objectMapper.writeValueAsString(result));

        } catch (Exception e) {
            handleError(response, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            Map<String, Object> result = new HashMap<>();

            String pathInfo = request.getPathInfo();
            if (pathInfo != null && pathInfo.startsWith("/")) {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length > 1) {
                    int customerId = Integer.parseInt(pathParts[1]);

                    if (customerDAO.deleteCustomer(customerId)) {
                        result.put("success", true);
                        result.put("message", "Customer deleted successfully");
                    } else {
                        result.put("success", false);
                        result.put("message", "Failed to delete customer");
                    }
                }
            } else {
                result.put("success", false);
                result.put("message", "Customer ID required");
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