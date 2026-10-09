package com.movieweb.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.movieweb.model.Users;
import com.movieweb.util.DBConnection;

@WebServlet("/manager-payments")
public class ManagerPaymentsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        if (!checkManager(request, response)) {
            return;
        }
        Users manager = (Users) request.getSession(false).getAttribute("user");
        try {
            List<Map<String, Object>> paymentMethods = loadPaymentMethods(manager.getUserId());
            request.setAttribute("paymentMethods", paymentMethods);
            request.getRequestDispatcher("/pos_payment_methods.jsp").forward(request, response);
        } 
        catch (Exception e) {
            request.setAttribute("paymentError", "Unable to load payment methods: " + e.getMessage());
            request.setAttribute("paymentMethods", new ArrayList<Map<String, Object>>());
            request.getRequestDispatcher("/pos_payment_methods.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        if (!checkManager(request, response)) {
            return;
        }
        Users manager = (Users) request.getSession(false).getAttribute("user");
        String action = request.getParameter("action");
        if ("add".equalsIgnoreCase(action)) {
            addPaymentMethod(request, response, manager.getUserId());
            return;
        }
        if ("remove".equalsIgnoreCase(action)) {
            removePaymentMethod(request, response, manager.getUserId());
            return;
        }
        response.sendRedirect(request.getContextPath() + "/manager-payments");
    }

    private void addPaymentMethod(
            HttpServletRequest request,
            HttpServletResponse response,
            int userId)
            throws IOException {
        String method = request.getParameter("method");
        String cardNumber = request.getParameter("card_number");
        String expiredDate = request.getParameter("expired_date");
        if (method == null
            || method.trim().isEmpty()) {
            redirectError(request, response, "Payment method is required.");
            return;
        }
        method = method.trim().toUpperCase();
        if (!method.equals("VISA")
            && !method.equals("CASH")
            && !method.equals("QR")) {
            redirectError(request, response, "Invalid payment method.");
            return;
        }
        if ("VISA".equals(method)) {
            if (cardNumber == null
                || cardNumber.trim().isEmpty()) {
                redirectError(request, response, "Card number is required for VISA.");
                return;
            }
            if (expiredDate == null
                || expiredDate.trim().isEmpty()) {
                redirectError(request, response, "Expired date is required for VISA.");
                return;
            }
        }
        if (!"VISA".equals(method)) {
            cardNumber = null;
            expiredDate = null;
        }
        String sql =
                "INSERT INTO Payment_methods "
                + "(user_id, method, card_number, expired_date, "
                + "created_at, isActive) "
                + "VALUES (?, ?, ?, ?, GETDATE(), 1)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setString(2, method);
            if (cardNumber == null) {
                statement.setNull(
                        3,
                        java.sql.Types.VARCHAR
                );
            } 
            else {
                statement.setString(
                        3,
                        cardNumber.trim()
                );
            }
            if (expiredDate == null) {
                statement.setNull(
                        4,
                        java.sql.Types.VARCHAR
                );
            } 
            else {
                statement.setString(
                        4,
                        expiredDate.trim()
                );
            }
            statement.executeUpdate();
            response.sendRedirect(request.getContextPath() + "/manager-payments?success=added");
        } 
        catch (Exception e) {
            redirectError(request, response, e.getMessage());
        }
    }

    private void removePaymentMethod(
            HttpServletRequest request,
            HttpServletResponse response,
            int userId)
            throws IOException {
        String paymentMethodParameter = request.getParameter("payment_method_id");
        int paymentMethodId;
        try {
            paymentMethodId = Integer.parseInt(paymentMethodParameter);

        } 
        catch (Exception e) {
            redirectError(request, response, "Invalid payment method.");
            return;
        }
        String sql =
                "UPDATE Payment_methods "
                + "SET isActive = 0 "
                + "WHERE payment_method_id = ? "
                + "AND user_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =connection.prepareStatement(sql)) {
            statement.setInt(
                    1,
                    paymentMethodId
            );
            statement.setInt(
                    2,
                    userId
            );
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                redirectError(request, response, "Payment method was not found.");
                return;
            }
            response.sendRedirect(request.getContextPath() + "/manager-payments?success=removed");
        } 
        catch (Exception e) {
            redirectError(request, response, e.getMessage());
        }
    }
    
    private List<Map<String, Object>> loadPaymentMethods(int userId)
            throws Exception {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        String sql =
                "SELECT "
                + "payment_method_id, "
                + "method, "
                + "card_number, "
                + "expired_date, "
                + "created_at, "
                + "isActive "
                + "FROM Payment_methods "
                + "WHERE user_id = ? "
                + "ORDER BY payment_method_id DESC";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> payment = new HashMap<String, Object>();
                    payment.put("paymentMethodId", rs.getInt("payment_method_id"));
                    payment.put("method", rs.getString("method"));
                    payment.put("cardNumber", rs.getString("card_number"));
                    payment.put("expiredDate", rs.getString("expired_date"));
                    payment.put("createdAt", rs.getTimestamp("created_at"));
                    payment.put("isActive", rs.getBoolean("isActive"));
                    result.add(payment);
                }
            }
        }
        return result;
    }

    private boolean checkManager(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        Users currentUser = session == null
                        ? null
                        : (Users) session.getAttribute("user");
        if (currentUser == null
            || !"MANAGER".equalsIgnoreCase(currentUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/log_in.jsp");
            return false;
        }
        return true;
    }

    private void redirectError(
            HttpServletRequest request,
            HttpServletResponse response,
            String message)
            throws IOException {
        if (message == null
            || message.trim().isEmpty()) {
            message = "Payment operation failed.";
        }
        String encodedMessage = java.net.URLEncoder.encode(message, "UTF-8");
        response.sendRedirect(request.getContextPath() + "/manager-payments?error=" + encodedMessage);
    }
}