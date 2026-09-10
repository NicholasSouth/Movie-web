package com.movieweb.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.movieweb.model.Seats;
import com.movieweb.util.DBConnection;

public class SeatsDAO 
{
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
}