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
    <title>${action == 'add' ? 'Add' : 'Edit'} Customer - Pahana Edu Book Shop</title>
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
                ${action == 'add' ? 'Add New Customer' : 'Edit Customer'}
            </div>
            <div class="card-body">
                <form action="customers" method="post">
                    <input type="hidden" name="action" value="${action}">
                    <c:if test="${action == 'edit'}">
                        <input type="hidden" name="id" value="${customer.id}">
                    </c:if>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label for="accountNumber">Account Number:</label>
                            <input type="text" id="accountNumber" name="accountNumber" class="form-control" 
                                   value="${action == 'add' ? accountNumber : customer.accountNumber}" readonly>
                        </div>
                        <div class="form-group">
                            <label for="name">Full Name:</label>
                            <input type="text" id="name" name="name" class="form-control" 
                                   value="${customer.name}" required>
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label for="address">Address:</label>
                        <textarea id="address" name="address" class="form-control" rows="3" required>${customer.address}</textarea>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label for="telephone">Telephone:</label>
                            <input type="tel" id="telephone" name="telephone" class="form-control" 
                                   value="${customer.telephone}" required>
                        </div>
                        <div class="form-group">
                            <label for="email">Email:</label>
                            <input type="email" id="email" name="email" class="form-control" 
                                   value="${customer.email}">
                        </div>
                    </div>
                    
                    <div style="margin-top: 20px;">
                        <button type="submit" class="btn btn-primary">
                            ${action == 'add' ? 'Add Customer' : 'Update Customer'}
                        </button>
                        <a href="customers?action=list" class="btn btn-secondary">Cancel</a>
                    </div>
                </form>
            </div>
        </div>
    </main>
</body>
</html>