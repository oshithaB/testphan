package com.pahanaedu.bookshop.webservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pahanaedu.bookshop.persistence.common.session.SessionManager;
import com.pahanaedu.bookshop.persistence.dao.UserDAO;
import com.pahanaedu.bookshop.business.user.model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * REST Web Service for Authentication operations
 * Provides JSON API endpoints for distributed authentication
 */
public class AuthWebService extends HttpServlet {
    private UserDAO userDAO;
    private SessionManager sessionManager;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        sessionManager = SessionManager.getInstance();
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();

        try {
            Map<String, Object> result = new HashMap<>();

            if ("/login".equals(pathInfo)) {
                handleLogin(request, response, result);
            } else if ("/logout".equals(pathInfo)) {
                handleLogout(request, response, result);
            } else if ("/validate".equals(pathInfo)) {
                handleValidateSession(request, response, result);
            } else {
                result.put("success", false);
                result.put("message", "Invalid endpoint");
            }

            response.getWriter().write(objectMapper.writeValueAsString(result));

        } catch (Exception e) {
            handleError(response, e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();

        try {
            Map<String, Object> result = new HashMap<>();

            if ("/session".equals(pathInfo)) {
                handleGetSession(request, response, result);
            } else if ("/stats".equals(pathInfo)) {
                handleGetSessionStats(request, response, result);
            } else {
                result.put("success", false);
                result.put("message", "Invalid endpoint");
            }

            response.getWriter().write(objectMapper.writeValueAsString(result));

        } catch (Exception e) {
            handleError(response, e);
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response, Map<String, Object> result)
            throws Exception {

        // Read JSON from request body
        StringBuilder jsonBuffer = new StringBuilder();
        String line;
        while ((line = request.getReader().readLine()) != null) {
            jsonBuffer.append(line);
        }

        @SuppressWarnings("unchecked")
        Map<String, String> loginData = objectMapper.readValue(jsonBuffer.toString(), Map.class);

        String username = loginData.get("username");
        String password = loginData.get("password");

        User user = userDAO.authenticateUser(username, password);

        if (user != null) {
            // Create session
            String sessionId = sessionManager.createSession(request, response, user);

            result.put("success", true);
            result.put("message", "Login successful");
            result.put("sessionId", sessionId);
            result.put("user", convertUserToMap(user));
        } else {
            result.put("success", false);
            result.put("message", "Invalid username or password");
        }
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response, Map<String, Object> result)
            throws Exception {

        sessionManager.invalidateSession(request, response);

        result.put("success", true);
        result.put("message", "Logout successful");
    }

    private void handleValidateSession(HttpServletRequest request, HttpServletResponse response, Map<String, Object> result)
            throws Exception {

        String sessionId = request.getParameter("sessionId");
        if (sessionId == null) {
            sessionId = request.getSession(false) != null ? request.getSession(false).getId() : null;
        }

        if (sessionId != null && sessionManager.isSessionValid(sessionId)) {
            SessionManager.SessionInfo sessionInfo = sessionManager.getSessionInfo(sessionId);
            result.put("success", true);
            result.put("valid", true);
            result.put("user", convertUserToMap(sessionInfo.getUser()));
        } else {
            result.put("success", true);
            result.put("valid", false);
            result.put("message", "Invalid or expired session");
        }
    }

    private void handleGetSession(HttpServletRequest request, HttpServletResponse response, Map<String, Object> result)
            throws Exception {

        String sessionId = request.getSession(false) != null ? request.getSession(false).getId() : null;

        if (sessionId != null && sessionManager.isSessionValid(sessionId)) {
            SessionManager.SessionInfo sessionInfo = sessionManager.getSessionInfo(sessionId);
            result.put("success", true);
            result.put("sessionId", sessionId);
            result.put("user", convertUserToMap(sessionInfo.getUser()));
            result.put("createdTime", sessionInfo.getCreatedTime());
            result.put("lastAccessTime", sessionInfo.getLastAccessTime());
        } else {
            result.put("success", false);
            result.put("message", "No valid session found");
        }
    }

    private void handleGetSessionStats(HttpServletRequest request, HttpServletResponse response, Map<String, Object> result)
            throws Exception {

        Map<String, Integer> stats = sessionManager.getSessionStatistics();
        result.put("success", true);
        result.put("statistics", stats);
    }

    private Map<String, Object> convertUserToMap(User user) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("username", user.getUsername());
        userMap.put("role", user.getRole());
        userMap.put("fullName", user.getFullName());
        userMap.put("email", user.getEmail());
        userMap.put("phone", user.getPhone());
        userMap.put("active", user.isActive());
        return userMap;
    }

    private void handleError(HttpServletResponse response, Exception e) throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", "Server error: " + e.getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}