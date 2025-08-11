package com.pahanaedu.bookshop.persistence.servlet;

import com.pahanaedu.bookshop.persistence.dao.UserDAO;
import com.pahanaedu.bookshop.business.user.model.User;
import com.pahanaedu.bookshop.persistence.common.session.SessionManager;
import com.pahanaedu.bookshop.persistence.common.observer.SessionAuditObserver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginServlet extends HttpServlet {
    private UserDAO userDAO;
    private SessionManager sessionManager;
    private SessionAuditObserver auditObserver;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        sessionManager = SessionManager.getInstance();
        auditObserver = new SessionAuditObserver();
        sessionManager.addObserver(auditObserver);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        try {
            User user = userDAO.authenticateUser(username, password);
            
            if (user != null) {
                // Use SessionManager to create session
                sessionManager.createSession(request, response, user);
                
                response.sendRedirect(request.getContextPath() + "/dashboard");
            } else {
                request.setAttribute("error", "Invalid username or password");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Login failed. Please try again.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}