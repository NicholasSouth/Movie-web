package com.movieweb.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.movieweb.model.Showtimes;
import com.movieweb.util.DBConnection;

public class ShowtimesDAO 
{
    public Showtimes getShowtimeById(int showtime_id) 
    {
        String sql = "SELECT * FROM Showtimes WHERE showtime_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            stmt.setInt(1, showtime_id);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) 
            {
                Showtimes showtime = new Showtimes();
                showtime.setShowtime_id(rs.getInt("showtime_id"));
                showtime.setRoom_id(rs.getInt("room_id"));
                showtime.setMovie_id(rs.getInt("movie_id"));
                showtime.setStart_at(rs.getString("start_at"));
                showtime.setEnd_at(rs.getString("end_at"));
                showtime.setStatus(rs.getString("status"));
                
                return showtime;
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        
        return null;
    }
}