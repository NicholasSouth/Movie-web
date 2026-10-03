
package com.movieweb.servlet;

import com.movieweb.model.Users;
import com.movieweb.service.PaymentService;
import com.movieweb.util.DBConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

@WebServlet("/payment")
public class PaymentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final PaymentService paymentService = new PaymentService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Users user = getLoggedInUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        Integer bookingId = parsePositiveInt(request.getParameter("booking_id"));
        if (bookingId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid booking ID.");
            return;
        }
        try {
            if (!loadCheckoutData(request, user.getUserId(), bookingId)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Booking not found.");
                return;
            }
            request.getRequestDispatcher("/payment.jsp").forward(request, response);
        } 
        catch (SQLException e) {
            throw new ServletException("Unable to load checkout details.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Users user = getLoggedInUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        Integer bookingId = parsePositiveInt(request.getParameter("booking_id"));
        Integer paymentMethodId = parsePositiveInt(request.getParameter("payment_method_id"));
        if (bookingId == null || paymentMethodId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid booking or payment method.");
            return;
        }
        try {
            String transactionCode = paymentService.payWithVisa(
                    user.getUserId(),
                    bookingId,
                    paymentMethodId
            );

            // Redirect after a successful payment to avoid resubmitting POST.
            response.sendRedirect(request.getContextPath() + "/payment?booking_id=" + bookingId);
        } 
        catch (IllegalArgumentException e) {
            showPaymentError(request, response, user.getUserId(), bookingId, e.getMessage());

        } 
        catch (SQLException e) {
            // Log the detailed exception on the server; show a safe message.
            getServletContext().log("Fake VISA payment failed.", e);
            showPaymentError(request, response, user.getUserId(), bookingId,
                    "The payment could not be completed. Please try again.");
        }
    }
    
    private void showPaymentError(HttpServletRequest request,
                                  HttpServletResponse response,
                                  int userId,
                                  int bookingId,
                                  String message)
            throws ServletException, IOException {
        try {
            if (!loadCheckoutData(request, userId, bookingId)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Booking not found.");
                return;
            }
            request.setAttribute("paymentError", message);
            request.getRequestDispatcher("/payment.jsp").forward(request, response);
        } 
        catch (SQLException e) {
            throw new ServletException("Unable to reload checkout details.", e);
        }
    }
    private boolean loadCheckoutData(HttpServletRequest request, int userId, int bookingId)
            throws SQLException {
        String bookingSql =
                "SELECT b.booking_id, b.user_id, b.showtime_id, " +
                "       b.book_at, b.status AS booking_status, b.price, " +
                "       b.delete_at, " +
                "       CASE WHEN b.status = 'PENDING' " +
                "                 AND (b.delete_at IS NOT NULL " +
                "                      OR DATEADD(MINUTE, 15, b.book_at) <= GETDATE()) " +
                "            THEN 1 ELSE 0 END AS is_expired, " +
                "       m.movie_name, s.start_at, s.end_at, " +
                "       s.status AS showtime_status, " +
                "       r.room_name, t.theater_name, " +
                "       p.status AS payment_status, p.transaction_code " +
                "FROM Booking_showtimes b " +
                "INNER JOIN Showtimes s ON s.showtime_id = b.showtime_id " +
                "INNER JOIN Movies m ON m.movie_id = s.movie_id " +
                "INNER JOIN Rooms r ON r.room_id = s.room_id " +
                "INNER JOIN Theaters t ON t.theater_id = r.theater_id " +
                "LEFT JOIN Payments p ON p.booking_id = b.booking_id " +
                "WHERE b.booking_id = ? AND b.user_id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(bookingSql)) {
                stmt.setInt(1, bookingId);
                stmt.setInt(2, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        return false;
                    }
                    request.setAttribute("bookingId", rs.getInt("booking_id"));
                    request.setAttribute("bookingStatus", rs.getString("booking_status"));
                    request.setAttribute("bookingPrice", rs.getInt("price"));
                    request.setAttribute("isExpired", rs.getInt("is_expired") == 1);
                    request.setAttribute("movieName", rs.getString("movie_name"));
                    request.setAttribute("showtimeStart", rs.getTimestamp("start_at"));
                    request.setAttribute("showtimeEnd", rs.getTimestamp("end_at"));
                    request.setAttribute("showtimeStatus", rs.getString("showtime_status"));
                    request.setAttribute("roomName", rs.getString("room_name"));
                    request.setAttribute("theaterName", rs.getString("theater_name"));
                    request.setAttribute("paymentStatus", rs.getString("payment_status"));
                    request.setAttribute("transactionCode", rs.getString("transaction_code"));
                }
            }
            List<Map<String, Object>> seats = new ArrayList<>();
            String seatsSql =
                    "SELECT s.seat_row, s.seat_col, " +
                    "       st.seat_type_name, tt.ticket_type_name, " +
                    "       bs.final_price " +
                    "FROM Booking_seats bs " +
                    "INNER JOIN Seats s ON s.seat_id = bs.seat_id " +
                    "INNER JOIN Seat_types st ON st.seat_type_id = s.seat_type_id " +
                    "INNER JOIN Ticket_types tt ON tt.ticket_type_id = bs.ticket_type_id " +
                    "WHERE bs.booking_id = ? " +
                    "ORDER BY s.seat_row, s.seat_col";
            try (PreparedStatement stmt = conn.prepareStatement(seatsSql)) {
                stmt.setInt(1, bookingId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> seat = new HashMap<>();
                        seat.put("seatRow", rs.getObject("seat_row"));
                        seat.put("seatCol", rs.getObject("seat_col"));
                        seat.put("seatType", rs.getString("seat_type_name"));
                        seat.put("ticketType", rs.getString("ticket_type_name"));
                        seat.put("finalPrice", rs.getInt("final_price"));
                        seats.add(seat);
                    }
                }
            }
            request.setAttribute("bookingSeats", seats);
            List<Map<String, Object>> cards = new ArrayList<>();
            String cardsSql =
                    "SELECT payment_method_id, card_number, expired_date " +
                    "FROM Payment_methods " +
                    "WHERE user_id = ? AND isActive = 1 AND method = 'VISA' " +
                    "ORDER BY created_at DESC";
            try (PreparedStatement stmt = conn.prepareStatement(cardsSql)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> card = new HashMap<>();
                        card.put("paymentMethodId", rs.getInt("payment_method_id"));
                        String cardNumber = rs.getString("card_number");
                        String lastFour = cardNumber == null
                                ? "----"
                                : cardNumber.substring(
                                        Math.max(0, cardNumber.length() - 4));
                        card.put("maskedNumber", "•••• •••• •••• " + lastFour);
                        card.put("expiredDate", rs.getObject("expired_date"));
                        cards.add(card);
                    }
                }
            }
            request.setAttribute("visaCards", cards);
            return true;
        }
    }

    private Users getLoggedInUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object userObject = session.getAttribute("user");
        if (!(userObject instanceof Users)) {
            return null;
        }
        Users user = (Users) userObject;

        // Checkout is for normal users, not managers.
        if (!"USER".equalsIgnoreCase(user.getRole())) {
            return null;
        }
        return user;
    }

    private Integer parsePositiveInt(String value) {
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : null;
        } 
        catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }
}