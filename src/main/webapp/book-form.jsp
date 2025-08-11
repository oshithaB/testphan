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
    <title>${action == 'add' ? 'Add' : 'Edit'} Book - Pahana Edu Book Shop</title>
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
            <li><a href="users?action=list">Users</a></li>
            <li><a href="customers?action=list">Customers</a></li>
            <li><a href="books?action=list" class="active">Books</a></li>
            <li><a href="bills?action=list">Bills</a></li>
        </ul>
    </nav>

    <main class="main-content">
        <div class="card fade-in">
            <div class="card-header">
                ${action == 'add' ? 'Add New Book' : 'Edit Book'}
            </div>
            <div class="card-body">
                <form action="books" method="post">
                    <input type="hidden" name="action" value="${action}">
                    <c:if test="${action == 'edit'}">
                        <input type="hidden" name="id" value="${book.id}">
                    </c:if>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label for="title">Book Title:</label>
                            <input type="text" id="title" name="title" class="form-control" 
                                   value="${book.title}" required>
                        </div>
                        <div class="form-group">
                            <label for="author">Author:</label>
                            <input type="text" id="author" name="author" class="form-control" 
                                   value="${book.author}" required>
                        </div>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label for="isbn">ISBN:</label>
                            <input type="text" id="isbn" name="isbn" class="form-control" 
                                   value="${book.isbn}">
                        </div>
                        <div class="form-group">
                            <label for="category">Category:</label>
                            <select id="category" name="category" class="form-control" required>
                                <option value="">Select Category</option>
                                <option value="Programming" ${book.category == 'Programming' ? 'selected' : ''}>Programming</option>
                                <option value="Database" ${book.category == 'Database' ? 'selected' : ''}>Database</option>
                                <option value="Web Development" ${book.category == 'Web Development' ? 'selected' : ''}>Web Development</option>
                                <option value="Computer Science" ${book.category == 'Computer Science' ? 'selected' : ''}>Computer Science</option>
                                <option value="Security" ${book.category == 'Security' ? 'selected' : ''}>Security</option>
                                <option value="Networking" ${book.category == 'Networking' ? 'selected' : ''}>Networking</option>
                                <option value="Mathematics" ${book.category == 'Mathematics' ? 'selected' : ''}>Mathematics</option>
                                <option value="Other" ${book.category == 'Other' ? 'selected' : ''}>Other</option>
                            </select>
                        </div>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label for="price">Price (Rs.):</label>
                            <input type="number" id="price" name="price" class="form-control" 
                                   value="${book.price}" step="0.01" min="0" required>
                        </div>
                        <div class="form-group">
                            <label for="quantity">Quantity:</label>
                            <input type="number" id="quantity" name="quantity" class="form-control" 
                                   value="${book.quantity}" min="0" required>
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label for="description">Description:</label>
                        <textarea id="description" name="description" class="form-control" rows="4">${book.description}</textarea>
                    </div>
                    
                    <div style="margin-top: 20px;">
                        <button type="submit" class="btn btn-primary">
                            ${action == 'add' ? 'Add Book' : 'Update Book'}
                        </button>
                        <a href="books?action=list" class="btn btn-secondary">Cancel</a>
                    </div>
                </form>
            </div>
        </div>
    </main>
</body>
</html>