package com.movieweb.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.movieweb.model.Seats;
import com.movieweb.util.DBConnection;

public class SeatsDAO 
{
    // Minutes a pending booking keeps its seats before they are released
    private static final int HOLD_MINUTES = 15;

    public Seats getSeatById(int seat_id) 
    {
        String sql = "SELECT * FROM Seats WHERE seat_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            stmt.setInt(1, seat_id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) 
            {
                Seats seat = new Seats();
                seat.setSeat_id(rs.getInt("seat_id"));
                seat.setRoom_id(rs.getInt("room_id"));
                seat.setSeat_row(rs.getString("seat_row"));
                seat.setSeat_col(rs.getInt("seat_col"));
                seat.setSeat_type_id(rs.getInt("seat_type_id"));
                seat.setActive(rs.getBoolean("isActive"));

                return seat;
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }

        return null;
    }

    public List<Seats> getSeatsWithStatusByShowtime(int showtimeId) {
        List<Seats> seats = new ArrayList<>();
        String sql =
                "SELECT s.seat_id, s.room_id, s.seat_row, s.seat_col, s.seat_type_id, s.isActive, " +
                "t.seat_type_name, " + "t.price_modify AS seat_price_modify, " +
                "CASE WHEN EXISTS (" +
                "    SELECT 1 " +
                "    FROM Booking_seats bs " +
                "    JOIN Booking_showtimes b ON b.booking_id = bs.booking_id " +
                "    WHERE bs.seat_id = s.seat_id " +
                "    AND b.showtime_id = st.showtime_id " +
                "    AND b.delete_at IS NULL " +
                "    AND (b.status = 'CONFIRMED' " +
                "         OR (b.status = 'PENDING' " +
                "             AND DATEADD(MINUTE, " + HOLD_MINUTES + ", b.book_at) > GETDATE()))" +
                ") THEN 1 ELSE 0 END AS is_booked " +
                "FROM Showtimes st " +
                "INNER JOIN Seats s ON s.room_id = st.room_id " +
                "INNER JOIN Seat_types t ON t.seat_type_id = s.seat_type_id " +
                "WHERE st.showtime_id = ? " +
                "AND s.seat_row IS NOT NULL AND s.seat_row <> '' " +
                "AND s.seat_col IS NOT NULL AND s.seat_col > 0 " +
                "ORDER BY s.seat_row ASC, s.seat_col ASC";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, showtimeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Seats seat = new Seats();
                    seat.setSeat_id(rs.getInt("seat_id"));
                    seat.setRoom_id(rs.getInt("room_id"));
                    seat.setSeat_row(rs.getString("seat_row"));
                    seat.setSeat_col(rs.getInt("seat_col"));
                    seat.setSeat_type_id(rs.getInt("seat_type_id"));
                    seat.setSeat_type_name(rs.getString("seat_type_name"));
                    seat.setSeat_price_modify(rs.getString("seat_price_modify"));
                    seat.setActive(rs.getBoolean("isActive"));
                    seat.setBooked(rs.getInt("is_booked") == 1);
                    seats.add(seat);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return seats;
    }
}