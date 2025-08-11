package com.pahanaedu.bookshop.persistence.servlet;

import com.pahanaedu.bookshop.persistence.dao.UserDAO;
import com.pahanaedu.bookshop.business.user.model.User;
import com.pahanaedu.bookshop.persistence.resource.resource.util.EmailUtil;
import com.pahanaedu.bookshop.persistence.resource.resource.util.PasswordUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public class UserServlet extends HttpServlet {
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check if user is admin
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || !"admin".equals(currentUser.getRole())) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        String action = request.getParameter("action");
        if (action == null) action = "list";
        
        try {
            switch (action) {
                case "list":
                    listUsers(request, response);
                    break;
                case "add":
                    showAddForm(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "delete":
                    deleteUser(request, response);
                    break;
                case "search":
                    searchUsers(request, response);
                    break;
                default:
                    listUsers(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error processing request: " + e.getMessage());
            request.getRequestDispatcher("/users.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        try {
            switch (action) {
                case "add":
                    addUser(request, response);
                    break;
                case "edit":
                    updateUser(request, response);
                    break;
                default:
                    listUsers(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error processing request: " + e.getMessage());
            request.getRequestDispatcher("/users.jsp").forward(request, response);
        }
    }
    
    private void listUsers(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<User> users = userDAO.getAllUsers();
        request.setAttribute("users", users);
        request.getRequestDispatcher("/users.jsp").forward(request, response);
    }
    
    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        request.setAttribute("action", "add");
        request.getRequestDispatcher("/user-form.jsp").forward(request, response);
    }
    
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        int id = Integer.parseInt(request.getParameter("id"));
        User user = userDAO.getUserById(id);
        request.setAttribute("user", user);
        request.setAttribute("action", "edit");
        request.getRequestDispatcher("/user-form.jsp").forward(request, response);
    }
    
    private void addUser(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        User user = new User();
        user.setUsername(request.getParameter("username"));
        user.setPassword(PasswordUtil.hashPassword(request.getParameter("password")));
        user.setRole(request.getParameter("role"));
        user.setFullName(request.getParameter("fullName"));
        user.setEmail(request.getParameter("email"));
        user.setPhone(request.getParameter("phone"));
        
        if (userDAO.addUser(user)) {
            // Send welcome email
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                EmailUtil.sendWelcomeEmail(user.getEmail(), user.getUsername());
            }
            
            request.setAttribute("success", "User added successfully!");
        } else {
            request.setAttribute("error", "Failed to add user");
        }
        
        listUsers(request, response);
    }
    
    private void updateUser(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        User user = new User();
        user.setId(Integer.parseInt(request.getParameter("id")));
        user.setUsername(request.getParameter("username"));
        user.setRole(request.getParameter("role"));
        user.setFullName(request.getParameter("fullName"));
        user.setEmail(request.getParameter("email"));
        user.setPhone(request.getParameter("phone"));
        
        if (userDAO.updateUser(user)) {
            request.setAttribute("success", "User updated successfully!");
        } else {
            request.setAttribute("error", "Failed to update user");
        }
        
        listUsers(request, response);
    }
    
    private void deleteUser(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        int id = Integer.parseInt(request.getParameter("id"));
        
        if (userDAO.deleteUser(id)) {
            request.setAttribute("success", "User deleted successfully!");
        } else {
            request.setAttribute("error", "Failed to delete user");
        }
        
        listUsers(request, response);
    }
    
    private void searchUsers(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String searchTerm = request.getParameter("search");
        List<User> users;
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            users = userDAO.searchUsers(searchTerm);
        } else {
            users = userDAO.getAllUsers();
        }
        
        request.setAttribute("users", users);
        request.setAttribute("searchTerm", searchTerm);
        request.getRequestDispatcher("/users.jsp").forward(request, response);
    }
}