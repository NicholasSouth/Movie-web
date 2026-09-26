package com.movieweb.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.movieweb.model.Theaters;
import com.movieweb.util.DBConnection;

public class TheatersDAO 
{
    public Theaters getTheaterById(int theater_id) 
    {
        String sql = "SELECT * FROM Theaters WHERE theater_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            stmt.setInt(1, theater_id);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) 
            {
                return mapTheater(rs);
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        
        return null;
    }

    public List<Theaters> getTheaters() {
        List<Theaters> theaters = new ArrayList<>();
        String sql =
            "SELECT * " +
                    "FROM Theaters " +
                    "WHERE isActive = 1 " +
                    "and deleted_at IS NULL";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                theaters.add(mapTheater(rs));
            }
        }
        catch (SQLException e) {
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

        double latitude = rs.getDouble("latitude");
        if (rs.wasNull()) {
            latitude = Double.NaN;
        }
        theater.setLatitude(latitude);

        double longtitude = rs.getDouble("longtitude");
        if (rs.wasNull()) {
            longtitude = Double.NaN;
        }
        theater.setLongtitude(longtitude);

        theater.setOpen_time(rs.getString("open_time"));
        theater.setClosing_time(rs.getString("closing_time"));
        theater.setActive(rs.getBoolean("isActive"));
        theater.setDeleted_at(rs.getString("deleted_at"));
        return theater;
    }
}