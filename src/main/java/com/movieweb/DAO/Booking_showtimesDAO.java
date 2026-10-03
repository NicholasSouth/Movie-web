package com.movieweb.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.movieweb.model.Booking_showtimes;
import com.movieweb.util.DBConnection;

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