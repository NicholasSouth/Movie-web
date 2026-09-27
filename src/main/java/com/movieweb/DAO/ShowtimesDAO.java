package com.movieweb.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.movieweb.model.Showtimes;
import com.movieweb.model.Theaters;
import com.movieweb.util.DBConnection;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

import java.util.LinkedHashMap;

public class ShowtimesDAO {
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
    public List<Showtimes> getShowtimesByTheaterAndDate(
            int theaterId,
            LocalDate date) {
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
    
    private Showtimes mapShowtime(ResultSet rs) throws SQLException {
        Showtimes showtime = new Showtimes();
        showtime.setShowtime_id(rs.getInt("showtime_id"));
        showtime.setRoom_id(rs.getInt("room_id"));
        showtime.setMovie_id(rs.getInt("movie_id"));
        showtime.setStart_at(rs.getTimestamp("start_at"));
        showtime.setEnd_at(rs.getTimestamp("end_at"));
        return showtime;
    }
}