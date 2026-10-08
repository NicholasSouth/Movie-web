package com.movieweb.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.movieweb.model.Theaters;
import com.movieweb.util.DBConnection;

public class TheatersDAO {
    public Theaters getTheaterById(int theater_id) {
        String sql = "SELECT * FROM Theaters WHERE theater_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, theater_id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapTheater(rs);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Theaters> getAllTheaters() {
        List<Theaters> theaters = new ArrayList<>();
        String sql = "SELECT * FROM Theaters WHERE isActive = 1 AND deleted_at IS NULL";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                theaters.add(mapTheater(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return theaters;
    }

    // Active theaters that the manager is not managing yet
    public List<Theaters> getTheatersNotManagedByUser(int user_id) {
        List<Theaters> theaters = new ArrayList<>();
        String sql =
                "SELECT t.* " +
                "FROM Theaters t " +
                "WHERE t.isActive = 1 " +
                "AND t.deleted_at IS NULL " +
                "AND NOT EXISTS (SELECT 1 FROM Managers m " +
                "                WHERE m.theater_id = t.theater_id AND m.user_id = ?) " +
                "ORDER BY t.theater_name ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, user_id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) theaters.add(mapTheater(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return theaters;
    }

    private Theaters mapTheater(ResultSet rs) throws SQLException {
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

        return theater;
    }
}