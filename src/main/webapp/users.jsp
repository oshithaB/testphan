<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.pahanaedu.bookshop.business.user.model.User" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null || !"admin".equals(user.getRole())) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Users - Pahana Edu Book Shop</title>
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
            <li><a href="users?action=list" class="active">Users</a></li>
            <li><a href="customers?action=list">Customers</a></li>
            <li><a href="books?action=list">Books</a></li>
            <li><a href="bills?action=list">Bills</a></li>
        </ul>
    </nav>

    <main class="main-content">
        <div class="card fade-in">
            <div class="card-header">
                User Management (Admin Only)
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
                        <form action="users" method="get" style="display: flex;">
                            <input type="hidden" name="action" value="search">
                            <input type="text" name="search" placeholder="Search users..." value="${searchTerm}" class="form-control">
                            <button type="submit" class="btn btn-primary">Search</button>
                        </form>
                    </div>
                    <a href="users?action=add" class="btn btn-success">Add New User</a>
                </div>

                <!-- Users Table -->
                <c:choose>
                    <c:when test="${not empty users}">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Username</th>
                                    <th>Full Name</th>
                                    <th>Role</th>
                                    <th>Email</th>
                                    <th>Phone</th>
                                    <th>Created Date</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="userItem" items="${users}">
                                    <tr>
                                        <td>${userItem.username}</td>
                                        <td>${userItem.fullName}</td>
                                        <td>
                                            <span class="badge ${userItem.role == 'admin' ? 'badge-danger' : 'badge-info'}">
                                                ${userItem.role.toUpperCase()}
                                            </span>
                                        </td>
                                        <td>${userItem.email}</td>
                                        <td>${userItem.phone}</td>
                                        <td>${userItem.createdAt}</td>
                                        <td>
                                            <a href="users?action=edit&id=${userItem.id}" class="btn btn-warning btn-sm">Edit</a>
                                            <c:if test="${userItem.id != user.id}">
                                                <a href="users?action=delete&id=${userItem.id}" 
                                                   class="btn btn-danger btn-sm" 
                                                   onclick="return confirm('Are you sure you want to delete this user?')">Delete</a>
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <p>No users found.</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </main>

    <!-- Help Modal -->
    <div id="helpModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h3>User Management Help</h3>
                <span class="close" onclick="closeHelp()">&times;</span>
            </div>
            <div class="modal-body">
                <h4>User Management Features (Admin Only):</h4>
                <ul>
                    <li><strong>Add User:</strong> Click "Add New User" to create admin or cashier accounts</li>
                    <li><strong>Search:</strong> Use the search box to find users by username, name, or email</li>
                    <li><strong>Edit:</strong> Click "Edit" to modify user information (except passwords)</li>
                    <li><strong>Delete:</strong> Click "Delete" to remove users (cannot delete yourself)</li>
                </ul>
                
                <h4>User Roles:</h4>
                <ul>
                    <li><strong>Admin:</strong> Full system access including user management</li>
                    <li><strong>Cashier:</strong> Can manage customers, view inventory, create bills</li>
                </ul>
                
                <h4>Security:</h4>
                <ul>
                    <li>All passwords are securely hashed</li>
                    <li>Welcome emails are sent to new users</li>
                    <li>Users cannot delete their own accounts</li>
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

    <style>
        .badge {
            padding: 4px 8px;
            border-radius: 12px;
            font-size: 11px;
            font-weight: bold;
            text-transform: uppercase;
        }
        
        .badge-danger {
            background-color: #e74c3c;
            color: white;
        }
        
        .badge-info {
            background-color: #3498db;
            color: white;
        }
    </style>
</body>
</html>