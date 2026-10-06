package com.movieweb.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.movieweb.model.Booking_seats;
import com.movieweb.util.DBConnection;

public class Booking_seatsDAO 
{
    private static final int HOLD_MINUTES = 15;

    public Booking_seats getBookingSeat(int booking_id, int seat_id)
    {
        String sql = "SELECT * FROM Booking_seats WHERE booking_id = ? AND seat_id = ?";      
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            stmt.setInt(1, booking_id);
            stmt.setInt(2, seat_id);            
            ResultSet rs = stmt.executeQuery();           
            if (rs.next()) 
            {
                Booking_seats booking_seat = new Booking_seats();
                booking_seat.setBooking_id(rs.getInt("booking_id"));
                booking_seat.setSeat_id(rs.getInt("seat_id"));
                booking_seat.setTicket_type_id(rs.getInt("ticket_type_id"));
                booking_seat.setFinal_price(rs.getInt("final_price"));               
                return booking_seat;
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }        
        return null;
    }
    
    public boolean addBookingSeat(Connection conn, Booking_seats bookingSeat)
            throws SQLException {
        String sql = "INSERT INTO Booking_seats " +
                     "(booking_id, seat_id, ticket_type_id, final_price) " +
                     "VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingSeat.getBooking_id());
            stmt.setInt(2, bookingSeat.getSeat_id());
            stmt.setInt(3, bookingSeat.getTicket_type_id());
            stmt.setInt(4, bookingSeat.getFinal_price());
            return stmt.executeUpdate() == 1;
        }
    }

    public boolean hasUpcomingBooking(int seatId) {
        String sql =
                "SELECT 1 " +
                        "FROM Booking_seats bs " +
                        "JOIN Booking_showtimes b ON b.booking_id = bs.booking_id " +
                        "JOIN Showtimes st ON st.showtime_id = b.showtime_id " +
                        "WHERE bs.seat_id = ? " +
                        "AND st.start_at > GETDATE() " +
                        "AND b.delete_at IS NULL " +
                        "AND (b.status = 'CONFIRMED' " +
                        "     OR (b.status = 'PENDING' " +
                        "         AND DATEADD(MINUTE, " + HOLD_MINUTES + ", b.book_at) > GETDATE()))";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, seatId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }
}