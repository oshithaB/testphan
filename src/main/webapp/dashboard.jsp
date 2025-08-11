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
    <title>Dashboard - Pahana Edu Book Shop</title>
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
            <li><a href="dashboard" class="active">Dashboard</a></li>
            <% if ("admin".equals(user.getRole())) { %>
                <li><a href="users?action=list">Users</a></li>
            <% } %>
            <li><a href="customers?action=list">Customers</a></li>
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
                Dashboard Overview
            </div>
            <div class="card-body">
                <div class="stats-grid">
                    <div class="stat-card customers">
                        <span class="stat-number">${customerCount}</span>
                        <span class="stat-label">Total Customers</span>
                    </div>
                    <div class="stat-card books">
                        <span class="stat-number">${bookCount}</span>
                        <span class="stat-label">Books in Inventory</span>
                    </div>
                    <div class="stat-card bills">
                        <span class="stat-number">${billCount}</span>
                        <span class="stat-label">Total Bills</span>
                    </div>
                    <div class="stat-card">
                        <span class="stat-number">Rs. ${totalSales}</span>
                        <span class="stat-label">Total Sales</span>
                    </div>
                </div>

                <div class="card">
                    <div class="card-header">Recent Activity</div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${not empty recentBills}">
                                <table class="table">
                                    <thead>
                                        <tr>
                                            <th>Bill Number</th>
                                            <th>Customer</th>
                                            <th>Amount</th>
                                            <th>Date</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="bill" items="${recentBills}">
                                            <tr>
                                                <td>${bill.billNumber}</td>
                                                <td>${bill.customerName}</td>
                                                <td>Rs. ${bill.totalAmount}</td>
                                                <td>${bill.createdAt}</td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </c:when>
                            <c:otherwise>
                                <p>No recent activity found.</p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </main>

    <!-- Help Modal -->
    <div id="helpModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h3>System Help Guide</h3>
                <span class="close" onclick="closeHelp()">&times;</span>
            </div>
            <div class="modal-body">
                <% if ("admin".equals(user.getRole())) { %>
                    <h4>Admin Functions:</h4>
                    <ul>
                        <li><strong>Dashboard:</strong> View system overview and statistics</li>
                        <li><strong>Users:</strong> Add, edit, delete, and search system users</li>
                        <li><strong>Customers:</strong> Manage customer information</li>
                        <li><strong>Books:</strong> Manage inventory (add, edit, delete books)</li>
                        <li><strong>Bills:</strong> View and search all bills (read-only)</li>
                    </ul>
                <% } else { %>
                    <h4>Cashier Functions:</h4>
                    <ul>
                        <li><strong>Dashboard:</strong> View system overview</li>
                        <li><strong>Customers:</strong> Create and search customers</li>
                        <li><strong>Books:</strong> View and search inventory</li>
                        <li><strong>Create Bill:</strong> Create new bills for customers</li>
                        <li><strong>Bills:</strong> View and search previous bills</li>
                    </ul>
                <% } %>
                
                <h4>General Tips:</h4>
                <ul>
                    <li>Use the search functionality to quickly find records</li>
                    <li>When creating bills, type customer/book names for auto-suggestions</li>
                    <li>Bills can be printed using the browser's print function</li>
                    <li>All passwords are securely hashed</li>
                    <li>System automatically sends email notifications</li>
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
        
        // Close modal when clicking outside
        window.onclick = function(event) {
            const modal = document.getElementById('helpModal');
            if (event.target === modal) {
                modal.style.display = 'none';
            }
        }
    </script>
</body>
</html>