package com.movieweb.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.movieweb.model.Booking_showtimes;
import com.movieweb.util.DBConnection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Booking_showtimesDAO 
{
    public Booking_showtimes getBookingById(int booking_id) 
    {
        String sql = "SELECT * FROM Booking_showtimes WHERE booking_id = ?";        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            stmt.setInt(1, booking_id);           
            ResultSet rs = stmt.executeQuery();        
            if (rs.next()) 
            {
                Booking_showtimes booking = new Booking_showtimes();
                booking.setBooking_id(rs.getInt("booking_id"));
                booking.setUser_id(rs.getInt("user_id"));
                booking.setShowtime_id(rs.getInt("showtime_id"));
                booking.setPromotion_id(rs.getInt("promotion_id"));
                booking.setBook_at(rs.getString("book_at"));
                booking.setStatus(rs.getString("status"));
                booking.setPrice(rs.getInt("price"));
                booking.setDelete_at(rs.getString("delete_at"));               
                return booking;
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }    
        return null;
    }
    
    //for booking history
    public List<Map<String, Object>> getBookingHistoryByUserId(int user_id) {
        List<Map<String, Object>> bookingHistory = new ArrayList<>();
        String sql =
            "SELECT " +
            "    b.booking_id, b.book_at, b.status AS booking_status, b.price, " +
            "    m.movie_name, m.poster_path, " +
            "    s.start_at, s.end_at, " +
            "    t.theater_name, r.room_name, " +
            "    COALESCE(p.status, 'NOT PAID') AS payment_status, " +
            "    p.method AS payment_method, p.amount AS payment_amount, " +
            "    STUFF(( " +
            "        SELECT ', ' + " +
            "            CAST(se.seat_row AS VARCHAR(10)) + " +
            "            CAST(se.seat_col AS VARCHAR(10)) + " +
            "            ' (' + tt.ticket_type_name + ')' " +
            "        FROM Booking_seats bs " +
            "        INNER JOIN Seats se ON bs.seat_id = se.seat_id " +
            "        INNER JOIN Ticket_types tt " +
            "            ON bs.ticket_type_id = tt.ticket_type_id " +
            "        WHERE bs.booking_id = b.booking_id " +
            "        FOR XML PATH(''), TYPE " +
            "    ).value('.', 'NVARCHAR(MAX)'), 1, 2, '') AS seat_summary " +
            "FROM Booking_showtimes b " +
            "INNER JOIN Showtimes s ON b.showtime_id = s.showtime_id " +
            "INNER JOIN Movies m ON s.movie_id = m.movie_id " +
            "INNER JOIN Rooms r ON s.room_id = r.room_id " +
            "INNER JOIN Theaters t ON r.theater_id = t.theater_id " +
            "LEFT JOIN Payments p ON b.booking_id = p.booking_id " +
            "WHERE b.user_id = ? " +
            "AND b.delete_at IS NULL " +
            "ORDER BY b.book_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, user_id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> booking = new HashMap<>();
                    booking.put("booking_id", rs.getInt("booking_id"));
                    booking.put("book_at", rs.getTimestamp("book_at"));
                    booking.put("booking_status", rs.getString("booking_status"));
                    booking.put("price", rs.getInt("price"));
                    booking.put("movie_name", rs.getString("movie_name"));
                    booking.put("poster_path", rs.getString("poster_path"));
                    booking.put("start_at", rs.getTimestamp("start_at"));
                    booking.put("end_at", rs.getTimestamp("end_at"));
                    booking.put("theater_name", rs.getString("theater_name"));
                    booking.put("room_name", rs.getString("room_name"));
                    booking.put("seat_summary", rs.getString("seat_summary"));
                    booking.put("payment_status", rs.getString("payment_status"));
                    booking.put("payment_method", rs.getString("payment_method"));
                    booking.put("payment_amount", rs.getObject("payment_amount"));
                    bookingHistory.add(booking);
                }
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }

        return bookingHistory;
    }
    
    public int createPendingBooking(
            Connection conn,
            int user_id,
            int showtime_id,
            int promotion_id,
            int price) throws SQLException {
        String sql =
                "INSERT INTO Booking_showtimes " +
                "(user_id, showtime_id, promotion_id, book_at, status, price, delete_at) " +
                "VALUES (?, ?, ?, GETDATE(), 'PENDING', ?, NULL)";
        try (PreparedStatement stmt = conn.prepareStatement(
                sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, user_id);
            stmt.setInt(2, showtime_id);
            if (promotion_id > 0) {
                stmt.setInt(3, promotion_id);
            } 
            else {
                stmt.setNull(3, java.sql.Types.INTEGER);
            }
            stmt.setInt(4, price);
            if (stmt.executeUpdate() != 1) {
                throw new SQLException("Failed to create booking.");
            }
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            throw new SQLException("Could not retrieve generated booking_id.");
        }
    }
}