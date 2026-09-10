package com.movieweb.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
                Theaters theater = new Theaters();
                theater.setTheater_id(rs.getInt("theater_id"));
                theater.setTheater_name(rs.getString("theater_name"));
                theater.setTheater_address(rs.getString("theater_address"));
                theater.setTheater_image_path(rs.getString("theater_image_path"));
                theater.setDescription(rs.getString("description"));
                theater.setLatitude(rs.getDouble("latitude"));
                theater.setLongtitude(rs.getDouble("longtitude"));
                theater.setOpen_time(rs.getString("open_time"));
                theater.setClosing_time(rs.getString("closing_time"));
                theater.setActive(rs.getBoolean("isActive"));
                theater.setDeleted_at(rs.getString("deleted_at"));
                
                return theater;
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        
        return null;
    }
}