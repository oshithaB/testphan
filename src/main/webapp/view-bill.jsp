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
    <title>Bill ${bill.billNumber} - Pahana Edu Book Shop</title>
    <link rel="stylesheet" href="css/style.css">
    <style>
        @media print {
            .no-print { display: none !important; }
            .print-area { width: 100%; }
            body { font-size: 12px; }
        }
    </style>
</head>
<body>
    <div class="no-print">
        <header class="header">
            <div class="header-content">
                <div class="logo">
                    <h1>Pahana Edu Book Shop</h1>
                </div>
                <div class="user-info">
                    <span>Welcome, <%= user.getFullName() %> (<%= user.getRole().toUpperCase() %>)</span>
                    <button onclick="printBill()" class="btn btn-primary btn-sm">Print Bill</button>
                    <% if ("cashier".equals(user.getRole())) { %>
                        <button onclick="sendBillEmail()" class="btn btn-success btn-sm">Send via Email</button>
                    <% } %>
                    <a href="bills?action=list" class="btn btn-secondary btn-sm">Back to Bills</a>
                    <a href="logout" class="btn btn-danger btn-sm">Logout</a>
                </div>
            </div>
        </header>
    </div>

    <main class="main-content print-area">
        <div class="card">
            <div class="card-body">
                <!-- Bill Header -->
                <div style="text-align: center; margin-bottom: 30px;">
                    <h1 style="color: #2c3e50; margin-bottom: 5px;">Pahana Edu Book Shop</h1>
                    <p style="margin: 0; color: #7f8c8d;">Educational Books & Learning Materials</p>
                    <p style="margin: 0; color: #7f8c8d;">Phone: +94 11 234 5678 | Email: info@pahanaedu.com</p>
                    <hr style="margin: 20px 0;">
                </div>

                <!-- Bill Information -->
                <div style="display: flex; justify-content: space-between; margin-bottom: 30px;">
                    <div>
                        <h3 style="color: #2c3e50; margin-bottom: 15px;">Bill Information</h3>
                        <p><strong>Bill Number:</strong> ${bill.billNumber}</p>
                        <p><strong>Date:</strong> ${bill.createdAt}</p>
                        <p><strong>Cashier:</strong> ${bill.cashierName}</p>
                    </div>
                    <div>
                        <h3 style="color: #2c3e50; margin-bottom: 15px;">Customer Information</h3>
                        <p><strong>Customer:</strong> ${bill.customerName}</p>
                        <p><strong>Status:</strong> <span style="color: #27ae60; font-weight: bold;">${bill.paymentStatus.toUpperCase()}</span></p>
                    </div>
                </div>

                <!-- Bill Items -->
                <table class="table" style="margin-bottom: 30px;">
                    <thead>
                        <tr style="background-color: #34495e; color: white;">
                            <th>Item</th>
                            <th>Author</th>
                            <th>Price</th>
                            <th>Qty</th>
                            <th>Tax</th>
                            <th>Discount</th>
                            <th>Amount</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${bill.billItems}">
                            <tr>
                                <td>${item.bookTitle}</td>
                                <td>${item.bookAuthor}</td>
                                <td>Rs. ${item.unitPrice}</td>
                                <td>${item.quantity}</td>
                                <td>Rs. ${item.taxAmount}</td>
                                <td>Rs. ${item.discountAmount}</td>
                                <td>Rs. ${item.lineTotal}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>

                <!-- Bill Totals -->
                <div style="float: right; width: 300px;">
                    <table style="width: 100%; border-collapse: collapse;">
                        <tr>
                            <td style="padding: 8px; border-bottom: 1px solid #ddd;"><strong>Subtotal:</strong></td>
                            <td style="padding: 8px; border-bottom: 1px solid #ddd; text-align: right;">Rs. ${bill.subtotal}</td>
                        </tr>
                        <tr>
                            <td style="padding: 8px; border-bottom: 1px solid #ddd;"><strong>Total Discount:</strong></td>
                            <td style="padding: 8px; border-bottom: 1px solid #ddd; text-align: right;">Rs. ${bill.discountAmount}</td>
                        </tr>
                        <tr>
                            <td style="padding: 8px; border-bottom: 1px solid #ddd;"><strong>Total Tax:</strong></td>
                            <td style="padding: 8px; border-bottom: 1px solid #ddd; text-align: right;">Rs. ${bill.taxAmount}</td>
                        </tr>
                        <tr style="background-color: #3498db; color: white;">
                            <td style="padding: 12px; font-size: 16px;"><strong>Total Amount:</strong></td>
                            <td style="padding: 12px; text-align: right; font-size: 16px;"><strong>Rs. ${bill.totalAmount}</strong></td>
                        </tr>
                    </table>
                </div>
                
                <div style="clear: both; margin-top: 50px; text-align: center; color: #7f8c8d;">
                    <p>Thank you for your business!</p>
                    <p style="font-size: 12px;">This is a computer generated bill and does not require signature.</p>
                </div>
            </div>
        </div>
    </main>

    <script>
        function printBill() {
            window.print();
        }
        
        function sendBillEmail() {
            // Simulate sending email
            alert('Bill ${bill.billNumber} has been sent via email to ${bill.customerName}!');
        }
    </script>
</body>
</html>