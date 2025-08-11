package com.pahanaedu.bookshop.persistence.servlet;

import com.pahanaedu.bookshop.persistence.common.session.SessionManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogoutServlet extends HttpServlet {
    private SessionManager sessionManager;
    
    @Override
    public void init() throws ServletException {
        sessionManager = SessionManager.getInstance();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Use SessionManager to invalidate session
        sessionManager.invalidateSession(request, response);
        
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }
}