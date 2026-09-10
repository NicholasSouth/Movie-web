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
}