<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.pahanaedu.bookshop.business.user.model.User" %>
<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null || !"admin".equals(currentUser.getRole())) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${action == 'add' ? 'Add' : 'Edit'} User - Pahana Edu Book Shop</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <header class="header">
        <div class="header-content">
            <div class="logo">
                <h1>Pahana Edu Book Shop</h1>
            </div>
            <div class="user-info">
                <span>Welcome, <%= currentUser.getFullName() %> (<%= currentUser.getRole().toUpperCase() %>)</span>
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
                ${action == 'add' ? 'Add New User' : 'Edit User'}
            </div>
            <div class="card-body">
                <form action="users" method="post">
                    <input type="hidden" name="action" value="${action}">
                    <c:if test="${action == 'edit'}">
                        <input type="hidden" name="id" value="${user.id}">
                    </c:if>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label for="username">Username:</label>
                            <input type="text" id="username" name="username" class="form-control" 
                                   value="${user.username}" required>
                        </div>
                        <div class="form-group">
                            <label for="role">Role:</label>
                            <select id="role" name="role" class="form-control" required>
                                <option value="">Select Role</option>
                                <option value="admin" ${user.role == 'admin' ? 'selected' : ''}>Admin</option>
                                <option value="cashier" ${user.role == 'cashier' ? 'selected' : ''}>Cashier</option>
                            </select>
                        </div>
                    </div>
                    
                    <c:if test="${action == 'add'}">
                        <div class="form-group">
                            <label for="password">Password:</label>
                            <input type="password" id="password" name="password" class="form-control" required>
                        </div>
                    </c:if>
                    
                    <div class="form-group">
                        <label for="fullName">Full Name:</label>
                        <input type="text" id="fullName" name="fullName" class="form-control" 
                               value="${user.fullName}" required>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label for="email">Email:</label>
                            <input type="email" id="email" name="email" class="form-control" 
                                   value="${user.email}">
                        </div>
                        <div class="form-group">
                            <label for="phone">Phone:</label>
                            <input type="tel" id="phone" name="phone" class="form-control" 
                                   value="${user.phone}">
                        </div>
                    </div>
                    
                    <c:if test="${action == 'edit'}">
                        <div class="alert alert-info">
                            <strong>Note:</strong> Password cannot be changed through this form. Contact system administrator for password resets.
                        </div>
                    </c:if>
                    
                    <div style="margin-top: 20px;">
                        <button type="submit" class="btn btn-primary">
                            ${action == 'add' ? 'Add User' : 'Update User'}
                        </button>
                        <a href="users?action=list" class="btn btn-secondary">Cancel</a>
                    </div>
                </form>
            </div>
        </div>
    </main>
</body>
</html>