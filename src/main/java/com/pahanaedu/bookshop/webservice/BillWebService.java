package com.pahanaedu.bookshop.webservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pahanaedu.bookshop.bill.model.Bill;
import com.pahanaedu.bookshop.bill.model.BillItem;
import com.pahanaedu.bookshop.dao.BillDAO;
import com.pahanaedu.bookshop.dao.CustomerDAO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Web Service for Bill operations
 * Provides JSON API endpoints for distributed access
 */
public class BillWebService extends HttpServlet {
    private BillDAO billDAO;
    private CustomerDAO customerDAO;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        billDAO = new BillDAO();
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
                // RESTful URL: /api/bills/123
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length > 1) {
                    int billId = Integer.parseInt(pathParts[1]);
                    Bill bill = billDAO.getBillById(billId);
                    if (bill != null) {
                        result.put("success", true);
                        result.put("data", convertBillToMap(bill));
                    } else {
                        result.put("success", false);
                        result.put("message", "Bill not found");
                    }
                }
            } else if ("list".equals(action)) {
                // Get all bills
                List<Bill> bills = billDAO.getAllBills();
                result.put("success", true);
                result.put("data", convertBillsToMaps(bills));
                result.put("count", bills.size());

            } else if ("search".equals(action)) {
                // Search bills
                String searchTerm = request.getParameter("term");
                List<Bill> bills = billDAO.searchBills(searchTerm);
                result.put("success", true);
                result.put("data", convertBillsToMaps(bills));
                result.put("count", bills.size());
                result.put("searchTerm", searchTerm);

            } else if ("count".equals(action)) {
                // Get bill count
                int count = billDAO.getBillCount();
                result.put("success", true);
                result.put("count", count);

            } else if ("totalSales".equals(action)) {
                // Get total sales
                BigDecimal totalSales = billDAO.getTotalSales();
                result.put("success", true);
                result.put("totalSales", totalSales);

            } else if ("recent".equals(action)) {
                // Get recent bills
                int limit = Integer.parseInt(request.getParameter("limit") != null ? request.getParameter("limit") : "5");
                List<Bill> bills = billDAO.getRecentBills(limit);
                result.put("success", true);
                result.put("data", convertBillsToMaps(bills));
                result.put("count", bills.size());

            } else if ("generateBillNumber".equals(action)) {
                // Generate new bill number
                String billNumber = billDAO.generateBillNumber();
                result.put("success", true);
                result.put("billNumber", billNumber);

            } else {
                // Default: return all bills
                List<Bill> bills = billDAO.getAllBills();
                result.put("success", true);
                result.put("data", convertBillsToMaps(bills));
                result.put("count", bills.size());
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

            @SuppressWarnings("unchecked")
            Map<String, Object> billData = objectMapper.readValue(jsonBuffer.toString(), Map.class);

            // Create bill from JSON data
            Bill bill = new Bill();
            bill.setBillNumber((String) billData.get("billNumber"));
            bill.setCustomerId((Integer) billData.get("customerId"));
            bill.setCashierId((Integer) billData.get("cashierId"));

            // Create bill items
            List<BillItem> billItems = new ArrayList<>();
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> itemsData = (List<Map<String, Object>>) billData.get("items");

            BigDecimal subtotal = BigDecimal.ZERO;
            BigDecimal totalTax = BigDecimal.ZERO;
            BigDecimal totalDiscount = BigDecimal.ZERO;

            for (Map<String, Object> itemData : itemsData) {
                BillItem item = new BillItem();
                item.setBookId((Integer) itemData.get("bookId"));
                item.setQuantity((Integer) itemData.get("quantity"));
                item.setUnitPrice(new BigDecimal(itemData.get("unitPrice").toString()));
                item.setTaxRate(new BigDecimal(itemData.get("taxRate").toString()));
                item.setDiscountRate(new BigDecimal(itemData.get("discountRate").toString()));
                item.calculateAmounts();

                billItems.add(item);

                BigDecimal itemSubtotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                subtotal = subtotal.add(itemSubtotal);
                totalTax = totalTax.add(item.getTaxAmount());
                totalDiscount = totalDiscount.add(item.getDiscountAmount());
            }

            bill.setSubtotal(subtotal);
            bill.setTaxAmount(totalTax);
            bill.setDiscountAmount(totalDiscount);
            bill.setTotalAmount(subtotal.subtract(totalDiscount).add(totalTax));

            if (billDAO.createBill(bill, billItems)) {
                result.put("success", true);
                result.put("message", "Bill created successfully");
                result.put("data", convertBillToMap(bill));
            } else {
                result.put("success", false);
                result.put("message", "Failed to create bill");
            }

            response.getWriter().write(objectMapper.writeValueAsString(result));

        } catch (Exception e) {
            handleError(response, e);
        }
    }

    private List<Map<String, Object>> convertBillsToMaps(List<Bill> bills) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Bill bill : bills) {
            result.add(convertBillToMap(bill));
        }
        return result;
    }

    private Map<String, Object> convertBillToMap(Bill bill) {
        Map<String, Object> billMap = new HashMap<>();
        billMap.put("id", bill.getId());
        billMap.put("billNumber", bill.getBillNumber());
        billMap.put("customerId", bill.getCustomerId());
        billMap.put("cashierId", bill.getCashierId());
        billMap.put("subtotal", bill.getSubtotal());
        billMap.put("taxAmount", bill.getTaxAmount());
        billMap.put("discountAmount", bill.getDiscountAmount());
        billMap.put("totalAmount", bill.getTotalAmount());
        billMap.put("paymentStatus", bill.getPaymentStatus());
        billMap.put("createdAt", bill.getCreatedAt());
        billMap.put("customerName", bill.getCustomerName());
        billMap.put("cashierName", bill.getCashierName());

        if (bill.getBillItems() != null) {
            List<Map<String, Object>> items = new ArrayList<>();
            for (BillItem item : bill.getBillItems()) {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("id", item.getId());
                itemMap.put("bookId", item.getBookId());
                itemMap.put("quantity", item.getQuantity());
                itemMap.put("unitPrice", item.getUnitPrice());
                itemMap.put("taxRate", item.getTaxRate());
                itemMap.put("discountRate", item.getDiscountRate());
                itemMap.put("taxAmount", item.getTaxAmount());
                itemMap.put("discountAmount", item.getDiscountAmount());
                itemMap.put("lineTotal", item.getLineTotal());
                itemMap.put("bookTitle", item.getBookTitle());
                itemMap.put("bookAuthor", item.getBookAuthor());
                items.add(itemMap);
            }
            billMap.put("items", items);
        }

        return billMap;
    }

    private void handleError(HttpServletResponse response, Exception e) throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", "Server error: " + e.getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}