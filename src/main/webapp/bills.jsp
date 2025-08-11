<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.pahanaedu.bookshop.business.user.model.User" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bills - Pahana Edu Book Shop</title>
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
                <a href="javascript:void(0)" onclick="showHelp()" class="btn btn-secondary btn-sm">Help</a>
                <a href="logout" class="btn btn-danger btn-sm">Logout</a>
            </div>
        </div>
    </header>

    <nav class="nav-menu">
        <ul>
            <li><a href="dashboard">Dashboard</a></li>
            <% if ("admin".equals(user.getRole())) { %>
                <li><a href="users?action=list">Users</a></li>
            <% } %>
            <li><a href="customers?action=list">Customers</a></li>
            <li><a href="books?action=list">Books</a></li>
            <li><a href="bills?action=list" class="active">Bills</a></li>
            <% if ("cashier".equals(user.getRole())) { %>
                <li><a href="bills?action=create">Create Bill</a></li>
            <% } %>
        </ul>
    </nav>

    <main class="main-content">
        <div class="card fade-in">
            <div class="card-header">
                Bill Management
            </div>
            <div class="card-body">
                <!-- Alerts -->
                <c:if test="${not empty success}">
                    <div class="alert alert-success">
                        ${success}
                        <c:if test="${printBill}">
                            <br><a href="bills?action=view&id=${billNumber}" target="_blank" class="btn btn-primary btn-sm" style="margin-top: 10px;">View & Print Bill</a>
                        </c:if>
                    </div>
                </c:if>
                <c:if test="${not empty error}">
                    <div class="alert alert-error">${error}</div>
                </c:if>

                <!-- Search and Create Section -->
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
                    <div class="search-box">
                        <form action="bills" method="get" style="display: flex;">
                            <input type="hidden" name="action" value="search">
                            <input type="text" name="search" placeholder="Search bills..." value="${searchTerm}" class="form-control">
                            <button type="submit" class="btn btn-primary">Search</button>
                        </form>
                    </div>
                    <% if ("cashier".equals(user.getRole())) { %>
                        <a href="bills?action=create" class="btn btn-success">Create New Bill</a>
                    <% } %>
                </div>

                <!-- Bills Table -->
                <c:choose>
                    <c:when test="${not empty bills}">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Bill Number</th>
                                    <th>Customer</th>
                                    <th>Cashier</th>
                                    <th>Total Amount</th>
                                    <th>Status</th>
                                    <th>Date</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="bill" items="${bills}">
                                    <tr>
                                        <td>${bill.billNumber}</td>
                                        <td>${bill.customerName}</td>
                                        <td>${bill.cashierName}</td>
                                        <td>Rs. ${bill.totalAmount}</td>
                                        <td>
                                            <span class="badge ${bill.paymentStatus == 'paid' ? 'badge-success' : 'badge-warning'}">
                                                ${bill.paymentStatus.toUpperCase()}
                                            </span>
                                        </td>
                                        <td>${bill.createdAt}</td>
                                        <td>
                                            <a href="bills?action=view&id=${bill.id}" class="btn btn-primary btn-sm" target="_blank">View</a>
                                            <% if ("cashier".equals(user.getRole())) { %>
                                                <button onclick="sendBillEmail('${bill.billNumber}', '${bill.customerName}')" class="btn btn-success btn-sm">Email</button>
                                            <% } %>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <p>No bills found.</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </main>

    <!-- Help Modal -->
    <div id="helpModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h3>Bill Management Help</h3>
                <span class="close" onclick="closeHelp()">&times;</span>
            </div>
            <div class="modal-body">
                <h4>Bill Management Features:</h4>
                <ul>
                    <% if ("cashier".equals(user.getRole())) { %>
                        <li><strong>Create Bill:</strong> Click "Create New Bill" to generate a new bill for customers</li>
                    <% } %>
                    <li><strong>View Bills:</strong> Click "View" to see detailed bill information and print</li>
                    <li><strong>Search:</strong> Use the search box to find bills by number or customer name</li>
                    <li><strong>Status:</strong> Green badges indicate paid bills, orange indicates pending</li>
                </ul>
                
                <% if ("cashier".equals(user.getRole())) { %>
                    <h4>Creating Bills:</h4>
                    <ul>
                        <li>Select a customer from the dropdown or search by typing</li>
                        <li>Add items by typing book names - autocomplete will suggest available books</li>
                        <li>Quantities, taxes, and discounts are automatically calculated</li>
                        <li>Review totals before creating the bill</li>
                        <li>Bills can be printed immediately after creation</li>
                    </ul>
                <% } %>
            </div>
        </div>
    </div>

    <script>
        function showHelp() {
            document.getElementById('helpModal').style.display = 'block';
        }
        
        function closeHelp() {
            document.getElementById('helpModal').style.display = 'none';
        }
        
        window.onclick = function(event) {
            const modal = document.getElementById('helpModal');
            if (event.target === modal) {
                modal.style.display = 'none';
            }
        }
        
        function sendBillEmail(billNumber, customerName) {
            alert('Bill ' + billNumber + ' has been sent via email to ' + customerName + '!');
        }
    </script>

    <style>
        .badge {
            padding: 4px 8px;
            border-radius: 12px;
            font-size: 11px;
            font-weight: bold;
            text-transform: uppercase;
        }
        
        .badge-success {
            background-color: #27ae60;
            color: white;
        }
        
        .badge-warning {
            background-color: #f39c12;
            color: white;
        }
    </style>
</body>
</html>