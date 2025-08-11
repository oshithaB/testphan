package com.pahanaedu.bookshop.persistence.servlet;

import com.google.gson.Gson;
import com.pahanaedu.bookshop.persistence.dao.BillDAO;
import com.pahanaedu.bookshop.persistence.dao.BookDAO;
import com.pahanaedu.bookshop.persistence.dao.CustomerDAO;
import com.pahanaedu.bookshop.webservice.BillWebService;
import com.pahanaedu.bookshop.business.bill.bill.model.Bill;
import com.pahanaedu.bookshop.business.bill.bill.model.BillItem;
import com.pahanaedu.bookshop.business.user.model.User;
import com.pahanaedu.bookshop.business.customer.model.Customer;
import com.pahanaedu.bookshop.persistence.resource.resource.util.DatabaseConnection;
import com.pahanaedu.bookshop.persistence.resource.resource.util.EmailUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BillServlet extends HttpServlet {
    private BillDAO billDAO;
    private CustomerDAO customerDAO;
    private BookDAO bookDAO;
    private BillWebService webService;

    @Override
    public void init() throws ServletException {
        billDAO = new BillDAO();
        customerDAO = new CustomerDAO();
        bookDAO = new BookDAO();
        webService = new BillWebService();
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
                    listBills(request, response);
                    break;
                case "create":
                    showCreateForm(request, response);
                    break;
                case "view":
                    viewBill(request, response);
                    break;
                case "search":
                    searchBills(request, response);
                    break;
                default:
                    listBills(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error processing request: " + e.getMessage());
            request.getRequestDispatcher("/bills.jsp").forward(request, response);
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
            if ("create".equals(action)) {
                createBill(request, response);
            } else {
                listBills(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error processing request: " + e.getMessage());
            request.getRequestDispatcher("/bills.jsp").forward(request, response);
        }
    }

    private void listBills(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<Bill> bills = billDAO.getAllBills();
        request.setAttribute("bills", bills);
        request.getRequestDispatcher("/bills.jsp").forward(request, response);
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<Customer> customers = customerDAO.getAllCustomers();
        String billNumber = billDAO.generateBillNumber();

        request.setAttribute("customers", customers);
        request.setAttribute("billNumber", billNumber);
        request.getRequestDispatcher("/create-bill.jsp").forward(request, response);
    }

    private void viewBill(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        int id = Integer.parseInt(request.getParameter("id"));
        Bill bill = billDAO.getBillById(id);

        if (bill != null) {
            request.setAttribute("bill", bill);
            request.getRequestDispatcher("/view-bill.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Bill not found");
            listBills(request, response);
        }
    }

    private void createBill(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        // Create bill
        Bill bill = new Bill();
        bill.setBillNumber(request.getParameter("billNumber"));
        bill.setCustomerId(Integer.parseInt(request.getParameter("customerId")));
        bill.setCashierId(user.getId());

        // Create bill items
        List<BillItem> billItems = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;

        int itemCount = 1;
        while (request.getParameter("book_id_" + itemCount) != null) {
            String bookIdStr = request.getParameter("book_id_" + itemCount);
            String quantityStr = request.getParameter("quantity_" + itemCount);
            String priceStr = request.getParameter("price_" + itemCount);
            String taxStr = request.getParameter("tax_" + itemCount);
            String discountStr = request.getParameter("discount_" + itemCount);

            if (bookIdStr != null && !bookIdStr.isEmpty() &&
                    quantityStr != null && !quantityStr.isEmpty()) {

                BillItem item = new BillItem();
                item.setBookId(Integer.parseInt(bookIdStr));
                item.setQuantity(Integer.parseInt(quantityStr));
                item.setUnitPrice(new BigDecimal(priceStr));
                item.setTaxRate(new BigDecimal(taxStr != null ? taxStr : "0"));
                item.setDiscountRate(new BigDecimal(discountStr != null ? discountStr : "0"));
                item.calculateAmounts();

                billItems.add(item);

                BigDecimal itemSubtotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                subtotal = subtotal.add(itemSubtotal);
                totalTax = totalTax.add(item.getTaxAmount());
                totalDiscount = totalDiscount.add(item.getDiscountAmount());
            }
            itemCount++;
        }

        bill.setSubtotal(subtotal);
        bill.setTaxAmount(totalTax);
        bill.setDiscountAmount(totalDiscount);
        bill.setTotalAmount(subtotal.subtract(totalDiscount).add(totalTax));

        if (billDAO.createBill(bill, billItems)) {
            // Send email to customer
            Customer customer = customerDAO.getCustomerById(bill.getCustomerId());
            if (customer != null && customer.getEmail() != null && !customer.getEmail().isEmpty()) {
                EmailUtil.sendBillEmail(customer.getEmail(), bill.getBillNumber(), bill.getTotalAmount().doubleValue());
            }

            request.setAttribute("success", "Bill created successfully!");
            request.setAttribute("billNumber", bill.getBillNumber());
            request.setAttribute("printBill", true);
        } else {
            request.setAttribute("error", "Failed to create bill");
        }

        listBills(request, response);
    }

    private void searchBills(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String searchTerm = request.getParameter("search");
        List<Bill> bills;

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            bills = billDAO.searchBills(searchTerm);
        } else {
            bills = billDAO.getAllBills();
        }

        request.setAttribute("bills", bills);
        request.setAttribute("searchTerm", searchTerm);
        request.getRequestDispatcher("/bills.jsp").forward(request, response);
    }
}