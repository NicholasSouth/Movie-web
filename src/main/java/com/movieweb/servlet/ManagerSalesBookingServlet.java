package com.movieweb.servlet;

import com.movieweb.DAO.Booking_showtimesDAO;
import com.movieweb.DAO.PaymentsDAO;
import com.movieweb.model.Users;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/sales-bookings")
public class ManagerSalesBookingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Users currentUser = session == null
                ? null
                : (Users) session.getAttribute("user");
        if (currentUser == null
            | !"MANAGER".equalsIgnoreCase(currentUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/log_in.jsp");
            return;
        }
        Integer theaterId = (Integer) session.getAttribute("currentTheaterId");
        if (theaterId == null) {
            response.sendRedirect(request.getContextPath() + "/main_manager.jsp");
            return;
        }
        try {
            Booking_showtimesDAO bookingDAO = new Booking_showtimesDAO();
            PaymentsDAO paymentsDAO = new PaymentsDAO();
            List<Map<String, Object>> bookings = bookingDAO.getBookingHistoryByTheaterId(theaterId);
            int dailyRevenue = paymentsDAO.getDailyRevenueByTheaterId(theaterId);
            request.setAttribute("bookingHistory", bookings);
            request.setAttribute("dailyRevenue", dailyRevenue);
            request.getRequestDispatcher("/pos.jsp").forward(request, response);
        }
        catch (SQLException e) {
            throw new ServletException("Unable to load sales and booking data.", e);
        }
    }
}