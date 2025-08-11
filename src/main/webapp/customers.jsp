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
    <title>Customers - Pahana Edu Book Shop</title>
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
            <li><a href="customers?action=list" class="active">Customers</a></li>
            <li><a href="books?action=list">Books</a></li>
            <li><a href="bills?action=list">Bills</a></li>
            <% if ("cashier".equals(user.getRole())) { %>
                <li><a href="bills?action=create">Create Bill</a></li>
            <% } %>
        </ul>
    </nav>

    <main class="main-content">
        <div class="card fade-in">
            <div class="card-header">
                Customer Management
            </div>
            <div class="card-body">
                <!-- Alerts -->
                <c:if test="${not empty success}">
                    <div class="alert alert-success">${success}</div>
                </c:if>
                <c:if test="${not empty error}">
                    <div class="alert alert-error">${error}</div>
                </c:if>

                <!-- Search and Add Section -->
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
                    <div class="search-box">
                        <form action="customers" method="get" style="display: flex;">
                            <input type="hidden" name="action" value="search">
                            <input type="text" name="search" placeholder="Search customers..." value="${searchTerm}" class="form-control">
                            <button type="submit" class="btn btn-primary">Search</button>
                        </form>
                    </div>
                    <a href="customers?action=add" class="btn btn-success">Add New Customer</a>
                </div>

                <!-- Customers Table -->
                <c:choose>
                    <c:when test="${not empty customers}">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Account Number</th>
                                    <th>Name</th>
                                    <th>Address</th>
                                    <th>Telephone</th>
                                    <th>Email</th>
                                    <th>Created Date</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="customer" items="${customers}">
                                    <tr>
                                        <td>${customer.accountNumber}</td>
                                        <td>${customer.name}</td>
                                        <td>${customer.address}</td>
                                        <td>${customer.telephone}</td>
                                        <td>${customer.email}</td>
                                        <td>${customer.createdAt}</td>
                                        <td>
                                            <a href="customers?action=edit&id=${customer.id}" class="btn btn-warning btn-sm">Edit</a>
                                            <% if ("admin".equals(user.getRole())) { %>
                                                <a href="customers?action=delete&id=${customer.id}" 
                                                   class="btn btn-danger btn-sm" 
                                                   onclick="return confirm('Are you sure you want to delete this customer?')">Delete</a>
                                            <% } %>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <p>No customers found.</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </main>

    <!-- Help Modal -->
    <div id="helpModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h3>Customer Management Help</h3>
                <span class="close" onclick="closeHelp()">&times;</span>
            </div>
            <div class="modal-body">
                <h4>Customer Management Features:</h4>
                <ul>
                    <li><strong>Add Customer:</strong> Click "Add New Customer" to create a new customer account</li>
                    <li><strong>Search:</strong> Use the search box to find customers by name, account number, email, or phone</li>
                    <li><strong>Edit:</strong> Click "Edit" to modify customer information</li>
                    <% if ("admin".equals(user.getRole())) { %>
                        <li><strong>Delete:</strong> Click "Delete" to remove a customer (Admin only)</li>
                    <% } %>
                </ul>
                
                <h4>Customer Information:</h4>
                <ul>
                    <li>Account numbers are automatically generated</li>
                    <li>All fields are required for complete customer records</li>
                    <li>Email addresses are used for notifications</li>
                    <li>Phone numbers should include area codes</li>
                </ul>
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
    </script>
</body>
</html>