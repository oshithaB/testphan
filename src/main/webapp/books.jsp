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
    <title>Books - Pahana Edu Book Shop</title>
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
            <li><a href="books?action=list" class="active">Books</a></li>
            <li><a href="bills?action=list">Bills</a></li>
            <% if ("cashier".equals(user.getRole())) { %>
                <li><a href="bills?action=create">Create Bill</a></li>
            <% } %>
        </ul>
    </nav>

    <main class="main-content">
        <div class="card fade-in">
            <div class="card-header">
                Book Inventory Management
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
                        <form action="books" method="get" style="display: flex;">
                            <input type="hidden" name="action" value="search">
                            <input type="text" name="search" placeholder="Search books..." value="${searchTerm}" class="form-control">
                            <button type="submit" class="btn btn-primary">Search</button>
                        </form>
                    </div>
                    <% if ("admin".equals(user.getRole())) { %>
                        <a href="books?action=add" class="btn btn-success">Add New Book</a>
                    <% } %>
                </div>

                <!-- Books Table -->
                <c:choose>
                    <c:when test="${not empty books}">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Title</th>
                                    <th>Author</th>
                                    <th>ISBN</th>
                                    <th>Category</th>
                                    <th>Price</th>
                                    <th>Quantity</th>
                                    <th>Status</th>
                                    <% if ("admin".equals(user.getRole())) { %>
                                        <th>Actions</th>
                                    <% } %>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="book" items="${books}">
                                    <tr>
                                        <td>${book.title}</td>
                                        <td>${book.author}</td>
                                        <td>${book.isbn}</td>
                                        <td>${book.category}</td>
                                        <td>Rs. ${book.price}</td>
                                        <td>
                                            <span class="${book.quantity <= 5 ? 'text-danger' : book.quantity <= 10 ? 'text-warning' : 'text-success'}">
                                                ${book.quantity}
                                            </span>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${book.quantity == 0}">
                                                    <span style="color: #e74c3c; font-weight: bold;">Out of Stock</span>
                                                </c:when>
                                                <c:when test="${book.quantity <= 5}">
                                                    <span style="color: #f39c12; font-weight: bold;">Low Stock</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span style="color: #27ae60; font-weight: bold;">In Stock</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <% if ("admin".equals(user.getRole())) { %>
                                            <td>
                                                <a href="books?action=edit&id=${book.id}" class="btn btn-warning btn-sm">Edit</a>
                                                <a href="books?action=delete&id=${book.id}" 
                                                   class="btn btn-danger btn-sm" 
                                                   onclick="return confirm('Are you sure you want to delete this book?')">Delete</a>
                                            </td>
                                        <% } %>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <p>No books found in inventory.</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </main>

    <!-- Help Modal -->
    <div id="helpModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h3>Book Inventory Help</h3>
                <span class="close" onclick="closeHelp()">&times;</span>
            </div>
            <div class="modal-body">
                <h4>Book Management Features:</h4>
                <ul>
                    <% if ("admin".equals(user.getRole())) { %>
                        <li><strong>Add Book:</strong> Click "Add New Book" to add books to inventory</li>
                        <li><strong>Edit:</strong> Click "Edit" to modify book information and quantities</li>
                        <li><strong>Delete:</strong> Click "Delete" to remove books from inventory</li>
                    <% } else { %>
                        <li><strong>View Only:</strong> As a cashier, you can view and search inventory</li>
                    <% } %>
                    <li><strong>Search:</strong> Use the search box to find books by title, author, ISBN, or category</li>
                    <li><strong>Stock Status:</strong> Colors indicate stock levels (Red: Out of Stock, Orange: Low Stock, Green: In Stock)</li>
                </ul>
                
                <h4>Stock Management:</h4>
                <ul>
                    <li>Red quantities (≤5) indicate low stock that needs replenishment</li>
                    <li>Orange quantities (6-10) indicate moderate stock levels</li>
                    <li>Green quantities (>10) indicate healthy stock levels</li>
                    <li>Out of stock items cannot be added to bills</li>
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