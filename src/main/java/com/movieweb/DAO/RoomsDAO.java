package com.movieweb.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.movieweb.model.Rooms;
import com.movieweb.util.DBConnection;

public class RoomsDAO 
{
    public Rooms getRoomById(int room_id) 
    {
        String sql = "SELECT * FROM Rooms WHERE room_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            stmt.setInt(1, room_id);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) 
            {
                Rooms room = new Rooms();
                room.setRoom_id(rs.getInt("room_id"));
                room.setTheater_id(rs.getInt("theater_id"));
                room.setRoom_name(rs.getString("room_name"));
                room.setRoom_type_id(rs.getInt("room_type_id"));
                room.setActive(rs.getBoolean("isActive"));
                
                return room;
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        
        return null;
    }
}