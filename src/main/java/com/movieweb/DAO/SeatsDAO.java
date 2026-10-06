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
                "AND s.deleted_at IS NULL " +
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

    public List<Seats> getSeatsByRoomId(int roomId) {
        List<Seats> seats = new ArrayList<>();
        String sql =
                "SELECT s.seat_id, s.room_id, s.seat_row, s.seat_col, s.seat_type_id, s.isActive, " +
                        "t.seat_type_name, t.price_modify AS seat_price_modify " +
                        "FROM Seats s " +
                        "INNER JOIN Seat_types t ON t.seat_type_id = s.seat_type_id " +
                        "WHERE s.room_id = ? " +
                        "AND s.seat_row IS NOT NULL AND s.seat_row <> '' " +
                        "AND s.seat_col IS NOT NULL AND s.seat_col > 0 " +
                        "AND s.deleted_at IS NULL " +
                        "ORDER BY s.seat_row ASC, s.seat_col ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roomId);
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
                    seats.add(seat);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return seats;
    }

    public boolean seatExists(int roomId, String seatRow, int seatCol) {
        String sql = "SELECT 1 FROM Seats WHERE room_id = ? AND seat_row = ? " +
                     "AND seat_col = ? AND deleted_at IS NULL";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            stmt.setString(2, seatRow);
            stmt.setInt(3, seatCol);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean insertSeat(Seats seat) {
        String sql = "INSERT INTO Seats (room_id, seat_row, seat_col, seat_type_id, isActive) VALUES (?, ?, ?, ?, 1)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, seat.getRoom_id());
            stmt.setString(2, seat.getSeat_row());
            stmt.setInt(3, seat.getSeat_col());
            stmt.setInt(4, seat.getSeat_type_id());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Seats getSeatByPosition(int roomId, String seatRow, int seatCol) {
        String sql = "SELECT seat_id, room_id, seat_row, seat_col, seat_type_id, isActive " +
                     "FROM Seats WHERE room_id = ? AND seat_row = ? AND seat_col = ? " +
                     "AND deleted_at IS NULL";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            stmt.setString(2, seatRow);
            stmt.setInt(3, seatCol);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
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
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateSeats(List<Seats> seats) {
        String sql = "UPDATE Seats SET seat_row = ?, seat_col = ?, seat_type_id = ? WHERE seat_id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (Seats seat : seats) {
                    stmt.setString(1, seat.getSeat_row());
                    stmt.setInt(2, seat.getSeat_col());
                    stmt.setInt(3, seat.getSeat_type_id());
                    stmt.setInt(4, seat.getSeat_id());
                    stmt.addBatch();
                }
                stmt.executeBatch();
                conn.commit();
                return true;
            }
            catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateSeatsActive(List<Integer> seatIds, boolean active) {
        String sql = "UPDATE Seats SET isActive = ? WHERE seat_id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (int seatId : seatIds) {
                    stmt.setBoolean(1, active);
                    stmt.setInt(2, seatId);
                    stmt.addBatch();
                }
                stmt.executeBatch();
                conn.commit();
                return true;
            }
            catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteSeats(List<Integer> seatIds) {
        String sql = "UPDATE Seats SET deleted_at = GETDATE() WHERE seat_id = ? AND deleted_at IS NULL";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (int seatId : seatIds) {
                    stmt.setInt(1, seatId);
                    stmt.addBatch();
                }
                stmt.executeBatch();
                conn.commit();
                return true;
            }
            catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}