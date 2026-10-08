package com.movieweb.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.movieweb.model.Showtimes;
import com.movieweb.model.Theaters;
import com.movieweb.model.Showtime_ticket_types;
import com.movieweb.util.DBConnection;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.sql.Timestamp;

import java.util.LinkedHashMap;

public class ShowtimesDAO {
    private static final String ACTIVE_BOOKING_CONDITION =
            "b.delete_at IS NULL " +
            "AND (UPPER(b.status) = 'CONFIRMED' " +
            "OR (UPPER(b.status) = 'PENDING' " +
            "AND b.book_at > DATEADD(MINUTE, -15, GETDATE())))";
    private static final String MANAGED_UPCOMING_SHOWTIMES =
            "FROM Showtimes s " +
            "INNER JOIN Rooms r ON r.room_id = s.room_id " +
            "INNER JOIN Theaters t ON t.theater_id = r.theater_id " +
            "INNER JOIN Managers m ON m.theater_id = t.theater_id " +
            "WHERE m.user_id = ? " +
            "AND t.isActive = 1 " +
            "AND t.deleted_at IS NULL " +
            "AND UPPER(s.status) = 'SCHEDULED' " +
            "AND s.deleted_at IS NULL " +
            "AND s.start_at > GETDATE() ";

    public Showtimes getShowtimeById(int showtime_id) {
        String sql = "SELECT * FROM Showtimes WHERE showtime_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, showtime_id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapShowtime(rs);
                }
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    //for theater_details.jsp
    public List<Showtimes> getShowtimesByTheaterAndDate(int theaterId, LocalDate date) {
        List<Showtimes> showtimes = new ArrayList<>();
        String sql =
            "SELECT s.* " +
            "FROM Showtimes s " +
            "INNER JOIN Rooms r ON r.room_id = s.room_id " +
            "INNER JOIN Theaters t ON t.theater_id = r.theater_id " +
            "WHERE t.theater_id = ? " +
            "AND r.isActive = 1 " +
            "AND t.isActive = 1 " +
            "AND t.deleted_at IS NULL " +
            "AND UPPER(s.status) = 'SCHEDULED' " +
            "AND s.deleted_at IS NULL " +
            "AND s.start_at >= ? " +
            "AND s.start_at < ? " +
            "ORDER BY s.start_at ASC";
        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            /*Start of selected day.*/
            stmt.setTimestamp(
                2,
                java.sql.Timestamp.valueOf(date.atStartOfDay())
            );

            /* Start of the next day.
             * Using < next day instead of <= 23:59:59 avoids problems with milliseconds.*/
            stmt.setTimestamp(
                3,
                java.sql.Timestamp.valueOf(
                    date.plusDays(1).atStartOfDay()
                )
            );
            stmt.setInt(1, theaterId);
            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    showtimes.add(mapShowtime(rs));
                }
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return showtimes;
    }
    
    //for movie_details.jsp
    public Map<Showtimes, Theaters> getTop5ShowtimesByMovieId(int movieId) {
        Map<Showtimes, Theaters> showtimes = new LinkedHashMap<>();
        String sql =
                "SELECT TOP 5 " +
                "s.showtime_id, " +
                "s.room_id, " +
                "s.movie_id, " +
                "s.start_at, " +
                "s.end_at, " +
                "s.status, " +
                "t.theater_id, " +
                "t.theater_name, " +
                "t.theater_address, " +
                "t.theater_image_path, " +
                "t.description, " +
                "t.latitude, " +
                "t.longtitude, " +
                "t.open_time, " +
                "t.closing_time, " +
                "t.isActive, " +
                "t.deleted_at " +
                "FROM Showtimes s " +
                "INNER JOIN Rooms r ON r.room_id = s.room_id " +
                "INNER JOIN Theaters t ON t.theater_id = r.theater_id " +
                "WHERE s.movie_id = ? " +
                "AND r.isActive = 1 " +
                "AND t.isActive = 1 " +
                "AND t.deleted_at IS NULL " +
                "AND UPPER(s.status) = 'SCHEDULED' " +
                "AND s.deleted_at IS NULL " +
                "AND s.start_at >= GETDATE() " +
                "ORDER BY s.start_at ASC";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, movieId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Showtimes showtime = new Showtimes();
                    showtime.setShowtime_id(rs.getInt("showtime_id"));
                    showtime.setRoom_id(rs.getInt("room_id"));
                    showtime.setMovie_id(rs.getInt("movie_id"));
                    showtime.setStart_at(rs.getTimestamp("start_at"));
                    showtime.setEnd_at(rs.getTimestamp("end_at"));
                    Theaters theater = new Theaters();
                    theater.setTheater_id(rs.getInt("theater_id"));
                    theater.setTheater_name(rs.getString("theater_name"));
                    theater.setTheater_address(rs.getString("theater_address"));
                    theater.setTheater_image_path(rs.getString("theater_image_path"));
                    theater.setDescription(rs.getString("description"));
                    theater.setLatitude(rs.getDouble("latitude"));
                    theater.setLongtitude(rs.getDouble("longtitude"));
                    theater.setOpen_time(rs.getTime("open_time"));
                    theater.setClosing_time(rs.getTime("closing_time"));
                    theater.setActive(rs.getBoolean("isActive"));
                    theater.setDeleted_at(rs.getTimestamp("deleted_at"));
                    showtimes.put(showtime, theater);
                }
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return showtimes;
    }
    
    //for booking_showtimes.jsp
    public List<Theaters> getTheatersByMovieId(int movieId) {
        List<Theaters> theaters = new ArrayList<>();
        String sql =
            "SELECT DISTINCT t.* " +
            "FROM Theaters t " +
            "INNER JOIN Rooms r ON r.theater_id = t.theater_id " +
            "INNER JOIN Showtimes s ON s.room_id = r.room_id " +
            "WHERE s.movie_id = ? " +
            "AND r.isActive = 1 " +
            "AND t.isActive = 1 " +
            "AND t.deleted_at IS NULL " +
            "AND UPPER(s.status) = 'SCHEDULED' " +
            "AND s.deleted_at IS NULL " +
            "AND s.start_at >= GETDATE() " +
            "AND s.start_at < DATEADD(DAY, 7, CONVERT(date, GETDATE())) " +
            "ORDER BY t.theater_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, movieId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Theaters theater = new Theaters();
                    theater.setTheater_id(rs.getInt("theater_id"));
                    theater.setTheater_name(rs.getString("theater_name"));
                    theater.setTheater_address(rs.getString("theater_address"));
                    theater.setTheater_image_path(rs.getString("theater_image_path"));
                    theater.setDescription(rs.getString("description"));
                    theater.setLatitude(rs.getDouble("latitude"));
                    theater.setLongtitude(rs.getDouble("longtitude"));
                    theater.setOpen_time(rs.getTime("open_time"));
                    theater.setClosing_time(rs.getTime("closing_time"));
                    theater.setActive(rs.getBoolean("isActive"));
                    theater.setDeleted_at(rs.getTimestamp("deleted_at"));
                    theaters.add(theater);
                }
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return theaters;
    }

    public List<Showtimes> getShowtimesByMovieTheaterAndDate(int movieId, int theaterId, LocalDate date) {
        List<Showtimes> showtimes = new ArrayList<>();
        LocalDate today = LocalDate.now();

        // Exclude showtimes that have already started if selecting today.
        java.time.LocalDateTime startDateTime =
            date.equals(today)
                ? java.time.LocalDateTime.now()
                : date.atStartOfDay();
        java.time.LocalDateTime endDateTime = date.plusDays(1).atStartOfDay();
        String sql =
            "SELECT s.* " +
            "FROM Showtimes s " +
            "INNER JOIN Rooms r ON r.room_id = s.room_id " +
            "INNER JOIN Theaters t ON t.theater_id = r.theater_id " +
            "WHERE s.movie_id = ? " +
            "AND t.theater_id = ? " +
            "AND r.isActive = 1 " +
            "AND t.isActive = 1 " +
            "AND t.deleted_at IS NULL " +
            "AND UPPER(s.status) = 'SCHEDULED' " +
            "AND s.deleted_at IS NULL " +
            "AND s.start_at >= ? " +
            "AND s.start_at < ? " +
            "ORDER BY s.start_at ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, movieId);
            stmt.setInt(2, theaterId);
            stmt.setTimestamp(3, Timestamp.valueOf(startDateTime));
            stmt.setTimestamp(4, Timestamp.valueOf(endDateTime));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    showtimes.add(mapShowtime(rs));
                }
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return showtimes;
    }

    public boolean hasUpcomingShowtimes(int roomId) {
        String sql = "SELECT 1 FROM Showtimes WHERE room_id = ? AND start_at > GETDATE() AND deleted_at IS NULL";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasOverlappingShowtime(
            int roomId,
            Timestamp startAt,
            Timestamp endAt) {

        String sql =
                "SELECT 1 " +
                "FROM Showtimes " +
                "WHERE room_id = ? " +
                "AND UPPER(status) = 'SCHEDULED' " +
                "AND deleted_at IS NULL " +
                "AND start_at < ? " +
                "AND end_at > ?";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, roomId);
            stmt.setTimestamp(2, endAt);
            stmt.setTimestamp(3, startAt);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean insertShowtime(int roomId, int movieId, Timestamp startAt, Timestamp endAt, List<Showtime_ticket_types> ticketPrices) {
        String insertShowtimeSql =
                "INSERT INTO Showtimes " +
                "(room_id, movie_id, start_at, end_at, status) " +
                "VALUES (?, ?, ?, ?, 'SCHEDULED')";
        String insertPriceSql =
                "INSERT INTO Showtime_ticket_types " +
                "(showtime_id, ticket_type_id, price) " +
                "VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int showtimeId;
                try (PreparedStatement stmt = conn.prepareStatement(
                        insertShowtimeSql,
                        Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setInt(1, roomId);
                    stmt.setInt(2, movieId);
                    stmt.setTimestamp(3, startAt);
                    stmt.setTimestamp(4, endAt);
                    stmt.executeUpdate();

                    try (ResultSet keys = stmt.getGeneratedKeys()) {
                        if (!keys.next()) {
                            conn.rollback();
                            return false;
                        }
                        showtimeId = keys.getInt(1);
                    }
                }

                try (PreparedStatement stmt = conn.prepareStatement(insertPriceSql)) {
                    for (Showtime_ticket_types ticketPrice : ticketPrices) {
                        stmt.setInt(1, showtimeId);
                        stmt.setInt(2, ticketPrice.getTicket_type_id());
                        stmt.setInt(3, ticketPrice.getPrice());
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                }

                conn.commit();
                return true;
            }
            catch (SQLException e) {
                conn.rollback();
                throw e;
            }
            finally {
                conn.setAutoCommit(true);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasActiveBooking(int showtimeId) {
        String sql =
                "SELECT 1 " +
                "FROM Booking_showtimes b " +
                "WHERE b.showtime_id = ? " +
                "AND " + ACTIVE_BOOKING_CONDITION;
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, showtimeId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean softDeleteShowtime(int showtimeId) {
        String sql =
                "UPDATE Showtimes " +
                "SET deleted_at = GETDATE() " +
                "WHERE showtime_id = ? " +
                "AND deleted_at IS NULL " +
                "AND NOT EXISTS (" +
                "    SELECT 1 FROM Booking_showtimes b " +
                "    WHERE b.showtime_id = Showtimes.showtime_id " +
                "    AND " + ACTIVE_BOOKING_CONDITION + ")";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, showtimeId);
            return stmt.executeUpdate() > 0;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Movies that still have upcoming showtimes in the manager's theaters (filter list)
    public List<Integer> getUpcomingMovieIdsByManager(int userId) {
        List<Integer> movieIds = new ArrayList<>();
        String sql = "SELECT DISTINCT s.movie_id " + MANAGED_UPCOMING_SHOWTIMES;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) movieIds.add(rs.getInt("movie_id"));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return movieIds;
    }

    // One query: {deletable, blocked} upcoming showtimes of a movie, or null on error
    public int[] getMovieShowtimeCountsByManager(int userId, int movieId) {
        String sql =
                "SELECT " +
                "COALESCE(SUM(CASE WHEN x.has_booking = 0 THEN 1 ELSE 0 END), 0) AS deletable, " +
                "COALESCE(SUM(x.has_booking), 0) AS blocked " +
                "FROM (" +
                "    SELECT CASE WHEN EXISTS (" +
                "        SELECT 1 FROM Booking_showtimes b " +
                "        WHERE b.showtime_id = s.showtime_id " +
                "        AND " + ACTIVE_BOOKING_CONDITION + ") THEN 1 ELSE 0 END AS has_booking " +
                MANAGED_UPCOMING_SHOWTIMES +
                "    AND s.movie_id = ?" +
                ") x";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, movieId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new int[] { rs.getInt("deletable"), rs.getInt("blocked") };
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Delete the movie's upcoming showtimes in the
    // manager's theaters that have no active booking. Returns deleted count, -1 on error.
    public int softDeleteMovieShowtimesByManager(int userId, int movieId) {
        String sql =
                "UPDATE s SET deleted_at = GETDATE() " +
                MANAGED_UPCOMING_SHOWTIMES +
                "AND s.movie_id = ? " +
                "AND NOT EXISTS (" +
                "    SELECT 1 FROM Booking_showtimes b " +
                "    WHERE b.showtime_id = s.showtime_id " +
                "    AND " + ACTIVE_BOOKING_CONDITION + ")";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, movieId);
            return stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private Showtimes mapShowtime(ResultSet rs) throws SQLException {
        Showtimes showtime = new Showtimes();
        showtime.setShowtime_id(rs.getInt("showtime_id"));
        showtime.setRoom_id(rs.getInt("room_id"));
        showtime.setMovie_id(rs.getInt("movie_id"));
        showtime.setStart_at(rs.getTimestamp("start_at"));
        showtime.setEnd_at(rs.getTimestamp("end_at"));
        showtime.setStatus(rs.getString("status"));
        showtime.setDeleted_at(rs.getTimestamp("deleted_at"));
        return showtime;
    }
}