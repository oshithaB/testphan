<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.pahanaedu.bookshop.business.user.model.User" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null || !"cashier".equals(user.getRole())) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Bill - Pahana Edu Book Shop</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <header class="header">
        <div class="header-content">
            <div class="logo">
                <h1>Pahana Edu Book Shop</h1>
            </div>
            <div class="user-info">
                <span>Welcome, <%= user.getFullName() %> (<%= user.getRole().toUpperCase() %>)</span>
                <a href="logout" class="btn btn-danger btn-sm">Logout</a>
            </div>
        </div>
    </header>

    <nav class="nav-menu">
        <ul>
            <li><a href="dashboard">Dashboard</a></li>
            <li><a href="customers?action=list">Customers</a></li>
            <li><a href="books?action=list">Books</a></li>
            <li><a href="bills?action=list">Bills</a></li>
            <li><a href="bills?action=create" class="active">Create Bill</a></li>
        </ul>
    </nav>

    <main class="main-content">
        <div class="card fade-in">
            <div class="card-header">
                Create New Bill
            </div>
            <div class="card-body">
                <form action="bills" method="post" onsubmit="return validateBillForm()">
                    <input type="hidden" name="action" value="create">
                    
                    <!-- Bill Header -->
                    <div class="form-row">
                        <div class="form-group">
                            <label for="billNumber">Bill Number:</label>
                            <input type="text" id="billNumber" name="billNumber" class="form-control" 
                                   value="${billNumber}" readonly>
                        </div>
                        <div class="form-group">
                            <label for="customerId">Customer:</label>
                            <select id="customerId" name="customerId" class="form-control" required>
                                <option value="">Select Customer</option>
                                <c:forEach var="customer" items="${customers}">
                                    <option value="${customer.id}">${customer.name} - ${customer.accountNumber}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                    
                    <!-- Bill Items Table -->
                    <div class="card bill-items-table">
                        <div class="card-header">
                            Bill Items
                            <button type="button" class="btn btn-success btn-sm" onclick="addBillItemRow()" style="float: right;">Add Item</button>
                        </div>
                        <div class="card-body">
                            <table class="table" id="billItemsTable">
                                <thead>
                                    <tr>
                                        <th>Item</th>
                                        <th>Price</th>
                                        <th>Quantity</th>
                                        <th>Tax (%)</th>
                                        <th>Discount (%)</th>
                                        <th>Amount</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <!-- Initial row will be added by JavaScript -->
                                </tbody>
                            </table>
                        </div>
                    </div>
                    
                    <!-- Bill Total -->
                    <div class="bill-total">
                        <div class="total-row">
                            <span>Subtotal:</span>
                            <span>Rs. <span id="billSubtotal">0.00</span></span>
                        </div>
                        <div class="total-row">
                            <span>Total Discount:</span>
                            <span>Rs. <span id="billDiscountAmount">0.00</span></span>
                        </div>
                        <div class="total-row">
                            <span>Total Tax:</span>
                            <span>Rs. <span id="billTaxAmount">0.00</span></span>
                        </div>
                        <div class="total-row final-total">
                            <span>Total Amount:</span>
                            <span>Rs. <span id="billTotal">0.00</span></span>
                        </div>
                    </div>
                    
                    <div style="margin-top: 20px;">
                        <button type="submit" class="btn btn-primary">Create Bill</button>
                        <a href="bills?action=list" class="btn btn-secondary">Cancel</a>
                    </div>
                </form>
            </div>
        </div>
    </main>

    <script src="js/autocomplete.js"></script>
    <script>
        // Add initial row when page loads
        document.addEventListener('DOMContentLoaded', function() {
            addBillItemRow();
        });
    </script>
</body>
</html>